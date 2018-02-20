package com.isi.jtapi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.telephony.*;
import javax.telephony.events.ProvEv;
import javax.telephony.events.ProvInServiceEv;
import javax.telephony.events.ProvOutOfServiceEv;

import com.cisco.cti.util.Condition;
import com.cisco.jtapi.*;
import com.cisco.jtapi.extensions.*;
import com.isi.axl.CiscoPhoneInfo;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.constans.SVCTYPE;
import com.isi.data.Employees;
import com.isi.db.JDatabase;
import com.isi.event.IEvt;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.process.IQueue;
import com.isi.vo.DeviceVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.JTapiResultVO;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class JTAPI2 implements IJTAPI, ProviderObserver {

	private Map m_DevMap = Collections.synchronizedMap(new HashMap());
	private ILog m_Log = null;// Logging.getInstance(Logging.JTAPI);
	private ILog m_PackLog = null;
	private Condition m_conditionInSvc = new Condition();
	private Provider m_Provider = null;
	private IQueue m_Queue = null;
	private int m_CMID;
	private PrintWriter pw;
	private StringWriter sw;

	public JTAPI2(int cmid, IQueue aQueue) {

		m_Log = new GLogWriter();
		m_CMID = cmid;

		sw = new StringWriter();
		pw = new PrintWriter(sw);

		if (aQueue == null) {
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "JTAPI2",
					"**** [ERROR] JTAPI event queue is null");
		}
		m_Queue = aQueue;
	}

	public JTAPI2(int cmid) {

		m_Log = new GLogWriter();
		m_CMID = cmid;

		sw = new StringWriter();
		pw = new PrintWriter(sw);

	}

	public Provider getProvider() {
		return m_Provider;
	}

	public int getCMID() {
		return m_CMID;
	}

	public ILog getLog() {
		return m_Log;
	}

	public ILog getPackLog() {
		return m_PackLog;
	}

	// 받은 이벤트를 처리한다
	// 큐에넣고 다른 Thread에서 처리
	public void ReceiveEvent(IEvt evt) {
		try {
			m_Queue.put(evt);
		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "ReceiveEvent", sw.toString());
			// m_Log.server("[****] ReceiveEvent Fail ", e);
		}
	}

	public int serviceStart(String cmIP, String cmID, String cmPwd) {

		String providerString;
		try {
			JtapiPeer peer = JtapiPeerFactory.getJtapiPeer(null);

			providerString = cmIP + ";login=" + cmID + ";passwd=" + cmPwd;

			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "serviceStart",
					"[****] start JTAPI provider - " + providerString);
			m_Provider = peer.getProvider(providerString);
			m_Provider.addObserver(this);
			m_conditionInSvc.waitTrue();

			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "serviceStart",
					"[****] start JTAPI provider success");

		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "serviceStart", sw.toString());
			return RESULT.RTN_EXCEPTION;
		}
		return RESULT.RTN_SUCCESS;
	}

	public int ServiceStop() {

		try {

			m_Log.config(LOGTYPE.STAND_LOG, "ServiceStop", "JTAPI Service Stop!");

			Collection col = m_DevMap.values();
			Iterator it = col.iterator();

			while (it.hasNext()) {
				DevEvt line = (DevEvt) it.next();
				if (line == null) {
					break;
				} else {
					this.MonitorStop(line.getDn());
				}
			}

			// 모든 Device 삭제
			m_DevMap.clear();
			m_Log.config(LOGTYPE.STAND_LOG, "ServiceStop", "[****] stop JTAPI provider success ");
			// m_Log.server("[****] stop JTAPI provider success ");

		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "ServiceStop", sw.toString());
			return RESULT.RTN_EXCEPTION;
		} finally {
			// Provider 서비스 종료
			m_Provider.shutdown();
		}
		return RESULT.RTN_SUCCESS;
	}

	public int MonitorAllStart(CiscoPhoneInfo address) {
		
		Address[] addrArray = null;
		Address addr = null;
		String ip = null, model = "", terminal = "";
		try {
			addrArray = m_Provider.getAddresses();
			if (addrArray == null)
				return RESULT.ERR_COMM_NO_GETADDRESS;
			for (int i = 0; i < addrArray.length; i++) {
				addr = addrArray[i];
				if (addr != null) {
					if (address != null) {
						ip = address.getIPbyDeviceNumber(addr.getName());
						if (ip == null) {
							ip = "";
						} else {
							model = address.getModelByIP(ip);
							terminal = address.getTermNamebyIP(ip);
						}
					} else {
						ip = "";
					}
					System.out.println("MonitorStart -> " + addr.getName() + " , " + ip + " , " + model);
					MonitorStart(addr.getName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "MonitorAllStart", sw.toString());
			return RESULT.RTN_EXCEPTION;
		}
		return RESULT.RTN_SUCCESS;
	}

	public int MonitorAllStop() {
		Address[] addrArray = null;
		Address addr = null;

		try {
			addrArray = m_Provider.getAddresses();
			for (int i = 0; i < addrArray.length; i++) {
				addr = addrArray[i];
				if (addr != null) {
					MonitorStop(addr.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "MonitorAllStop", sw.toString());
			return RESULT.RTN_EXCEPTION;
		}
		return RESULT.RTN_SUCCESS;
	}

	public JTapiResultVO MonitorStart(String aDn) {
		
		JTapiResultVO resultVO = new JTapiResultVO();
		
		CiscoAddress addr = null;
		DevEvt dev = null; // 장이 이벤트 객체
		// Device device = null; // 장치데이터
		try {

			addr = (CiscoAddress) m_Provider.getAddress(aDn);

			if (addr == null) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] [ERROR] MonitorStart - unregistered device ");
				// m_Log.server("[" + aDn + "] [ERROR] MonitorStart -
				// unregistered device ");
				resultVO.setCode(RESULT.ERR_DEV_UNREG);
				resultVO.setMessage("Device Unregistered");
				return resultVO;
			}
			if (addr.getState() == CiscoAddress.IN_SERVICE) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] [ERROR] MonitorStart- already registered device ");
				resultVO.setCode(RESULT.ERR_DEV_ALREADY_LOGIN);
				resultVO.setMessage("Already Login");
				return resultVO;
			}
			
			// 기존에 이미 모니터링이 걸려 있으면 계속하지 않는다.
			dev = getLine(aDn);

			if (dev != null) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] [ERROR] MonitorStart - already addred device ");
				// m_Log.server("[" + aDn + "] [ERROR] MonitorStart - already
				// addred device ");
				resultVO.setCode(RESULT.ERR_DEV_ALREADY_LOGIN);
				resultVO.setMessage("Already Login");
				return resultVO;
			}

			// 모니터링이 성공하면 장치를 추가
			// device = JCtiData.getData().addDevice(m_CMID, aDn); // 2016-04-08
			// 전화기 IP, Model 정보 Add 가능하도록 수정
			// ## 주석
			// device = JCtiData.getData().addDevice(m_CMID, aDn, aIP, aModel);

			dev = new DevEvt(addr.getName(), this);

			// 해당 Device에 모니터링을 시작한다.
			addr.addCallObserver(dev);
			addr.addObserver(dev);

			Terminal[] termarray = addr.getTerminals();

			// DN과 관련된 모든 터미날에 모니터링을 시작한다.
			for (int i = 0; i < termarray.length; i++) {
				CiscoTerminal term = (CiscoTerminal) termarray[i];

				if (term instanceof CiscoTerminal) {
					CiscoTermEvFilter filter = term.getFilter();
					filter.setRTPEventsEnabled(false);
					filter.setDeviceStateActiveEvFilter(true);
					filter.setDeviceStateAlertingEvFilter(true);
					filter.setDeviceStateHeldEvFilter(true);
					filter.setDeviceStateIdleEvFilter(true);
					term.setFilter(filter);
				}

				// TerminalObserver 추가
				term.addObserver(dev);
				// ## 주석
				// device.addTerminal(term.getName());
			}

			m_DevMap.put(addr.getName(), dev);

			m_Log.config(LOGTYPE.STAND_LOG, "MonitorStart", "[" + aDn + "] monitor start success ");

		} catch (PlatformExceptionImpl pe) {
			pe.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "MonitorStart", sw.toString());
			
			if (PlatformExceptionImpl.CTIERR_LINE_RESTRICTED == pe.getErrorCode()) {
				// m_Log.warning("[" + aDn + "] monitor start fail - line
				// restricted");
			} else {
				// m_Log.warning("[" + aDn + "] monitor start fail ", pe);
			}
			resultVO.setCode(RESULT.RTN_EXCEPTION);
			resultVO.setMessage(pe.getLocalizedMessage());
			return resultVO;
		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "MonitorStart", sw.toString());
			// m_Log.server("[" + aDn + "] monitor start fail ", e);
			resultVO.setCode(RESULT.RTN_EXCEPTION);
			resultVO.setMessage(e.getLocalizedMessage());
			return resultVO;
		}
		
		resultVO.setCode(RESULT.RTN_SUCCESS);
		resultVO.setMessage("success");
		return resultVO;
	}

	public JTapiResultVO MonitorStop(String aDn) {

		JTapiResultVO resultVO = new JTapiResultVO();
		Address addr = null;
		DevEvt dev = null;
		try {
			addr = m_Provider.getAddress(aDn);

			if (addr == null) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStop",
						"[" + aDn + "] [ERROR] MonitorStop - unregistered device ");
				resultVO.setCode(RESULT.ERR_DEV_UNREG);
				resultVO.setMessage("Device Unregistered");
				return resultVO;
			}

			dev = getLine(aDn);

			if (dev == null) {
				resultVO.setCode(RESULT.ERR_DEV_ISNOT_LOGIN);
				resultVO.setMessage("Not Login");
				return resultVO;
			}
			
			addr.removeCallObserver(dev);
			addr.removeObserver(dev);

			Terminal[] termarray = addr.getTerminals();
			for (int i = 0; i < termarray.length; i++) {
				CiscoTerminal term = (CiscoTerminal) termarray[i];
				term.removeObserver(dev);
			}

			if (m_DevMap.remove(aDn) == null) {

				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStop", "[" + aDn + "] [ERROR] MonitorStop cat't remove item ");
				resultVO.setCode(RESULT.RTN_EXCEPTION);
				resultVO.setMessage("Cannot remove HashMap");
				return resultVO;
			}
			
			m_Log.server(LOGTYPE.STAND_LOG, "MonitorStop", "[" + aDn + "] success monitor stop ");

			// 모니터링이 종료되면 장치를 삭제한다.
			// ## 주석
			// JCtiData.getData().removeDevice(aDn);

		} catch (Exception e) {
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "MonitorStop", sw.toString());
			resultVO.setCode(RESULT.RTN_EXCEPTION);
			resultVO.setMessage(e.getLocalizedMessage());
			return resultVO;
		}
		resultVO.setCode(RESULT.RTN_SUCCESS);
		resultVO.setMessage("success");
		return resultVO;
	}

	public DevEvt getLine(String dn) {
		DevEvt dev = (DevEvt) m_DevMap.get(dn);
		if (dev == null) {
			return null;
		} else {
			return dev;
		}
	}

	@Override
	public void providerChangedEvent(ProvEv[] eventList) {
		// TODO Auto-generated method stub
		if (eventList != null) {
			for (int i = 0; i < eventList.length; i++) {
				if (eventList[i] instanceof ProvInServiceEv) {
					m_conditionInSvc.set();
				} else if (eventList[i] instanceof ProvOutOfServiceEv) {
					// Terminal의 상태를 이미 보냈기 때문에 추가적이 설정은 보내지 않는다.
					// Provider 상태변경 이벤트.. 여기서 CM 이중화 로직 구현

				}
			}
		}
	}

}
