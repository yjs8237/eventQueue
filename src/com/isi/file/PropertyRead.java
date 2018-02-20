package com.isi.file;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
*
* @author greatyun
*/
public class PropertyRead {

//	public static final String propFilePath = System.getProperty("user.dir") + "\\conf\\servers.properties";
//	public static final String propFilePath = "C:\\Development\\SRC\\workspace_pwc\\ISXML\\conf\\servers.properties";
	public static final String propFilePath = "C:\\Development\\SRC\\ISXML\\conf\\servers.properties";
	
	private  Map<String, String> iniConfigMap = null;
	private  boolean bPropertyStatus = false;
	private Properties mProperties = null;
	private static PropertyRead pr = new PropertyRead(); 
	
	/**
	 * Creates a new instance of ConfigRead
	 */
	
    public static PropertyRead getInstance () {
		if(pr == null){
			pr = new PropertyRead();
		}
		return pr;
    }
	
	private PropertyRead() {
		System.out.println("############## PropertyRead 호출 ################");
		iniConfigMap = new HashMap<String, String>();
		System.out.println("servers.properties path -> " + propFilePath);
		read(propFilePath);
	}
	
	private  Properties readProperties(String configFile) throws IOException {
		Properties tempProperties = new Properties();
		FileInputStream in = new FileInputStream(configFile);
		tempProperties.load(in);
		in.close();
		return tempProperties;
	}
	
	public String toString() {
		return iniConfigMap.toString();
	}

	public  synchronized void read(String path) {
		// TODO Auto-generated method stub
		bPropertyStatus = false;
		try {
			File file = new File(path);
			if (file.exists()) {
				mProperties = readProperties(path);
				
				Set propSet = mProperties.keySet();
				Iterator iter = propSet.iterator();
				System.out.println("##########  Reading Properties file  ##########");
				while(iter.hasNext()){
					String key = (String) iter.next();
					System.out.println(key + " : " + mProperties.getProperty(key));
//					log.standLog("", "read", key + " : " + mProperties.getProperty(key));
					iniConfigMap.put(key, mProperties.getProperty(key));
				}
				bPropertyStatus = true;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public synchronized boolean isRead() {
		// TODO Auto-generated method stub
		return bPropertyStatus;
	}

		// TODO Auto-generated method stub
	public String getValue(String key) {
		if(iniConfigMap != null){
			if(iniConfigMap.get(key) != null)
				return iniConfigMap.get(key);
			else
				return "";
		}
		return "";
	}

}
