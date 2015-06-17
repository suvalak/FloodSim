package FloodSim;

import java.io.File;
import java.io.IOException;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Parameters {

	GlobalParamters global = new GlobalParamters();
	
	 private final static String A_FILE = "-file";
	 
	 public Parameters(String[] args) {
	        if (args != null) {
	            loadParameters(openParameterDatabase(args));
	        }
	 }
	 
	 private static ParameterDatabase openParameterDatabase(String[] args) {
		 ParameterDatabase parameters = null;
		 for (int x = 0; x < args.length - 1; x++) {
	            if (args[x].equals(A_FILE)) {
	                try {
	                    File parameterDatabaseFile = new File(args[x + 1]);
	                    parameters = new ParameterDatabase(parameterDatabaseFile.getAbsoluteFile());
	                } catch (IOException ex) {
	                    ex.printStackTrace();
	                }
	                break;
	            }
	        }
	        if (parameters == null) {
	            System.out.println("\nNot in a parameter Mode");//("\nNo parameter file was specified");
	            parameters = new ParameterDatabase();
	        }
	        return parameters;
	 }
	 
	 private void loadParameters(ParameterDatabase parameterDB) {
		 
		 global.setInitialVictimNumber(returnIntParameter(parameterDB, "initialVictimNumber",
                 global.getInitialVictimNumber()));
	 }
	
	 public int returnIntParameter(ParameterDatabase paramDB, String parameterName, int defaultValue) {
	        return paramDB.getIntWithDefault(new Parameter(parameterName), null, defaultValue);
	 }

	 public boolean returnBooleanParameter(ParameterDatabase paramDB, String parameterName, boolean defaultValue) {
	        return paramDB.getBoolean(new Parameter(parameterName), null, defaultValue);
	 }

	 double returnDoubleParameter(ParameterDatabase paramDB, String parameterName, double defaultValue) {
	        return paramDB.getDoubleWithDefault(new Parameter(parameterName), null, defaultValue);
	 }
/*	 
	 public int returnIntParameter(ParameterDatabase paramDB, String parameterName, int defaultValue) {
	     return paramDB.getIntWithDefault(new Parameter(parameterName), null, defaultValue);
	 }

	 public boolean returnBooleanParameter(ParameterDatabase paramDB, String parameterName, boolean defaultValue) {
	     return paramDB.getBoolean(new Parameter(parameterName), null, defaultValue);
	 }

	 double returnDoubleParameter(ParameterDatabase paramDB, String parameterName, double defaultValue) {
	     return paramDB.getDoubleWithDefault(new Parameter(parameterName), null, defaultValue);
	 }
*/	 
	  public class GlobalParamters {
		  public int initialVictimNumber = 5000; //min-1000
		  public int maximum_occupancy_Threshold = 1000; // arbitrary
		  public int intFloodDeep = 200; //area 
		  public double increaseFloodRate = 0.05; // area/cstep
		  public int healthFacilityCapacity = 100; // 100 person/day 
		  public double foodDischareRate = 0.8;  // tan/minute // proportion of total water capacity
		  public double foodCapacityInCenter = 0.7; // kg/day/person
		  public double foodCoverageRate = 0.3; // % food coverage 
		  public double foodRequirePerDay = 0.6; //0.6 kg/day 
		  public double probabilityOfEffectiveNessofmedicine = 0.9; //90% of the time
		  public int maximum_ignore_treat = 2;
		  public int maximum_water_Threshold_oldster = 250;
		  public int maximum_water_Threshold_middleage = 300;
		  public int maximum_water_Threshold_teenager = 350;
		  /*
		  public double movement_speed_oldster = 10; // kilometer/hour
		  public double movement_speed_minndleage = 20; // kilometer/hour
		  public double movement_speed_teenager = 30; // kilometer/hour
		  */
		  public double Minimum_Food_Requirement = 2;
		  public double Maximum_Food_Requirement = 3; // 15 liter per day -  for all uses
		  public int MaximumNumberRelative = 3;
		  //public int NumberOfAgentType = 3;
		  public String UtilityFileName = "src/FloodSim/ascii_util_2.txt";
		  public String RoadShapeFileName = "src/Road/L06_Transportation_MOT_road_merge_2011.shp";
		  public String RoadAsciiFileName = "";
		  
	  public void setInitialVictimNumber(int num) {
          this.initialVictimNumber = num;
      }	  
		  	  
	  public int getInitialVictimNumber() {
		  return initialVictimNumber;
	  }
		
      public void setMaximumHHOccumpancyPerField(int num) {
          this.maximum_occupancy_Threshold = num;
      }

      public int getMaximumHHOccumpancyPerField() {
          return maximum_occupancy_Threshold;

      }
      
      public void setMaxIgnoreTreat(int num){
    	  this.maximum_ignore_treat = num;
      }
      
      public int getMaxIgnoreTreat(){
    	  return maximum_ignore_treat;
      }
      
      public void setFloodDeep(int num){
    	  this.intFloodDeep = num;
      }
      
      public int getFloodDeep(){
    	  return intFloodDeep;
      }
      
      public void setIncreateFloodRate(double r){
    	  this.increaseFloodRate = r;
      }
      
      public double getIncreateFloodRate(){
    	  return increaseFloodRate;
      }
      
      public void setFoodRequirePerDay(double r){
    	  this.foodRequirePerDay = r;
      }
      
      public double getFoodRequirePerDay(){
    	  return foodRequirePerDay;
      }
      
      public void setMaxWaterThreadholdOldster(int num){
    	  this.maximum_water_Threshold_oldster = num;
      }
      
      public int getMaxWaterThreadholdOldster(){
    	  return maximum_water_Threshold_oldster;
      }
      
      public void setMaxWaterThreadholdMiddleAge(int num){
    	  this.maximum_water_Threshold_middleage = num;
      }
      
      public int getMaxWaterThreadholdMiddleAge(){
    	  return maximum_water_Threshold_middleage;
      }
      
      public void setMaxWaterThreadholdTeenager(int num){
    	  this.maximum_water_Threshold_teenager = num;
      }
      
      public int getMaxWaterThreadholdTeenager(){
    	  return maximum_water_Threshold_teenager;
      }
          
      public void setHeaalthFacilityCapacity(int ca) {
          this.healthFacilityCapacity = ca;
      }

      public int getHeaalthFacilityCapacity() {
          return healthFacilityCapacity;
      }
      
      // refill rate of each food
      public void setFoodDischareRatePerMinute(double w) {
          this.foodDischareRate = w;
      }

      public double getFoodDischareRatePerMinute() {
          return foodDischareRate * (foodCoverageRate * foodCapacityInCenter * this.getInitialVictimNumber()) / (1440 *12.0);   // 12 food center each / 1440 minute 
      }
		
      // determine water holding capacity of each borehole (per day)
      public void setFoodInCenterSupplyPerDay(double w) {

          this.foodCapacityInCenter = w;
      }

      public double getFoodInCenterSupplyPerDay() {
          return (foodCoverageRate * foodCapacityInCenter * this.getInitialVictimNumber()) / 12.0;   // 12 food center each 
      }
      
      public void setprobabilityOfEffectiveNessofmedicine(double er) {
          this.probabilityOfEffectiveNessofmedicine = er;
      }

      public double getprobabilityOfEffectiveNessofmedicine() {
          return probabilityOfEffectiveNessofmedicine;
      }
      
      // determine the maximum water requirement of agent per day
      public void setMaximumFoodRequirement(double f) {
          this.Maximum_Food_Requirement = f;
      }

      public double getMaximumFoodRequirement() {
          return Maximum_Food_Requirement;
      }
      
      // determine the minimum water requirement of agent per day
      public void setMinimumFoodRequirement(double w) {
          this.Minimum_Food_Requirement = w;
      }

      public double getMinimumFoodRequirement() {
          return Minimum_Food_Requirement;
      }
      
      public void setMaximumNumberRelative(int num) {
          this.MaximumNumberRelative = num;
      }

      public int getMaximumNumberRelative() {
          return MaximumNumberRelative;

      }
      /*
      public void setNumberOfAgentType(int num){
    	  this.NumberOfAgentType = num;
      }
      
      public int getNumberOfAgentType(){
    	  return NumberOfAgentType;
      }
      */
      public void setUtilityFileName(String name){
    	  this.UtilityFileName = name;
      }
      
      public String getUtilityFileName(){
    	  return UtilityFileName;
      }
      
      public void setRoadShapeFileName(String name){
    	  this.RoadShapeFileName = name;
      }
      
      public String getRoadShapeFileName(){
    	  return RoadShapeFileName;
      }
      
      public void setRoadAsciiFileName(String name){
    	  this.RoadAsciiFileName = name;
      }
      
      public String getRoadAsciiFileName(){
    	  return RoadAsciiFileName;
      }
      
	  } // end class GlobalParamters
	  
}
