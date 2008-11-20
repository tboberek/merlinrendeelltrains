package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class McCuskerDispatcher extends SchedulingDispatcher {

    public McCuskerDispatcher() {
    }

    protected boolean containsStation(Train train, String station, Collection<Train> trains) {
        for (Train t : trains) {
            if (train == t) continue;
            if (train.getRoute().contains(station)) return true;
        }
        return false;
    }
    
    @Override
    protected List<ScheduleItem> getSchedule(Collection<Train> trains) {
        List<ScheduleItem> result = new ArrayList<ScheduleItem>();
        for (Train t : trains) {
            List<String> route = t.getRoute();
            for (int i=0; i< route.size(); ++i) {
                if (i+1 < route.size() 
                        && containsStation(t, route.get(i+1),trains))
                    result.add(new ScheduleItem(t, route.get(i), i));
            }
        }
        System.out.println(getName()+" Created schedule:");
        for (ScheduleItem item : result) {
            System.out.println(item);
        }
        return result;
    }

    @Override
    public String getName() {
        return "McCusker Dispatcher";
    }

}
