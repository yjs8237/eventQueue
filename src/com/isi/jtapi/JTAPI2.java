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
	private Map m_TerminalMap = Collections.synchronizedMap(new HashMap());
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
			// 원복
//			providerString = cmIP + ";login=" + "test_user" + ";passwd=" + "dkdlvlxl123$";

			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "serviceStart",
					"[****] start JTAPI provider - " + providerString);
			m_Provider = peer.getProvider(providerString);
			m_Provider.addObserver(this);
			m_conditionInSvc.waitTrue();

			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "serviceStart",
					"[****] start JTAPI provider success");

		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "ServiceStop", sw.toString());
			return RESULT.RTN_EXCEPTION;
		} finally {
			// Provider 서비스 종료
			m_Provider.shutdown();
		}
		return RESULT.RTN_SUCCESS;
	}
	
	
	
	public CiscoTerminal getTerminal(String mac_address) {
		CiscoTerminal terminal = null;
		try {
			terminal = (CiscoTerminal) m_Provider.getTerminal(mac_address);
		} catch (Exception e) {
			return null;
		}
		return terminal;
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
		
		try {

			addr = (CiscoAddress) m_Provider.getAddress(aDn);
			//Terminal terminal = m_Provider.getTerminal(aDn);
			/*
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
				resultVO.setCode(200);
				resultVO.setMessage("success");
				return resultVO;
			}
			*/
			// 기존에 이미 모니터링이 걸려 있으면 계속하지 않는다.
			dev = getLine(aDn);

			if (dev != null) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] [ERROR] MonitorStart - already addred device ");
				
				addr.removeCallObserver(dev);
				addr.removeObserver(dev);

				Terminal[] termarray = addr.getTerminals();
				for (int i = 0; i < termarray.length; i++) {
					CiscoTerminal term = (CiscoTerminal) termarray[i];
					term.removeObserver(dev);
				}
				
				m_DevMap.remove(aDn);
				
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] MonitorStart - Address remove observer ");
				
			}
			
			addr = (CiscoAddress) m_Provider.getAddress(aDn);
			
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
			
			for (int i = 0; i < termarray.length; i++) {
				m_Log.server(LOGTYPE.STAND_LOG, "MonitorStart",
						"[" + aDn + "] 터미널 등록 " + termarray[i].getName());
			}
			
			m_TerminalMap.put(aDn, termarray);	// 내 당겨받기 그룹에 사용될 Terminal 객체 
			
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
			sw = new StringWriter();
			pw = new PrintWriter(sw);
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
 
	@Override
	public JTapiResultVO pickupCall(String myExtension, String pickupExtension) {
		// TODO Auto-generated method stub
		/*
		loginUser.sendCloseMessage(null);
        String userID = request.getParameter("userID");
        UCUser user = adminController.getUser(userID);
        CiscoTerminal term = user.getTerminal();
        try
        {
            CiscoTerminalConnection target = null;
            TerminalConnection tc[] = term.getTerminalConnections();
            for(int i = 0; i < tc.length; i++)
                if(((CiscoTerminalConnection)tc[i]).getCallControlState() == 97)
                    target = (CiscoTerminalConnection)tc[i];

            if(target != null)
            {
                CiscoConnection cc = (CiscoConnection)target.getConnection();
                cc.redirect(loginUser.getPhone());
            }
        }
		*/
		
		int returnCode = RESULT.RTN_SUCCESS;
		String returnMessage = "success";
//		Terminal terminal = m_Provider.getTerminal(aDn);
		JTapiResultVO resultVO = new JTapiResultVO();
		
//		addr = (CiscoAddress) m_Provider.getAddress(aDn);
		
		try {
		
			CiscoAddress addr = (CiscoAddress) m_Provider.getAddress(pickupExtension);
			Terminal[] terminalArr = addr.getTerminals();
			
			if(terminalArr == null || terminalArr.length == 0) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "NOT LOGIN " + pickupExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
		
		
			for (int i = 0; i < terminalArr.length; i++) {
				Terminal terminal = terminalArr[i];
				CiscoTerminalConnection target = null;
	            TerminalConnection tc[] = terminal.getTerminalConnections();
	            for(int j = 0; j < tc.length; j++) {
	            	// 터미널 Connection 의 CallControlState가 Ringing 일 경우
	            	if(((CiscoTerminalConnection)tc[j]).getCallControlState() == 97) {
	            		target = (CiscoTerminalConnection)tc[j];
	            	}
	            }
	            if(target != null)
	            {
	            	
	                CiscoConnection cc = (CiscoConnection)target.getConnection();
	                // 해당 콜의 나의 내선번호로 Redirect
	                cc.redirect(myExtension);
	                returnCode = RESULT.RTN_SUCCESS;
	                returnMessage = "success";
	                resultVO.setCode(returnCode);
					resultVO.setMessage(returnMessage);
					return resultVO;
	            }
			}
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.server(LOGTYPE.ERR_LOG, "pickupCall", sw.toString());
			returnCode = RESULT.RTN_EXCEPTION;
			returnMessage = e.getLocalizedMessage();
		} finally {
			resultVO.setCode(returnCode);
			resultVO.setMessage(returnMessage);
		}
		
		return resultVO;
	
	}

	@Override
	public JTapiResultVO makeCall(String myExtension, String callingNumber ,String mac_address , String requestID) {
		// TODO Auto-generated method stub
		JTapiResultVO resultVO = new JTapiResultVO();
		
		int returnCode = RESULT.RTN_SUCCESS;
		String returnMessage = "success";
		
		try {
			
			if(myExtension == null || myExtension.isEmpty()) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "myExtension is null " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			if(mac_address == null || mac_address.isEmpty()) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "mac_address is null " + mac_address;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			Terminal terminal = m_Provider.getTerminal(mac_address);
			
			if(terminal == null) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "terminal is null " + mac_address;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			
			Call call = terminal.getProvider().createCall();
			Address addresses[] = terminal.getAddresses();
			Address targetAddress = null;
			
			// 내선 기준으로 make call ( 같은 내선의 n 개의 Device 일 경우 모든 Device가 makecall 될기야..)
			
			for (int j = 0; j < addresses.length; j++) {
				
				m_Log.server(requestID ,LOGTYPE.STAND_LOG, "makeCall", "myExtension["+myExtension+"] callingNumber["+callingNumber+"] address : " + addresses[j].getName() + " , myExtension : " + myExtension);
				
				if (!addresses[j].getName().equals(myExtension)) {
					continue;
				}
				targetAddress = addresses[j];
				break;
			}
			
			if (targetAddress != null) {
				if(callingNumber != null && callingNumber.length() > 6) {
					callingNumber = callingNumber.replaceAll("#", "");
					callingNumber = callingNumber.replaceAll("-", "");
					
					if(callingNumber.startsWith("02709") || callingNumber.startsWith("023781")) {
						
						callingNumber = checkInternalNumber(callingNumber);
						
					} else {
						callingNumber = "#" + callingNumber;
					}
					
				}
				
				returnMessage = "success";
				m_Log.server(requestID,LOGTYPE.STAND_LOG, "makeCall", "call Connect 시도 MyExtension[" + myExtension + "] callingNumber[" + callingNumber + "]");
				call.connect(terminal, targetAddress, callingNumber);
				
				returnCode = RESULT.RTN_SUCCESS;
				returnMessage = "success";
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
				
			} else {
				m_Log.server(requestID,LOGTYPE.STAND_LOG, "makeCall", "targetAddress is null ");
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "targetAddress is null " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			/*
			CiscoAddress addr = (CiscoAddress) m_Provider.getAddress(myExtension);
			Terminal[] terminalArr = addr.getTerminals();
			
			if(terminalArr == null || terminalArr.length == 0) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "NOT LOGIN " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			if(myExtension == null || myExtension.isEmpty()) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "myExtension is null " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			
			for (int i = 0; i < terminalArr.length; i++) {
				
				Terminal terminal = terminalArr[i];
				
				m_Log.server(LOGTYPE.STAND_LOG, "makeCall", "myExtension["+myExtension+"] callingNumber["+callingNumber+"] terminal name : " + terminal.getName() + " , mac_address : " + mac_address);
				
				if(mac_address != null && !mac_address.isEmpty()) {
					// mac_address 까지 비교해야 한다면 
					if(!mac_address.equals(terminal.getName())){
						returnCode = RESULT.RTN_EXCEPTION;
						returnMessage = "mac_address is not same " + mac_address;
						continue;
					}	
				} 
				
				Call call = terminal.getProvider().createCall();
				Address addresses[] = terminal.getAddresses();
				Address targetAddress = null;
				
				
				// 내선 기준으로 make call ( 같은 내선의 n 개의 Device 일 경우 모든 Device가 makecall 될기야..)
				
				for (int j = 0; j < addresses.length; j++) {
					
					m_Log.server(LOGTYPE.STAND_LOG, "makeCall", "myExtension["+myExtension+"] callingNumber["+callingNumber+"] address : " + addresses[j].getName() + " , myExtension : " + myExtension);
					
					if (!addresses[j].getName().equals(myExtension)) {
						continue;
					}
					targetAddress = addresses[j];
					break;
				}
				
				if (targetAddress != null) {
					if(callingNumber != null && callingNumber.length() > 6) {
						callingNumber = callingNumber.replaceAll("#", "");
						callingNumber = callingNumber.replaceAll("-", "");
						
						if(callingNumber.startsWith("02709") || callingNumber.startsWith("023781")) {
							
							callingNumber = checkInternalNumber(callingNumber);
							
						} else {
							callingNumber = "#" + callingNumber;
						}
						
					}
					returnMessage = "success";
					m_Log.server(LOGTYPE.STAND_LOG, "makeCall", "call Connect 시도 MyExtension[" + myExtension + "] callingNumber[" + callingNumber + "]");
					call.connect(terminal, targetAddress, callingNumber);
					
					returnCode = RESULT.RTN_SUCCESS;
					returnMessage = "success";
					resultVO.setCode(returnCode);
					resultVO.setMessage(returnMessage);
					return resultVO;
					
				} else {
					System.out.println("targetAddress is null "  );
					returnCode = RESULT.RTN_EXCEPTION;
					returnMessage = "targetAddress is null " + myExtension;
					resultVO.setCode(returnCode);
					resultVO.setMessage(returnMessage);
					return resultVO;
				}
				
			}
			*/
			
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.server(requestID,  LOGTYPE.ERR_LOG, "makeCall", sw.toString());
			returnCode = RESULT.RTN_EXCEPTION;
			returnMessage = e.getLocalizedMessage();
		} finally {
			resultVO.setCode(returnCode);
			resultVO.setMessage(returnMessage);
		}
		return resultVO;
	}

	private String checkInternalNumber(String callingNumber) {
		// TODO Auto-generated method stub
		if(callingNumber.startsWith("02709")) {
			String lastNumber = callingNumber.substring(callingNumber.length()-4, callingNumber.length());
			if(lastNumber.startsWith("02") || lastNumber.startsWith("03") || lastNumber.startsWith("04") ||
					lastNumber.startsWith("05") || lastNumber.startsWith("06") || lastNumber.startsWith("07") ||
					lastNumber.startsWith("08") || lastNumber.startsWith("09") || lastNumber.startsWith("33") ||
					lastNumber.startsWith("40") || lastNumber.startsWith("47") || lastNumber.startsWith("64") ||
					lastNumber.startsWith("70") || lastNumber.startsWith("79") || lastNumber.startsWith("80") ||
					lastNumber.startsWith("81") || lastNumber.startsWith("82") || lastNumber.startsWith("83") ||
					lastNumber.startsWith("84") || lastNumber.startsWith("85") || lastNumber.startsWith("87") ||
					lastNumber.startsWith("88") || lastNumber.startsWith("89")) {
				return lastNumber;
			}
		} else {
			String lastNumber = callingNumber.substring(callingNumber.length()-4, callingNumber.length());
			if(lastNumber.startsWith("00") || lastNumber.startsWith("01") || lastNumber.startsWith("14") ||
					lastNumber.startsWith("15") || lastNumber.startsWith("16") || lastNumber.startsWith("17") ||
					lastNumber.startsWith("23") || lastNumber.startsWith("25") || lastNumber.startsWith("30") ||
					lastNumber.startsWith("31") || lastNumber.startsWith("32") || lastNumber.startsWith("34") ||
					lastNumber.startsWith("90") || lastNumber.startsWith("91") || lastNumber.startsWith("92") ||
					lastNumber.startsWith("93") || lastNumber.startsWith("94") || lastNumber.startsWith("95") ||
					lastNumber.startsWith("96") || lastNumber.startsWith("97") || lastNumber.startsWith("98") ||
					lastNumber.startsWith("99")) {
				return lastNumber;
			}
		}
		return callingNumber;
	}

	@Override
	public JTapiResultVO stopCall(String myExtension , String requestID) {
		// TODO Auto-generated method stub
		JTapiResultVO resultVO = new JTapiResultVO();
		
		int returnCode = RESULT.RTN_SUCCESS;
		String returnMessage = "success";
		
		try {
			
			if(myExtension == null || myExtension.isEmpty()) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "myExtension is null " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			
//			Terminal[] terminalArr = (Terminal[]) m_TerminalMap.get(myExtension);
			CiscoAddress addr = (CiscoAddress) m_Provider.getAddress(myExtension);
			Terminal[] terminalArr = addr.getTerminals();
			
			
			if(terminalArr == null || terminalArr.length == 0) {
				returnCode = RESULT.RTN_EXCEPTION;
				returnMessage = "NOT LOGIN " + myExtension;
				resultVO.setCode(returnCode);
				resultVO.setMessage(returnMessage);
				return resultVO;
			}
			
			// 
			/*
			for (int i = 0; i < terminalArr.length; i++) {
				CiscoTerminal ciscoTerminal = (CiscoTerminal) terminalArr[i];
				TerminalConnection tcs[] = ciscoTerminal.getTerminalConnections();
                if(tcs != null)
                {
                    for(int j = 0; j < tcs.length; j++)
                    {
                        CiscoCall call = (CiscoCall)tcs[j].getConnection().getCall();
                        
                        String callingAddress = call.getCallingAddress().toString();
                        m_Log.server(LOGTYPE.STAND_LOG, "stopCall", "call Hanhup 시도 MyExtension[" + myExtension + "] callingAddress [" + callingAddress+"]");
                        
                        if(callingAddress.equals(myExtension))
                        {
                            tcs[j].getConnection().disconnect();
                            m_Log.server(LOGTYPE.STAND_LOG, "stopCall", "call Hanhup Disconnection");
                        }
                    }

                }
				
			}
			*/
			
			
			for (int i = 0; i < terminalArr.length; i++) {
				
				Terminal terminal = terminalArr[i];
				
				TerminalConnection tcs[] = terminal.getTerminalConnections();
                if(tcs != null)
                {
                    for(int j = 0; j < tcs.length; j++)
                    {
                        CiscoCall call = (CiscoCall)tcs[j].getConnection().getCall();
                        
                        String callingAddress = call.getCallingAddress().toString();
                        
                        if(callingAddress.equals(myExtension))
                        {
                        	m_Log.server(requestID ,LOGTYPE.STAND_LOG, "stopCall", "call Hangup 시도 MyExtension[" + myExtension + "] callingAddress [" + callingAddress+"]");
                            tcs[j].getConnection().disconnect();
                            m_Log.server(requestID ,LOGTYPE.STAND_LOG, "stopCall", "call Hangup Disconnection");
                            returnCode = RESULT.RTN_SUCCESS;
                            returnMessage = "success";
                            resultVO.setCode(returnCode);
                			resultVO.setMessage(returnMessage);
                			return resultVO;
                        }
                    }

                }
				
				
			}
			
			
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.server(requestID ,LOGTYPE.ERR_LOG, "stopCall", sw.toString());
			returnCode = RESULT.RTN_EXCEPTION;
			returnMessage = e.getLocalizedMessage();
		} finally {
			resultVO.setCode(returnCode);
			resultVO.setMessage(returnMessage);
		}
		return resultVO;
	}

}
