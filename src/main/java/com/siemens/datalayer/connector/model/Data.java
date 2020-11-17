package com.siemens.datalayer.connector.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"description",
"domain",
"name",
"schemas"
})
public class Data {

@JsonProperty("id")
private Object id;
@JsonProperty("description")
private Object description;
@JsonProperty("domain")
private List<String> domain = null;
@JsonProperty("name")
private String name;
@JsonProperty("schemas")
private Schemas schemas;

@JsonProperty("id")
public Object getId() {
return id;
}

@JsonProperty("id")
public void setId(Object id) {
this.id = id;
}

@JsonProperty("description")
public Object getDescription() {
return description;
}

@JsonProperty("description")
public void setDescription(Object description) {
this.description = description;
}

@JsonProperty("domain")
public List<String> getDomain() {
return domain;
}

@JsonProperty("domain")
public void setDomain(List<String> domain) {
this.domain = domain;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("schemas")
public Schemas getSchemas() {
return schemas;
}

@JsonProperty("schemas")
public void setSchemas(Schemas schemas) {
this.schemas = schemas;
}

}