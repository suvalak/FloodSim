package FloodSim;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.xy.XYSeries;
import org.omg.Dynamic.Parameter;

import sim.engine.MakesSimState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.geo.GeomGridField;
import sim.field.geo.GeomVectorField;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Network;
import sim.util.Bag;

public class Flood extends SimState {
	
    public  ObjectGrid2D allRegions; // The model environment - holds fields ( parcels)
    public GeomGridField allRegionGeoGrid;
    public Continuous2D allVictims; // Victim agents 
    public SparseGrid2D facilityGrid;// facilities: schools, health center, borehol etc
    public IntGrid2D roadGrid; // road in grid- for navigation 
    public ObjectGrid2D waterGrid;
    public GeomVectorField roadLinks;
	public GeomVectorField regionShape;
	public GeomVectorField waterShape;
    public SparseGrid2D nodes;
    public ObjectGrid2D closestNodes; // the road nodes closest to each of the										// locations
    Network roadNetwork = new Network();
	
	
	public final Parameters params;
	private int totalSick;
    private int totalSynpton;
    private int totalHealthy;
    private int totalSickNewly;
    private int totalSynptonNewly;
    private int totalHealthyNewly;
	public int[] RegionSick;
    public int[] RegionSynpton;
    public int[] RegionHealthy;
    private int[] totalActivity;
    private double[] waterRegion;
    
    // agent health status
    private static final long serialVersionUID = -5966446373681187141L;
    public XYSeries totalHealthySeries = new XYSeries("Healthy"); // shows  number of Healthy agents
    public XYSeries totalSymptomSeries = new XYSeries("Symptom");
    public XYSeries totalSickSeries = new XYSeries(" Sick"); //shows number of sick agents
   
    
    //public XYSeries rainfallSeries = new XYSeries(" Rainfall"); // 
    public XYSeries totalHealthySeriesNewly = new XYSeries("Newly Healthy"); // shows  number of Newly Healthy agents
    public XYSeries totalSymptomSeriesNewly = new XYSeries("Newly Symptom");
    public XYSeries totalSickSeriesNewly = new XYSeries("Newly Sick"); //shows number of Newly Sick agents
    public XYSeries totalTotalPopSeries = new XYSeries(" Total"); // shows number of dead agents
    public XYSeries totalOutOfAreaSeries = new XYSeries(" Out of Area"); // shows number of dead agents
    
    //public XYSeries totalBacteriaLoadSeries = new XYSeries(" Total vibrio Cholerae /million"); // shows number of recovered agents
    
	DefaultCategoryDataset dataset = new DefaultCategoryDataset(); //
	DefaultCategoryDataset VictimLeavedataset = new DefaultCategoryDataset();
	DefaultCategoryDataset agedataset = new DefaultCategoryDataset();// shows age structure of agents 
	DefaultCategoryDataset familydataset = new DefaultCategoryDataset(); // shows family size 
	DefaultValueDataset hourDialer = new DefaultValueDataset(); // shows the current hour
	DefaultValueDataset dayDialer = new DefaultValueDataset(); // counts
	
    public int totalgridWidth = 10;
    public int totalgridHeight = 10;
    
    public Bag allFamilies; // holding all families
	public Bag RegionSites; // hold Region sites
	
