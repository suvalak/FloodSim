package FloodSim;

import sim.engine.*;
import sim.util.*;

public class Utility implements Steppable,Valuable{
	
	private int facilityID; // id
//	private int countOfFacility;
	GroundControl location; // location of the facility
	public static final int ORDERING = 0; // schedule first
	
    public void setUtilityID(int id){
        this.facilityID = id;
    }
    
    public int getUtilityID(){
        return facilityID;
    }
 /*   
    public void setCountOfFacilityID(int num){
        this.countOfFacility = num;
    }
    
    public int getCountOfFacilityID(){
        return countOfFacility;
    }
*/    
    public void setLoc(GroundControl loc){
        this.location =  loc;
    }
    
    public GroundControl getLoc(){
        return location;
    }
    
    public boolean isEnoughFood(GroundControl fu,Flood f){
    	if (fu.getFood() > f.params.global.getFoodInCenterSupplyPerDay()){
    		return true;
    	}
    	else return false;
    }
    
    public boolean isFullCapacity(GroundControl fu,Flood f){
        if(fu.getPatientCounter() >= f.params.global.getHeaalthFacilityCapacity()){
            return true;
        }
        else  return false;
    }
    
    // refill food each day
    public void refillFood(Flood flood){
    	for(Object obj: flood.foodCenter){
    		
    		GroundControl f = (GroundControl)obj;
    		double food = f.getFood()+ flood.params.global.getFoodDischareRatePerMinute();
            
            if(food > flood.params.global.getFoodInCenterSupplyPerDay()){ // if it is above the capacity,
            	food = flood.params.global.getFoodInCenterSupplyPerDay(); // set the maximum capacity 
              }
                     
           f.setFood(food); 
           
           //f.setVibrioCholerae(f.getFacility().getInfectionLevel()*water); // set the contamination level of the water
    	}
    }
    
    // daily health center capacity
    public void resetPatientNumber(Flood flood){
        for(Object obj: flood.healthCenters){
            GroundControl f = (GroundControl)obj;
            f.setPatientCounter(0);
        }
    }
    
    public void step(SimState state)
    {
    	Flood f = (Flood) state;
    	if(f.schedule.getSteps() % 1440 == 1){
    		resetPatientNumber(f);
    	}
    }
    
    //@Override
	public double doubleValue() {
       return this.getUtilityID();             
   }
    
}
