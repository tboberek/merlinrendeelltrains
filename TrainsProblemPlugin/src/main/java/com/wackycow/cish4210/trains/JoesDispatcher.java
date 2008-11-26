package com.wackycow.cish4210.trains;

import java.util.HashMap;
import java.util.Map;

public class JoesDispatcher extends Dispatcher {

	private HashMap stationcnt = new HashMap();
        public void initialize() {}
        	
	private  int upStationCnt(TrainsGraph g, String stationId) {
		int i = 0;
                if (!stationId.equals("XXXX")) {
                    if (!stationcnt.containsKey(stationId)) {
                      	stationcnt.put(stationId, new Integer(0));
                    }
                   
                    if (!stationId.equals(g.getEngineHouseId())) {
                       i = (Integer)stationcnt.get(stationId);
                       i++;
                       stationcnt.put(stationId, i);
                    }
                }
		return i;
	}
        
        private void downStationCnt(String stationId) {
		if (!stationId.equals("XXXX")) {
                    if (stationcnt.containsKey(stationId)) {
                        int i = (Integer)stationcnt.get(stationId);
                        if (i > 0) {
                              i--;
                         }
                         
                         stationcnt.put(stationId, i);
                    }
		}
               
                
		
	}
	
	@Override
	public synchronized void checkMoveTrain(TrainsGraph g, Train t, String nextStationId) {
		//get counts for next station and station after next
                       
            System.out.println (" just entered checkmovetrain(" + t.getId() + ") "  + " Current Stations count:" +
				 stationcnt.values()  );
            
             int tlimit = (g.getTrains().size()) - 1;
         
             String currentStationId = t.getCurrentStation();
             String priorStationId = t.getPriorStation();
             String next2StationId = t.getNext2Station();
             
             
             
             int iNextStation = upStationCnt(g, nextStationId);
             int iAfterNextStation = upStationCnt(g, next2StationId);
             
    
             while (((Integer)stationcnt.get(nextStationId) > (tlimit) && ((Integer)stationcnt.get(next2StationId)) > tlimit)) {
                    try{ 
                           wait();
                       }catch (InterruptedException e) {}
                    System.out.println ("(" + t.getId() + ") unable to move to  " +
				 nextStationId );
                    System.out.println ( nextStationId + " next count = " + (Integer)stationcnt.get(nextStationId));
                    System.out.println ( next2StationId + " next 2 count = " + (Integer)stationcnt.get(next2StationId) );
             }
             notifyAll();
             downStationCnt(currentStationId);
             downStationCnt(priorStationId);
             if (nextStationId.equals(g.getEngineHouseId())) {
                downStationCnt(currentStationId);
             }
      
            
	}

    @Override
    public String getName() {
        return "Joes Dispatcher";
    }

}
