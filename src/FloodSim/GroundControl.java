package FloodSim;

import sim.util.*;

public class GroundControl implements Valuable, java.io.Serializable{
	 
	private int fieldID; // identify the type pf the field
	private int RegionID; // holds id of the three Regions 
	private int WaterID;
	private double WaterArea;
	private double food;
	private int patientCounter = 0;
	
	Utility facility; 
	
	int xLoc;
	int yLoc;
	
	private Bag victimHH ; // Region location for household
    private Bag victim; // who are on the field right now
	 
	public GroundControl() {
		 super();
		 victimHH = new Bag();
	     victim = new Bag();
	}
	
	public GroundControl(int x, int y) {
        this.setX(x);
        this.setY(y);

    }
	
    // check how many familes can occupied in a field
	public boolean isRegionOccupied(Flood floodsim) {
		 if (this.getVictimHH().size() >= floodsim.params.global.getMaximumHHOccumpancyPerField()) {
	            return true;
	        } else {
	            return false;
	        }
	}

    public void setVictimHH(Bag victim) {

        this.victimHH = victim;
    }

    public Bag getVictimHH() {

        return victimHH;
    }
    
    public void addVictimHH(Family r) {

        this.victimHH.add(r);
    }

    public void removeVictimHH(Family r) {

        this.victimHH.remove(r);
    }
    
    public void setVictim(Bag victimMoving) {

        this.victim = victimMoving;
    }
    
    public Bag getVictim() {

        return victim;
    }

    public void addVictim(Victim v) {

        this.victim.add(v);
    }

    public void removeVictim(Victim v) {

        this.victim.remove(v);
    }
    
    public void setFieldID(int id) {

        this.fieldID = id;
    }
    
    public int getFieldID() {

        return fieldID;
    }
    
    
    public void setRegionID(int id) {

        this.RegionID = id;
    }

    public int getRegionID() {

        return RegionID;
    }
    
    public void setWaterID(int id) {

        this.WaterID = id;
    }

    public int getWaterID() {

        return WaterID;
    }
    
    public void setFacility(Utility f){
        this.facility =f;
    }
    
    public Utility getFacility(){
        return facility;
    }
    
    public void setFood(double food) {

        this.food = food;
    }

    public double getFood() {

        return food;
    }
    
    public void setWaterArea(double waterA) {

        this.WaterArea = waterA;
    }

    public double getWaterArea() {

        return WaterArea;
    }
   
    
    public void setPatientCounter(int c){
        this.patientCounter = c;
    }
    
    public int getPatientCounter(){
        return patientCounter;
    }
    
    // .....
    
    public boolean equals(GroundControl b) {
        if (b.getX() == this.getX() && b.getY() == this.getY()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(int x, int y) {
        if (x == this.getX() && y == this.getY()) {
            return true;
        }
        return false;
    }
    
    public double distanceTo(GroundControl b) {
        return Math.sqrt(Math.pow(b.getX() - this.getX(), 2) + Math.pow(b.getY() - this.getY(), 2));
    }

    public double distanceTo(int xCoord, int yCoord) {
        return Math.sqrt(Math.pow(xCoord - this.getX(), 2) + Math.pow(yCoord - this.getY(), 2));
    }

    
    GroundControl copy() {
        GroundControl l = new GroundControl(this.getX(), this.getY());
        return l;
    }
      
    final public int getX() {
        return xLoc;
    }

    final public void setX(int x) {
        this.xLoc = x;
    }
    
     // location Y
    final public int getY() {
        return yLoc;
    }

    final public void setY(int y) {
        this.yLoc = y;
    }
    
    //@Override
	public double doubleValue() {

     return getRegionID();
  
    }


}
