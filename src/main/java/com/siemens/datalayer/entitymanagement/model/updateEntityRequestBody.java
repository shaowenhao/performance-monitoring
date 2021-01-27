package com.siemens.datalayer.entitymanagement.model;

import java.util.HashMap;
import java.util.Map;

public class updateEntityRequestBody {
	
	private String id;
	
	private String label;
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private String nodeType;
	
	private String location;	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Map<String, String> getProperties() {
		return this.properties;
	}
	
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}	
	
	public boolean equals(updateEntityRequestBody other) {	
		
		return this.id.equals(other.id) 
			&& this.label.equals(other.label) 
			&& this.nodeType.equals(other.nodeType) 
			&& this.location.equals(other.location) 
			&& this.properties.equals(other.properties);
	}

}
