package FloodSim;

import java.util.ArrayList;

import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.*;

public class Vehicle implements Steppable, Valuable, java.io.Serializable{

     private int capacity;
     private GroundControl depot;
     private GroundControl position;
     private GroundControl goal;
     private double waterLevelMove;
     
	 public int cStep;
	 private double jitterX; // Visualization 
	 private double jitterY;
	 
	 public int minuteInDay;
	 TimeManager tm;
	 Victim vm;
	 Flood f;
	 ArrayList<GroundControl> path = null; 
	 MersenneTwisterFast randomN ;
	 
	 public Vehicle(int cap, Family hh, GroundControl depot, GroundControl position, MersenneTwisterFast random, Continuous2D allVehicle){
		 this.setCapacity(0);
		 this.setDepot(depot);
		 this.setGoal(depot);
		 this.jitterX = random.nextDouble();
	     this.jitterY = random.nextDouble();
	     this.setPosition(position);
	        
		 tm = new TimeManager();
		 f = null;
		 vm = null;
		 cStep = 0;
	     minuteInDay = 0;
	     randomN = random;
	     
	     allVehicle.setObjectLocation(this, new Double2D(hh.getRegionLocation().getX() + jitterX, hh.getRegionLocation().getY() + jitterY));
	 }
	 
	 private void setCapacity(int cap){
		    this.capacity = cap;
	 }
	 
	 private int getCapacity(){
		    return this.capacity;
	 }
	 
	 private void setPosition(GroundControl position) {
	        this.position = position;
	 }
	    
	 public GroundControl getPosition() {
	        return position;
	 }
	 
	 public void setDepot(GroundControl home) {
	        this.depot = home;
	 }

	 public GroundControl getDepot() {
	        return depot;
	 }
	 
     // goal position - where to go
     public void setGoal(GroundControl position) {
        this.goal = position;
     }
    
     public GroundControl getGoal() {
        return goal;
     }
	 
	 public void move(int steps) {
		 
		 if (this.getGoal() == null) {
	            return;
	        }
		 else if (this.getPosition().equals(this.getGoal()) == true && this.getGoal().equals(this.getDepot()) != true) {
			  //  calcGoal();
	        }
	 }
	 
     public void step(SimState state) {
    	 move(cStep);
     }
     
     public void setStoppable(Stoppable stopp) {

         //stopper = stopp;
     }

     public void stop() {

         //stopper.stop();
     }

	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}
     
	
}