	public  Bag allFacilities;
	public  Bag healthCenters; 
	public  Bag terminalCenter; 
	public  Bag foodCenter;
	public  Bag other;
	public  double FoodInCenterSupplyPerDay ;
	
	
	Utility fac; //schduling food refill
	TimeManager tm = new TimeManager();
	
	
	public Flood(long seed, String [] args){
		super(seed);
		params  = new Parameters(args);
		fac = new Utility();
		allFamilies = new Bag();
		RegionSites = new Bag();
		allFacilities = new Bag();
		healthCenters = new Bag();
		foodCenter = new Bag();
		terminalCenter = new Bag();
		other = new Bag();
		allRegionGeoGrid = new GeomGridField();
		
		totalActivity = new int[6];
		RegionHealthy = new int[6];
        RegionSynpton = new int[6];
        RegionSick = new int[6];
        waterRegion = new double[6];
	}
	
//	@Override
	public void start() 
	{
		super.start();
		
		
		String facilityAsciiPath =  this.params.global.getUtilityFileName() ;
		String roadAsciiPath = this.params.global.getRoadAsciiFileName();
		
		if (facilityAsciiPath.equals("")){
			facilityAsciiPath = "src/FloodSim/ascii_faci_2.txt";
		}
		
		if (roadAsciiPath.equals("")){
			roadAsciiPath = "src/FloodSim/ascii_road.txt";
		}
		
		RegionGenerator.create("src/FloodSim/ascii_region.txt", facilityAsciiPath,roadAsciiPath,"src/FloodSim/ascii_water.txt", this, this.random);

	
		schedule.scheduleRepeating(fac, Utility.ORDERING, 1);
	
		 Steppable chartUpdater = new Steppable() {
			 
			//@Override
			 public void step(SimState state) {
				 
				 Bag vm = allVictims.getAllObjects(); // getting all refugees
				 int[] sumAct = {0, 0, 0, 0}; // adding each activity and puting the value in array
			     //double[] WaterRank = {0, 0, 0, 0, 0, 0}; 
				 int[] sumVictimLeave = {0, 0, 0, 0, 0, 0}; 
				 int[] sumAge = {0, 0, 0}; // adding agent all agents whose age falls in a given age-class
				 
				 int[] sumfamSiz = {0, 0, 0, 0, 0, 0, 0};  // adding all agent families based o their family size
			 
				 int totalHealthy = 0;
				 int totalSymptom = 0;
				 int totalSick = 0;
				 
				 int totalHealthyNewly = 0;
				 int totalSymptomNewly = 0;
				 int totalSickNewly = 0;
				 
				 // by region
				 int totalHealthyCentral = 0;
				 int totalSymptomCentral = 0;
				 int totalSickCentral = 0;
				 
				 int totalHealthyEast = 0;
				 int totalSymptomEast = 0;
				 int totalSickEast = 0;
				 
				 int totalHealthyNortheast = 0;
				 int totalSymptomNortheast = 0;
				 int totalSickNortheast = 0;
				 
				 int totalHealthyNorth = 0;
				 int totalSymptomNorth = 0;
				 int totalSickNorth = 0;
				 
				 int totalHealthyWest = 0;
				 int totalSymptomWest = 0;
				 int totalSickWest = 0;
				 
				 int totalHealthySouth = 0;
				 int totalSymptomSouth = 0;
				 int totalSickSouth = 0;
			 
                 // accessing all families and chatagorize them based on their size
                 for (int i = 0; i < allFamilies.numObjs; i++) {
                    Family fm = (Family) allFamilies.objs[i];
                  
                    int siz = 0;
                    if (fm.getMembers().numObjs > 6) { // aggregate all families of  >6 family size 
                        siz = 6;
                    } else {
                        siz = fm.getMembers().numObjs - 1;

                    }
                    sumfamSiz[siz] += 1;
                 }
	                
	                
				 int none = 0;
				 // accessing each agent 
				 for (int i = 0; i < vm.numObjs; i++) {
					 Victim v = (Victim) vm.objs[i];					 
					 sumAct[v.getCurrentActivity()] += 1; // current activity
					 int age = ageClass(v.getAge()); // age class of agent i 
				 
					 sumAge[age] += 1;
									 
					 if (v.getHome().getRegionID() == 1) {

	                        if (v.getHealthStatus() == 1) {
	                            totalHealthyCentral = totalHealthyCentral + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                            totalSymptomCentral = totalSymptomCentral + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                            totalSickCentral = totalSickCentral + 1;
	                        } else {
	                            none = 0;
	                        }
	                        
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[0] += 1;
	                        }
	                        
	                        /*
	                     if (v.getPosition().getWaterID() == 0){
	                    	  WaterRank[0]= v.getWaterLevel();
	                     }
	                      */          
	                  }
					 
					 if (v.getHome().getRegionID() == 2) {

	                        if (v.getHealthStatus() == 1) {
	                        	totalHealthyEast = totalHealthyEast + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                        	totalSymptomEast = totalSymptomEast + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                        	totalSickEast = totalSickEast + 1;
	                        } else {
	                            none = 0;
	                        }
	                        /*
	                        if (v.getPosition().getWaterID() == 0){
	                        	 WaterRank[1]= v.getWaterLevel();
	                        } 
	                        */
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[1] += 1;
	                        }
	                   }
					 
					 if (v.getHome().getRegionID() == 3) {

	                        if (v.getHealthStatus() == 1) {
	                        	totalHealthyNortheast = totalHealthyNortheast + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                        	totalSymptomNortheast = totalSymptomNortheast + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                        	totalSickNortheast = totalSickNortheast + 1;
	                        } else {
	                            none = 0;
	                        }
	                        /*
	                        if (v.getPosition().getWaterID() == 0){
	                        	WaterRank[2]= v.getWaterLevel();
	                        }
	                        */
	                        
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[2] += 1;
	                        }
	                        
	                   }
					 
					 if (v.getHome().getRegionID() == 4) {

	                        if (v.getHealthStatus() == 1) {
	                        	totalHealthyNorth = totalHealthyNorth + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                        	totalSymptomNorth = totalSymptomNorth + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                        	totalSickNorth = totalSickNorth + 1;
	                        } else {
	                            none = 0;
	                        }
	                       /* 
	                        if (v.getPosition().getWaterID() == 0){
	                        	WaterRank[3]= v.getWaterLevel();
	                        }
	                        */
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[3] += 1;
	                        }
	                        
	                   }
					 
