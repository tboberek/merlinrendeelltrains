package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OneAtATimeSchedulingDispatcher extends SchedulingDispatcher {

    public OneAtATimeSchedulingDispatcher() {
    }

    @Override
    protected List<ScheduleItem> getSchedule(Collection<Train> trains) {
        List<ScheduleItem> result = new ArrayList<ScheduleItem>();
        for (Train t : trains) {
            List<String> route = t.getRoute();
            for (int i=1; i< route.size(); ++i) {
                result.add(new ScheduleItem(t, route.get(i), i));
            }
        }
        return result;
    }

    @Override
    public String getName() {
        return "One at a Time Scheduling Dispatcher";
    }

}
