package com.isi.vo;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatusVO extends BaseVO {
	private String extension;
	private List<String> extensionList;
	
	public DeviceStatusVO() {
		extensionList = new ArrayList<>();
	}
	
	public List<String> getExtensionList() {
		return extensionList;
	}

	public void setExtensionList(List<String> extensionList) {
		this.extensionList = extensionList;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		extensionList.add(extension);
		this.extension = extension;
	}
	
}
