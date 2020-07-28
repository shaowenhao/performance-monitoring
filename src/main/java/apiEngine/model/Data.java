package apiEngine.model;

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
private String id;
@JsonProperty("description")
private Object description;
@JsonProperty("domain")
private Object domain;
@JsonProperty("name")
private Object name;
@JsonProperty("schemas")
private Schemas schemas;

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
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
public Object getDomain() {
return domain;
}

@JsonProperty("domain")
public void setDomain(Object domain) {
this.domain = domain;
}

@JsonProperty("name")
public Object getName() {
return name;
}

@JsonProperty("name")
public void setName(Object name) {
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