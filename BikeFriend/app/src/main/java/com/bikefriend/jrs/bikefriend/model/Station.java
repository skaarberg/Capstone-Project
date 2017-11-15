
package com.bikefriend.jrs.bikefriend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "in_service",
    "title",
    "subtitle",
    "number_of_locks",
    "center",
    "bounds"
})
public class Station implements Serializable {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("in_service")
    private Boolean inService;
    @JsonProperty("title")
    private String title;
    @JsonProperty("subtitle")
    private String subtitle;
    @JsonProperty("number_of_locks")
    private Integer numberOfLocks;
    @JsonProperty("center")
    private Center center;
    @JsonProperty("bounds")
    private List<Bound> bounds = null;

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getLocks() {
        return locks;
    }

    public void setLocks(int locks) {
        this.locks = locks;
    }
    @Nullable
    private int bikes;
    @Nullable
    private int locks;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("in_service")
    public Boolean getInService() {
        return inService;
    }

    @JsonProperty("in_service")
    public void setInService(Boolean inService) {
        this.inService = inService;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("subtitle")
    public String getSubtitle() {
        return subtitle;
    }

    @JsonProperty("subtitle")
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @JsonProperty("number_of_locks")
    public Integer getNumberOfLocks() {
        return numberOfLocks;
    }

    @JsonProperty("number_of_locks")
    public void setNumberOfLocks(Integer numberOfLocks) {
        this.numberOfLocks = numberOfLocks;
    }

    @JsonProperty("center")
    public Center getCenter() {
        return center;
    }

    @JsonProperty("center")
    public void setCenter(Center center) {
        this.center = center;
    }

    @JsonProperty("bounds")
    public List<Bound> getBounds() {
        return bounds;
    }

    @JsonProperty("bounds")
    public void setBounds(List<Bound> bounds) {
        this.bounds = bounds;
    }

    public Station(){}

    public Station(String title, String subtitle, int bikes, int locks, int id, Double lat, Double lng){
        this.title = title;
        this.subtitle = subtitle;
        this.bikes = bikes;
        this.locks = locks;
        this.id = id;
        this.center = new Center(lat, lng);
    }
}
