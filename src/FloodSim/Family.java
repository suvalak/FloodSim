package FloodSim;


import sim.util.Bag;

public class Family {
	
	 Bag relatives; // hold relative location
	 private double FoodTotal = 0.0; // total water 
	 GroundControl location; // location of the house
	 Bag members; // holds the family members
	// private double waterQ = 0.0; // quality represent the contamination level
	 
	 public Family(GroundControl loc){
	        
	        this.setRegionLocation(loc);
	        members = new Bag();
	        relatives = new Bag();
	 }
	 
	 // hold the amount of water in house
     public void setFoodAtHome(double food){
        
        this.FoodTotal = food;      
                 
    }
    
    public double getFoodAtHome(){
        return FoodTotal;
    }
    
	 // location of house       
	 final public void setRegionLocation(GroundControl location){
        this.location = location;
	 }
	    
	 final  public GroundControl getRegionLocation(){
	     return location;
	 }
	 
	 // holds memebers of the family
	 public void setMembers(Bag refugees){
    
        this.members = refugees;
	 }
    
	 public Bag getMembers(){
    
        return members;
	 }
	 
	 public void addMembers(Victim v){
        
        this.members.add(v);
	 }

	 public void removeMembers(Victim v){
    
        this.members.remove(v);
	 }
	 
    // location of the relative
	 public void setRelativesLocation(Bag r){
        this.relatives = r;
	 }
    
	 public Bag getRelativesLocation(){
        return relatives;
	 }
    
	 public void addRelative(GroundControl relative){
        relatives.add(relative);
	 }
    
	 public void removeFriend(GroundControl relative){
        relatives.remove(relative);
	 }
    
	 public int numberOfOutOfAreaFamilyMembers(){
        int count  =0;
        for(Object v:this.getMembers()){
            Victim ref =(Victim)v;
            if (ref.VictimType == 1){
            	if(ref.getHealthStatus()==3 && ref.numIgnoreTreat == 2){
                count = count +1;
            	}
            }
            else if(ref.VictimType == 2){
            	if(ref.getHealthStatus() != 1){
            		 count = count +1;
            	}
            }
            else{
            	
            }             	
            
        }
        return count;
	 }
	 
}
