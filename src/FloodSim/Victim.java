package FloodSim;


import java.util.ArrayList;
import java.awt.*;

import FloodSim.RegionGenerator.Node;
import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.*;

public class Victim implements Steppable, Valuable, java.io.Serializable{
   
	private int age;
    private int sex;
    private int AgentType;
    private double waterLevel = 0.0;
    private double FoodDemand = 0.0;
    private GroundControl home;// home 
    
    private GroundControl position;
    private double waterPosition;
    private GroundControl goal; // location of the goal
    private int healthStatus; //health status -  healthy - 1, symptom - 2, sick-3
    private int prevHealthStatus; // monitors health status of agent in the previous step - to capture the change in each day
    public int cStep;
    private double jitterX; // Visualization 
    private double jitterY;
    
    Family hh;
    private int currentAct;
    public int sumVictumLeave;
    
    public static final int ORDERING = 2;
    protected Stoppable stopper;
    
    public boolean isRecieveTreatment = false;
    public int stayingTime;
    public boolean isSick = false;
    public int numIgnoreTreat = 0;
    public int VictimType;
    Flood f;
    
    public int minuteInDay;
    TimeManager tm;// time contorler-identify the hour, day, week
    ArrayList<GroundControl> path = null; // the agent's current path to its current goal
    MersenneTwisterFast randomN ;
    
    public Victim(int age, int sex, Family hh, GroundControl home, GroundControl position, MersenneTwisterFast random, Continuous2D allVictims){
    	this.setAge(age);
        this.setSex(sex);
        this.setFamily(hh);
        this.setHome(home);
        this.setGoal(home);
        this.jitterX = random.nextDouble();
        this.jitterY = random.nextDouble();
        this.setPosition(position);
        this.setWaterAtPosition(position.getWaterArea());
        this.setPrevHealthStatus(1);
        tm = new TimeManager();
        f = null;
        cStep = 0;
        minuteInDay = 0;
        randomN = random;
        
        allVictims.setObjectLocation(this, new Double2D(hh.getRegionLocation().getX() + jitterX, hh.getRegionLocation().getY() + jitterY));
    }
    
    public void setAgentType(int num) {
        this.AgentType = num;
    }

    public int getAgentType() {
        return AgentType;
    }
    
    public Color getInitialAgentColor() {
    	
    	Color c = new Color(0, 0, 0);
        if (this.getAgentType() == 1){
        	c = new Color(255, 246, 143); //yellow        	
        }
        else if (this.getAgentType() == 2){
        	c = new Color(122, 197, 205); //blue       
        }  
		return c;
    }
    
    private void setPosition(GroundControl position) {
        this.position = position;
    }
    
    public GroundControl getPosition() {
        return position;
    }
    
    private void setWaterAtPosition(Double wpos) {
        this.waterPosition = wpos;
    }
    
    public double getWaterAtPosition() {
        return waterPosition;
    }
    
    // goal position - where to go
    public void setGoal(GroundControl position) {
        this.goal = position;
    }
    
    public GroundControl getGoal() {
        return goal;
    }
    // home location   
    public void setHome(GroundControl home) {
        this.home = home;
    }

    public GroundControl getHome() {
        return home;
    }
    	
    public void setAge(int age) {
        this.age = age;
        setVictimType();
    }

    public int getAge() {
        return age;
    }
    
    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSex() {
        return sex;
    }
    
    public void setFamily(Family hh) {
        this.hh = hh;
    }

    public Family getFamily() {
        return hh;
    }
    
    public void setHealthStatus(int status) {
        this.healthStatus = status;
    }

    public int getHealthStatus() {
        return healthStatus;
    }
    
    public void setPrevHealthStatus(int status) {
        this.prevHealthStatus = status;
    }

    public int getPrevHealthStatus() {
        return prevHealthStatus;
    }
    
    public void setCountIgnoreTreat(int num) {
        this.numIgnoreTreat = num;
    }

    public int getCountIgnoreTreat() {
        return numIgnoreTreat;
    }
    
    public void setIsrecieveTreatment(boolean tr){
        isRecieveTreatment = tr;
    }
    
    public boolean getIsrecieveTreatment(){
        return isRecieveTreatment;
    }
    
    public void setIsSick(boolean s){   	
        isSick = s;
    }
    
    public void setStayingTime(int sty) {
        this.stayingTime = sty;
    }

    public int getStayingTime() {
        return stayingTime;
    }
    
    public boolean getIsSick(){
    	if (this.getCountIgnoreTreat() > f.params.global.getMaxIgnoreTreat() ){
    		isSick = true;
    	}
    	else {
    		isSick = false;
    	}
    	return isSick;
    }
    
