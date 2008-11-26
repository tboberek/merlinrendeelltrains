package com.wackycow.cish4210.trains;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Joes2ndDispatcher extends Dispatcher {

	private List<String> stationlist = new ArrayList<String>();
        
         public void initialize() {}
	private synchronized boolean checkStations(Train t) {
                int p = 0;
                List<String> route = new ArrayList<String>();
                route = t.getRoute();
            
                for (p = 1; p < route.size()-1; ++p) { 
                    if (stationlist.contains(route.get(p))) {
                        return true;
                    }
                }
                //capture the whole route
                for (p = 1; p < route.size()-1; ++p) { 
                      stationlist.add(route.get(p));
                }
                
                return false;
        }
	
    
        private void downStationCnt(String stationId) {
                stationlist.remove(stationId);
                System.out.println ("TRying to remove " + stationId + " from list");
        }
	
	@Override
	public synchronized  void checkMoveTrain(TrainsGraph g, Train t, String nextStationId) {
		//get counts for next station and station after next
                       
            List<String> route = new ArrayList<String>();
            route = t.getRoute();
            int troutesize = (route.size());
         
             String currentStationId = t.getCurrentStation();
             String priorStationId = t.getPriorStation();
             String next2StationId = t.getNext2Station();
             
             // first try for this train, check if the whole route is clear 
             if (t.getPosition() == 0) {
                 while (checkStations(t)) {
                     try{ 
                           wait();
                       }catch (InterruptedException e) {}
                 }
                 //allow train to move
                 notifyAll();
             } else {
                 //subsequent moves
                 notifyAll(); 
                if (t.getPosition() < troutesize-1) {
                        downStationCnt(currentStationId);
                }
             }
             System.out.println (" Current Station List" + stationlist.toString() + ") "  );
             
             
  
	}

    @Override
    public String getName() {
        return "Joes 2nd Dispatcher";
    }

}
