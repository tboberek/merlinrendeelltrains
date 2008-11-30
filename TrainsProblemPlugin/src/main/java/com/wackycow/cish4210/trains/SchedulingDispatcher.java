package com.wackycow.cish4210.trains;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** 
 * Abstract dispatcher that allows the production of a schedule 
 * around critical paths in the system.
*/
public abstract class SchedulingDispatcher extends Dispatcher {

    List<ScheduleItem> schedule = null;
    List<ScheduleItem> oldSchedule = null;
    int scheduleStep = -1;
    
    public void initialize() {
        schedule = null;
        scheduleStep = -1;
    }
    
    // TODO This still has race conditions.
    @Override
    public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
        System.out.println("Checking train "+t+" for station "+stationId);
        synchronized(this) {
            if (schedule == null) 
                schedule = Collections.synchronizedList(getSchedule(g.getTrains().values()));
        }
        int sstep = -1;
        synchronized(this) {
            sstep = scheduleStep;
        }
        
        if (sstep+1 >= schedule.size()) return;
        ScheduleItem nextStep = schedule.get(sstep+1);
        if (nextStep.train == t 
                && nextStep.position == t.getPosition()+1) {
            // All clear.
            System.out.println("Stepping through on "+t.getId()+" "+stationId);
        } else {
            boolean waited = false;
            for (int i=sstep+1; i<schedule.size(); ++i) {
                ScheduleItem item = schedule.get(i);
                if (item.train == t 
                        && item.position == t.getPosition()+1) {
                    synchronized (item) {
                        waited = true;
                        try {
                            System.out.println("Waiting for"+item+" on "+t.getId()+" "+stationId);
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
        }
        if (scheduleStep+1 >= schedule.size()) return;
        ScheduleItem item = schedule.get(scheduleStep+1);
        synchronized(item) {
            System.out.println("Notifying "+item+" from "+t.getId()+" "+stationId);
            item.notify();
            System.out.println("Notified "+item+" from "+t.getId()+" "+stationId);
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
        public String toString() {
            return "ScheduleItem:\t"+train.getId()+"\t"+station+"\t"+position;
        }
    }

}