    public int setVictimType(){
    	
    	if (this.getAge() >= 50){
    		VictimType = 1; // Oldster
    	}
    	else if (this.getAge() > 25 && this.getAge() < 50){
    		VictimType = 2; // Middle-Age
    	}
    	else if (this.getAge() <= 25){
    		VictimType = 3; // Teenager
    	}
    	else {
    		VictimType = 4; // Out of consider
    	} 
    	return  VictimType;
    }
    
    public void setWaterLevel(double w) {
        this.waterLevel = w;
    }

    public double getWaterLevel() {
        return waterLevel;
    }
    
    public void setFoodLevel(double w) {
        this.FoodDemand = w;
    }

    public double getFoodLevel() {
        return FoodDemand;
    }
    
    public void setCurrentActivity(int a) {
        this.currentAct = a;
    }

    public int getCurrentActivity() {
        return currentAct;
    }
    
    public void setVictimLeave(int l) {
        this.sumVictumLeave = l;
    }
    
    public int getVictimLeave() {
        return sumVictumLeave;
    }
    
    public void calcGoal() {
        // --> from home cals the best goal      
        if (this.getPosition().equals(this.getHome()) == true) {
               int cAct = actSelect();   //   select the best goal 
               Activity act = new Activity();
               this.setGoal(act.bestActivityLocation(this, this.getHome(), cAct, f)); // search the best location of your selected activity
               this.setCurrentActivity(cAct);   // track current activity - for the visualization     
               this.setStayingTime(stayingPeriod(this.getCurrentActivity()));
               
               return;

           } 
         // --> from goal to home             
         else if (this.getPosition().equals(this.getGoal()) == true && this.getGoal().equals(this.getHome()) != true) {
               
               this.setGoal(this.getHome());
               this.setStayingTime(stayingPeriod(0));
               this.setCurrentActivity(0);
               return;
               //
           } // incase 
           else {
               this.setGoal(this.getHome());
               this.setCurrentActivity(0);

               return;
           }
       }
    
    // where to move
    public void move(int steps) {

        // if you do not have goal- return
        if (this.getGoal() == null) {
            return;
        }      
        else if (this.getPosition().equals(this.getGoal()) == true && this.getGoal().equals(this.getHome()) != true && isStay() == true) {
            return;
        }
        // at your goal- do activity and recalulate goal  
        else if (this.getPosition().equals(this.getGoal()) == true) {
            
              doActivity(this.getGoal(), this.getCurrentActivity());
              if(steps % 1440 < 17){
                  if(randomN.nextDouble() > 0.3){
                      calcGoal();
                  }
              }
              else{
                  calcGoal();
              }
                
        } // else move to your goal
        else {
            Node start = (Node) f.closestNodes.get(this.getPosition().getX(), this.getPosition().getY());
            Node goal = (Node) f.closestNodes.get(this.getGoal().getX(), this.getGoal().yLoc);
            //          make sure we have a path to the goal!
            if (path == null || path.size() == 0) {
               path = AStar.astarPath(f, start,goal);
                    //    (Node) f.closestNodes.get(this.getPosition().getX(), this.getPosition().getY()),
                    //    (Node) f.closestNodes.get(this.getGoal().getX(), this.getGoal().yLoc));
                if (path != null) {
                    path.add(this.getGoal());
               }
            }


            // determine the best location to immediately move *toward*
            GroundControl subgoal;

            // It's possible that the agent isn't close to a node that can take it to the center. 
            // In that case, the A* will return null. If this is so the agent should move toward 
            // the goal until such a node is found.
            if (path == null) {
                subgoal = this.getGoal();
            } // Otherwise we have a path and should continue to move along it
            else {
                // have we reached the end of an edge? If so, move to the next edge
                if (path.get(0).equals(this.getPosition())) {
                    path.remove(0);
                }

                // our current subgoal is the end of the current edge
                if (path.size() > 0) {
                    subgoal = path.get(0);
                } else {
                    subgoal = this.getGoal();
                }
         
            }

            Activity current = new Activity();
            GroundControl loc = current.getNextTile(f, subgoal, this.getPosition());

            GroundControl oldLoc = this.getPosition();
            oldLoc.removeVictim(this);

            this.setPosition(loc);
            loc.addVictim(this);


            f.allVictims.setObjectLocation(this, new Double2D(loc.getX() + this.jitterX, loc.getY() + jitterY));
        }

    }
    
