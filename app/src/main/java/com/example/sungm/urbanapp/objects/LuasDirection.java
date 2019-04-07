package com.example.sungm.urbanapp.objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class LuasDirection {
    @Attribute
    private String name;
    @ElementList(name="direction", entry="tram", inline = true, type = Tram.class)
    private List<Tram> tram;

    /* Getters */
    public List<Tram> getTrams() {
        return tram;
    }

    public String getDirection() {
        return name;
    }

    /* Setters */
    public void setTrams(List<Tram> trams) {
        this.tram = trams;
    }

    public void setDirection(String direction) {
        this.name = direction;
    }
}