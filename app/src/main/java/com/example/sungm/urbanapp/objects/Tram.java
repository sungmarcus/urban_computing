package com.example.sungm.urbanapp.objects;
import org.simpleframework.xml.Attribute;

public class Tram {
    @Attribute
    private String dueMins;
    @Attribute
    private String destination;

    /* Getters */

    public String getDueMins() {
        return dueMins;
    }

    public String getDestination() {
        return destination;
    }

    /* Setters */

    public void setDueMins(String dueMins) {
        this.dueMins = dueMins;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}