	private double healthActivityWeight() {
        double wHealthC;
        
        
        if (this.getHealthStatus() == 2 && this.getIsrecieveTreatment() == false) { // weight high
            wHealthC = 0.7 + 0.3 * randomN.nextDouble();        
        }
        else if (this.getIsSick()){
        	wHealthC = 0.8 + 0.2 * randomN.nextDouble();    
        }
        else if (randomN.nextDouble() < 0.05){
            wHealthC = 0.5 + 0.5 * randomN.nextDouble();
        }
        else {
            wHealthC = randomN.nextDouble()*(0.1 +  0.2 * randomN.nextDouble());
        }
        return wHealthC;
    }
	
    private double terminalActivityWeight() {
    	double wTerminal;
    	
    	if (this.VictimType == 1){
    		if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdOldster()){
    			wTerminal = 1;  				
    		}
    		else{
    			wTerminal = randomN.nextDouble()*(0.1 +  0.2 * randomN.nextDouble());
    		}
    	}
    	
    	else if (this.VictimType == 2){
    		if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdMiddleAge() && this.getHealthStatus() != 1){
    			wTerminal = 1;
    		}
    		else{
    			wTerminal = randomN.nextDouble()*(0.1 +  0.2 * randomN.nextDouble());
    		}
    	}
    	
    	else if (this.VictimType == 3){
    		if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdTeenager() || this.getHealthStatus() == 3){
    			wTerminal = 1;
    		}
    		else{
    			wTerminal = randomN.nextDouble()*(0.1 +  0.2 * randomN.nextDouble());
    		}
    	}
    	else{
    		wTerminal = randomN.nextDouble()*(0.1 +  0.2 * randomN.nextDouble());
    	}
    	
    	return wTerminal;
    }

    private double foodActivityWeight() {
        
         // food distibution will take third
        // because ration is given on scheduled time, agent give priority for food at tat day
    	
    	// Flood : Everyday is foodDate -> must go to Food Center in every Day
    	
    	double wFood;
    	
    	if (this.getFamily().getFoodAtHome() < f.params.global.getMaximumFoodRequirement()) {
    		wFood = 0.6  + 0.3 * randomN.nextDouble(); // weight high
    	}
    	else{
    		wFood = 0.3 +  0.2 * randomN.nextDouble();  // weight low	
    	}
    	return wFood * randomN.nextDouble();
    	
/*  
        double wFoodDist;
        int foodDate = 1 + (tm.dayCount(cStep) % 9);
        int dummyFood = (foodDate == this.getFamily().getRationDate()) ? 1 : 0; // if the day is not a ration day, agent will not go to food center
        if(dummyFood == 1 && this.getAge()  > 15){
               wFoodDist = 0.6  + 0.3 * randomN.nextDouble();
      
        }
        else{
            wFoodDist =0.1 +  0.2 * randomN.nextDouble();
        }
        return wFoodDist * randomN.nextDouble();
 */       
    }
    
    
/*    
    private double collectWaterActivityWeight() {
        double wBorehole = 0;
        // not enough water at home
        if (this.getAge() > 10 ){
            if (this.getFamily().getWaterAtHome() < (d.params.global.getMinimumWaterRequirement() * (this.getFamily().getMembers().numObjs ))){
                wBorehole = 0.7 *Math.sin(this.getAge()) + 0.2 * randomN.nextDouble();
            }
            else{
                wBorehole = 0.2 +  0.2 * randomN.nextDouble();
            }
        }
        
        return wBorehole * randomN.nextDouble();
    }
 */   
    
    // collect water from borehole or rain
