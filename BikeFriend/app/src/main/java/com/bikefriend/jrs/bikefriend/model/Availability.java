package com.bikefriend.jrs.bikefriend.model;

/**
 * Created by jrs on 15/11/2017.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bikes",
        "locks"
})
public class Availability {

    @JsonProperty("bikes")
    private Integer bikes;
    @JsonProperty("locks")
    private Integer locks;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bikes")
    public Integer getBikes() {
        return bikes;
    }

    @JsonProperty("bikes")
    public void setBikes(Integer bikes) {
        this.bikes = bikes;
    }

    @JsonProperty("locks")
    public Integer getLocks() {
        return locks;
    }

    @JsonProperty("locks")
    public void setLocks(Integer locks) {
        this.locks = locks;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}