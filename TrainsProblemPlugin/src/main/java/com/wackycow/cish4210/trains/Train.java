package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.List;

public class Train {

    private List<String> route = new ArrayList<String>();
    
    private int position = 0;
    
    private String id;

    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    public List<String> getRoute() {
        return route;
    }

    public String getCurrentStation() {
        return route.get(position);
    }
    
    public String getNextStation() {
        return route.get(position+1);
    }

    public int getPosition() {
        return position;
    }
    
}