/*    
    public void featchFood(FieldUnit fu) {
        
        double waterReq = 2 * f.params.global.getMaximumWaterRequirement() + (2 * randomN.nextDouble() * f.params.global.getMaximumWaterRequirement()); // how many litres you can collect?
        double waterFetched = 0.0;
        double concentration = 0.0; // cholera virus
         // check the contamination level of the water you fetch
       
        if(fu.getWater() ==0){
            concentration = 0.0;
        }
       
        else{
            //concentration = f.getVibrioCholerae() / (f.getWater());
            concentration = fu.getVibrioCholerae();
        }
        // water from borehole
        if(fu.getWater()<=0){
             waterFetched = 0;
             fu.setWater(0);
             
        }
        
        else if (waterReq >= fu.getWater()) {  // if you collect all water, water level will be 0
            waterFetched = fu.getWater();
           
            fu.setWater(0);

        } else {
            waterFetched = waterReq;
            
            fu.setWater(fu.getWater() - waterFetched); // water level will lower by the amount you take

        }
     
        double currentWater = this.getFamily().getWaterAtHome() + waterFetched;   // add water to your family bucket


        this.getFamily().setWaterAtHome(currentWater);


        if (currentWater <= 0) {
            this.getFamily().setWaterBacteriaLevel(0);
        } //          
        else {
            this.getFamily().setWaterBacteriaLevel(concentration*waterFetched + this.getFamily().getWaterrBacteriaLevel()); // update the contamination level
        }

    }
*/    
    
    public int actSelect() {
        int alpha = (60 * 6) + randomN.nextInt(60 * 3); // in minute - working hour start 
        int beta = (60 * 17) + randomN.nextInt(120); // in minute working hour end
        boolean isDayTime = minuteInDay >= alpha && minuteInDay <= beta;
        if (!isDayTime) {
            double wHealth = 0;
            if(this.getHealthStatus() == 2){
                wHealth = healthActivityWeight();
            }
            else{
                wHealth = 0;
            }
            return (wHealth < 0.3) ? Activity.STAY_HOME : Activity.HEALTH_CENTER;
        } else {
            double[] activityPriortyWeight = {0.0, 0.0, 0.0, 0.0};
            activityPriortyWeight[1] = foodActivityWeight();//0.16;;//0.12;
            activityPriortyWeight[2] = healthActivityWeight();//0.1;
            activityPriortyWeight[3] = terminalActivityWeight();
            //... activityPriortyWeight[4] = visitRelativeActivityWeight();//0.09;
            
            int curAct = 0;

            // Find the activity with the heighest weight
            double maximum = activityPriortyWeight[0];   // start with the first value
            for (int i = 1; i < 4; i++) {
                if (activityPriortyWeight[i] > maximum) {
                    maximum = activityPriortyWeight[i];   // new maximum
                    curAct = i;
                }
            }
            
            // Maximum weight must be > 0.3, else stay home
            if (activityPriortyWeight[curAct] < 0.3) {
                curAct = Activity.STAY_HOME;
            }
            /*
            if(curAct == 3){
            	sumVictumLeave = this.getVictimLeave() + 1 ;
            	this.setVictimLeave(sumVictumLeave);
            }
            */
            return curAct;
        }
    }
        public void doActivity(GroundControl fu, int activ) {

            switch (activ)
            {
                default:
                case Activity.STAY_HOME:
                    break;               
                case Activity.FOOD_CENTER:                    
                    featchFood(fu);         
                    break;                   
                case Activity.HEALTH_CENTER:
                    recieveTreatment(fu, f);
                    break;
                case Activity.TERMINAL_CENTER:
                	migrateOutofLocation(fu, f);
                	break;          
            }
        }
        
        public int stayingPeriod(int act){
        	
        	int period = 0;
            int minStay = 20; // minumum time to stay in any facility
            int maxStay = 180; // 3 hours
            int curMin = minuteInDay;
            
            switch(act){
                
	            case 0:  // stay home
	                period = maxStay;
	                break;
	            case 1: // food center
	            	 period = minStay + 20; //stay food center max 20 minute	                 
	                 break;
	            case 2: // health center
	            	period = 0;
	            case 3: // terminal center
	            	period = 43200 ; //30days (must be infinity)
	            	break;
            }
            
            return (period + curMin);
        }
        
        // how long agent need to stay at location
        public boolean isStay() {

            //TimeManager tm = new TimeManager();

            boolean isStay = false;
            
            if (minuteInDay < this.getStayingTime()){
                isStay = true;
            } 
            else isStay = false;
            
            return isStay;

        }       
        
        public void reduceFood(){
        	
        	// every minute they loose 0.01* 24 * 60 = 15 liter/day
        	double dailyUse = this.getFoodLevel() - 0.01;
        	if (dailyUse <= 0) {
        		this.setFoodLevel(0);
        	}
        	else{
        		this.setFoodLevel(dailyUse);
        	}
        }
        
        public void featchFood(GroundControl fu) {
        	
        	double FoodReq = 2 * f.params.global.getMaximumFoodRequirement() + (2 * randomN.nextDouble() * f.params.global.getMaximumFoodRequirement()); // how many litres you can collect?
            double FoodFetched = 0.0;
            
            if(fu.getFood() <= 0){
                FoodFetched = 0;
                fu.setFood(0);        
            }
            else if (FoodReq >= fu.getFood()) {  // if you collect all food, food level will be 0
                FoodFetched = fu.getFood();
                fu.setFood(0);
            }
            else {
            	FoodFetched = FoodReq;
            	fu.setFood(fu.getFood() - FoodFetched); // food level will lower by the amount you take
            }
            
            double currentFood = this.getFamily().getFoodAtHome() + FoodFetched;   // add food to your family bucket
            this.getFamily().setFoodAtHome(currentFood);
            
        }
        
        private void utilizeFood() {
        	
        	// agent need to take food in daily bases
        	// the amount of food not fixed- between min and max
        	
        	 if (this.getFoodLevel() >= f.params.global.getMaximumFoodRequirement()){
                 return;
             }
        	 
        	 double dailyUse = 1.2 *(f.params.global.getMaximumFoodRequirement()  - this.getFoodLevel()) * randomN.nextDouble() ; // randomly
             if(dailyUse < 0){
                 dailyUse =0;
                 return;
             }
             
             double FoodUsed = 0;
             // only uses from family bucket
             if(this.getFamily().getFoodAtHome() <  dailyUse){
            	 FoodUsed = this.getFamily().getFoodAtHome();
             }
             else{
            	 FoodUsed = this.getFamily().getFoodAtHome() - dailyUse;
             }
             
             double maxFooduse  = this.getFoodLevel() + FoodUsed;
             this.setFoodLevel(maxFooduse);
             
             double FoodAtHome= this.getFamily().getFoodAtHome() - FoodUsed;
             this.getFamily().setFoodAtHome(FoodAtHome); // update the food level of the family bucket
             
             //.. Dadaab : Set Infection from water
        }
        
        public void recieveTreatment(GroundControl fu, Flood f) {
            // based on the capacity of the Health Center
            // be Symptom or Sick + Treat
            if (this.getHealthStatus() != 1 && fu.getFacility().isFullCapacity(fu,f) == false) {
                fu.setPatientCounter(fu.getPatientCounter() + 1);
                if(randomN.nextDouble() < f.params.global.getprobabilityOfEffectiveNessofmedicine()){
	                //int recovery = cStep + (400 + randomN.nextInt(1440));
	                this.setIsrecieveTreatment(true);
	                this.setCountIgnoreTreat(this.getCountIgnoreTreat() + 1);
                }                         
            }            
        }
        
        public void migrateOutofLocation(GroundControl fu, Flood f){
        	
        	this.stayingPeriod(3);
        	//fu.removeVictim(this);
        	
        	/*
        	if (fu.getFacility().isEnoughFood(fu, f) == false){
        		// oldster
    			if (this.VictimType == 1){
        			if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdOldster()){
        				fu.removeVictim(this);
        				this.stayingPeriod(3);
        			}
	        	}     
    			// middle-age
	        	else if (this.VictimType == 2){
	        		if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdMiddleAge() && this.getHealthStatus() != 1){
        				fu.removeVictim(this);
        				this.stayingPeriod(3);
        			}
	        	}
    			// teenager
	        	else if (this.VictimType == 3){
	        		if (this.getWaterLevel() >= f.params.global.getMaxWaterThreadholdTeenager()){
        				fu.removeVictim(this);
        				this.stayingPeriod(3);
        			}
	        	}	
			}
            */
        }

        
        // ...
        
