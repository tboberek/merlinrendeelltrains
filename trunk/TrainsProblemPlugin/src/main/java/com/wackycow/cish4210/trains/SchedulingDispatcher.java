package com.wackycow.cish4210.trains;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class SchedulingDispatcher extends Dispatcher {

    List<ScheduleItem> schedule = null;
    List<ScheduleItem> oldSchedule = null;
    int scheduleStep = -1;
    
    // TODO This still has race conditions.
    @Override
    public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
        synchronized(this) {
            if (schedule == null) 
                schedule = Collections.synchronizedList(getSchedule(g.getTrains().values()));
        }
        ScheduleItem nextStep = schedule.get(scheduleStep+1);
        if (nextStep.train == t 
                && nextStep.station == stationId 
                && nextStep.position == t.getPosition()+1) {
            // All clear.
        } else {
            boolean waited = false;
            for (int i=scheduleStep+1; i<schedule.size(); ++i) {
                ScheduleItem item = schedule.get(i);
                if (item.train == t 
                        && item.station == stationId 
                        && item.position == t.getPosition()+1) {
                    synchronized (item) {
                        waited = true;
                        try {
                            item.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            if (waited == false) return;
        }
        synchronized (this) {
            scheduleStep++;
            ScheduleItem item = schedule.get(scheduleStep+1);
            synchronized(item) {
                item.notify();
            }
        }
    }

    protected abstract List<ScheduleItem> getSchedule(Collection<Train> trains);

    protected class ScheduleItem {
        public Train train;
        public String station;
        public int position;
        
        public ScheduleItem(Train train, String station, int position) {
            this.train = train;
            this.station = station;
            this.position = position;
        }
        public ScheduleItem() {
            
        }
    }

}
