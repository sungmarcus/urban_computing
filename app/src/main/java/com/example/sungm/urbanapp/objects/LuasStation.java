package com.example.sungm.urbanapp.objects;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class LuasStation {
    @Element
    private String message;

    @ElementList(name = "stopInfo", entry = "direction", inline = true, type = LuasDirection.class)
    protected List<LuasDirection> direction;

    public LuasStation(){}

    /* Getters */

    public String getMessage() {
        return message;
    }

    public List<LuasDirection> getDirections() {
        return direction;
    }

    /* Setters */

    public void setDirections(List<LuasDirection> directions) {
        this.direction = directions;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}