//        @Override
        public void step(SimState state) {
        	f = (Flood) state;

            cStep = (int) f.schedule.getSteps();
            
            if(cStep < 1440){
                minuteInDay = cStep;
            }
           else {
                minuteInDay = cStep % 1440;
                
            }
           
            double w =  this.getWaterAtPosition() + (cStep * f.params.global.getIncreateFloodRate());
            this.setWaterLevel(w); 
                        
            if (this.getFoodLevel() < (f.params.global.getMinimumFoodRequirement())) {
               utilizeFood();
             
            }
                       
            this.setPrevHealthStatus(this.getHealthStatus()); // update prveois
 		
            
            if(this.getHealthStatus()==2){
            	if(this.getIsrecieveTreatment()==true ){
            		this.setHealthStatus(1);
                    this.setIsrecieveTreatment(false);
            	}
            	else{
            		if(this.getIsSick()==true){
            			 this.setHealthStatus(3);
            		}
            	}
            }
            
            // is sick and be treat --> be healthy
            if(this.getHealthStatus()==3){
                 if(this.getIsrecieveTreatment()==true ){
                    this.setHealthStatus(1);
                    this.setIsrecieveTreatment(false);
                }
                
               
            }
                     
            reduceFood();   
            
            move(cStep);
                      
        }
        
        public void setStoppable(Stoppable stopp) {

            stopper = stopp;
        }

        public void stop() {

            stopper.stop();
        }
        
       // @Override
    	public double doubleValue() {

            return this.getHealthStatus();

        }

}