					 if (v.getHome().getRegionID() == 5) {

	                        if (v.getHealthStatus() == 1) {
	                        	totalHealthyWest = totalHealthyWest + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                        	totalSymptomWest = totalSymptomWest + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                        	totalSickWest = totalSickWest + 1;
	                        } else {
	                            none = 0;
	                        }
	                        /*
	                        if (v.getPosition().getWaterID() == 0){
	                        	 WaterRank[4]= v.getWaterLevel();
	                        }
	                        */
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[4] += 1;
	                        }
	                       
	                   }
					 
					 if (v.getHome().getRegionID() == 6) {

	                        if (v.getHealthStatus() == 1) {
	                        	totalHealthySouth = totalHealthySouth + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                        	totalSymptomSouth = totalSymptomSouth + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                        	totalSickSouth = totalSickSouth + 1;
	                        } else {
	                            none = 0;
	                        }
	                        /*
	                        if (v.getPosition().getWaterID() == 0){
	                        	 WaterRank[5]= v.getWaterLevel();
	                        }
	                        */
	                        if (v.getCurrentActivity() == 3){
	                        	sumVictimLeave[5] += 1;
	                        }
	                       
	                   }
					 
					     // total health status

	                    if (v.getHealthStatus() == 1) {
	                    	totalHealthy = totalHealthy + 1;
	                    } else if (v.getHealthStatus() == 2) {
	                    	totalSymptom = totalSymptom + 1;
	                    } else if (v.getHealthStatus() == 3) {
	                    	totalSick = totalSick + 1;
	                    } else {
	                        none = 0;
	                    }
	                    
	                    if (v.getHealthStatus() != v.getPrevHealthStatus()) {
	                        if (v.getHealthStatus() == 1) {
	                            totalHealthyNewly = totalHealthyNewly + 1;
	                        } else if (v.getHealthStatus() == 2) {
	                            totalSymptomNewly = totalSymptomNewly + 1;
	                        } else if (v.getHealthStatus() == 3) {
	                            totalSickNewly = totalSickNewly + 1;
	                        } else {
	                            none = 0;
	                        }
	                    }
					 
				 }
				 
				setNumberOfHealthyNewly(totalHealthyNewly);
				setNumberOfSymptonNewly(totalSymptomNewly);
                setNumberOfSickNewly(totalSickNewly);
                
                setNumberOfHealthy(totalHealthy);
                setNumberOfSympton(totalSymptom);
                setNumberOfSick(totalSick);
                             	 
                RegionHealthy[0] = totalHealthyCentral;
                RegionHealthy[1] = totalHealthyEast;
                RegionHealthy[2] = totalHealthyNortheast;
                RegionHealthy[3] = totalHealthyNorth;
                RegionHealthy[4] = totalHealthyWest;
                RegionHealthy[5] = totalHealthySouth;

                RegionSynpton[0] = totalSymptomCentral;
                RegionSynpton[1] = totalSymptomEast;
                RegionSynpton[2] = totalSymptomNortheast;
                RegionSynpton[3] = totalSymptomNorth;
                RegionSynpton[4] = totalSymptomWest;
                RegionSynpton[5] = totalSymptomSouth;

                RegionSick[0] = totalSickCentral;
                RegionSick[1] = totalSickEast;
                RegionSick[2] = totalSickNortheast;
                RegionSick[3] = totalSickNorth;
                RegionSick[4] = totalSickWest;
                RegionSick[5] = totalSickSouth;

                setTotalActivity(sumAct); // set activity array output
                
                String actTitle = "Activity"; // row key - activity
                String[] activities = new String[]{"At Home", "Food C.", "Health C.", "Terminal C."};
            
                // percentage - agent activity by type
                for (int i = 0; i < sumAct.length; i++) {
                    dataset.setValue(sumAct[i] * 100 / allVictims.getAllObjects().numObjs, actTitle, activities[i]);
                }
               /* 
                String waterTitle = "Water Level";
                String[] waterL = new String[]{"Center", "East", "Northeast", "North", "West", "South"};
                for (int i = 0; i < WaterRank.length; i++){
                	waterdataset.setValue(WaterRank[i] , waterTitle, waterL[i]);
                }
                */
                
                String VictimLeaveTitle = "Total of Refugee Leave";
                String[] VictimL = new String[]{"Center", "East", "Northeast", "North", "West", "South"};
                for (int i = 0; i < sumVictimLeave.length; i++){
                	VictimLeavedataset.setValue(sumVictimLeave[i] * 100 / allVictims.getAllObjects().numObjs, VictimLeaveTitle, VictimL[i]);
                }
                
                
                String ageTitle = "Age Group";
                String[] ageC = new String[]{"10-25", "26-50", "50 +"};
                
                // ageset
                for (int i = 0; i < sumAge.length; i++) {
                    agedataset.setValue(sumAge[i] * 100 / allVictims.getAllObjects().numObjs, ageTitle, ageC[i]);
                }
                
                String famTitle = "Household Size";
                String[] famC = new String[]{"1", "2", "3", "4", "5", "6", "6+"};

                // family size
                for (int i = 0; i < sumAge.length; i++) {
                    familydataset.setValue(sumfamSiz[i], famTitle, famC[i]);
                }
               
                int totOutOfArea = countOutOfArea();
                totalTotalPopSeries.add((state.schedule.time()), allVictims.getAllObjects().numObjs);
                totalOutOfAreaSeries.add((state.schedule.time()), totOutOfArea);
                // health status - percentage 
                
                totalHealthySeries.add((state.schedule.time()), (totalHealthy));
                totalSymptomSeries.add((state.schedule.time()), (totalSymptom));
                totalSickSeries.add((state.schedule.time()), (totalSick));
			
                totalHealthySeriesNewly.add((state.schedule.time()), (totalHealthyNewly));
                totalSymptomSeriesNewly.add((state.schedule.time()), (totalSymptomNewly));
                totalSickSeriesNewly.add((state.schedule.time()), (totalSickNewly));
              
                int m = ((int) state.schedule.time()) % 60;
                
                double t = (tm.currentHour((int) state.schedule.time())) + (m / 60.0);
                int h = 1 + tm.dayCount((int) state.schedule.time()); //
                hourDialer.setValue(t);
                dayDialer.setValue(h);
			 }
		 };
		 
		 schedule.scheduleRepeating(chartUpdater);
		 
		 
	}
	
    // age class or ageset
    private int ageClass(int age) {
        int a = 0;

        if (age <= 25) {
            a = 0;
        } else if (age > 25 && age < 50) {
            a = 1;
        } else if (age >= 50) {
            a = 2;
        }

        return a;
    }
	
    /*
     * parameters getter and setter methods
     */
	
    public void killVictim(Victim v) {
    	v.getFamily().removeMembers(v);

        if (v.getFamily().getMembers().numObjs == 0) {
            allFamilies.remove(v.getFamily());
        }
        
        allVictims.remove(v);
    }
    
    public void setNumberOfSick(int sk) {
    	
        this.totalSick = sk;
    }

    public int getNumberOfSick() {
    	
        return totalSick;
    }
    
    public void setNumberOfSympton(int syn) {
    	
        this.totalSynpton = syn;
    }

    public int getNumberOfSympton() {
    	
        return totalSynpton;
    }
    
    public void setNumberOfHealthy(int hth) {
    	
        this.totalHealthy = hth;
    }

    public int getNumberOfHealthy() {
    	
        return totalHealthy;
    }
    
    public void setNumberOfSickNewly(int sk) {
    	
        this.totalSickNewly = sk;
    }

    public int getNumberOfSickNewly() {
    	
        return totalSickNewly;
    }
    
    public void setNumberOfSymptonNewly(int syn) {
    	
        this.totalSynptonNewly = syn;
    }

    public int getNumberOfSymptonNewly() {
    	
        return totalSynptonNewly;
    }
	 
     public void setNumberOfHealthyNewly(int hth) {
    	
        this.totalHealthyNewly = hth;
     }

     public int getNumberOfHealthyNewly() {
    	
        return totalHealthyNewly;
     }
     
     public void setTotalActivity(int[] rec) {
    	 
         this.totalActivity = rec;
     }

     public int[] getTotalActivity() {

         return totalActivity;
     }
     
     int PrevPop = 0;
     int curPop = 0;
     public int countOutOfArea() {
         int death = 0;

         int current = allVictims.getAllObjects().numObjs;
         PrevPop = curPop;
         death = PrevPop - current;
         curPop = current;
         if (death < 0) {
             death = 0;
         }
         return death;
     }
     
	 public static void main(String[] args)
	 {
		 doLoop(new MakesSimState()
		 {
			// @Override
	            public SimState newInstance(long seed, String[] args)
	            {

	                return new Flood(seed, args);
	            }

	          //  @Override
	            public Class simulationClass()
	            {
	                return Flood.class;
	            }
		 },args);
		 
		 System.exit(0);
	 }
	 
//	   @Override
		public void finish() {
	        super.finish();
	    }
}
