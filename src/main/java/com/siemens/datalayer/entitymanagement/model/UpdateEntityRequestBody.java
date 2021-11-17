package com.siemens.datalayer.entitymanagement.model;

import java.util.HashMap;
import java.util.Map;

public class UpdateEntityRequestBody {
	
	private String id;
	
	private String label;
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	/* private String nodeType;

	private String connectedRelationNumber; */

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
	
	/* public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getConnectedRelationNumber(){
		return this.connectedRelationNumber;
	}

	public void setConnectedRelationNumber(String connectedRelationNumber) {
		this.connectedRelationNumber = connectedRelationNumber;
	} */
	
	public boolean equals(UpdateEntityRequestBody other) {
		
		return this.id.equals(other.id) 
			&& this.label.equals(other.label)
			&& this.properties.equals(other.properties);
			// && this.nodeType.equals(other.nodeType)
			// && this.connectedRelationNumber.equals(other.connectedRelationNumber);
	}

}
