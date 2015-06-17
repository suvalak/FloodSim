package FloodSim;

import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import sim.field.continuous.*;
import sim.field.geo.GeomGridField;
import sim.field.geo.GeomVectorField;
import sim.field.geo.GeomGridField.GridDataType;
import sim.field.grid.IntGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Edge;
import sim.io.geo.ArcInfoASCGridImporter;
import sim.io.geo.ShapeFileImporter;
import sim.util.Bag;
import sim.util.geo.MasonGeometry;
import ec.util.MersenneTwisterFast;

public class RegionGenerator {

	 static int gridWidth = 0;
	 static int gridHeight = 0;
	 
	  public static void create(String Regionfile, String facilityfile, String roadfile, String Waterfile, Flood f, MersenneTwisterFast random) 
	 {
		 try {
			 
			 BufferedReader region = new BufferedReader(new FileReader(Regionfile));
			 String line;
			 
			 line = region.readLine();
			 String[] tokens = line.split("\\s+");
			 int width = Integer.parseInt(tokens[1]);
			 gridWidth = width;

			 line = region.readLine();
			 tokens = line.split("\\s+");
			 int height = Integer.parseInt(tokens[1]);
			 gridHeight = height;
			 
			 createGrids(width, height, f);
			 
			 for (int i = 0; i < 4; ++i) {
	                line = region.readLine();
	         }
			 
			 f.RegionSites.clear();// clear the bag
			 
			 for (int curr_row = 0; curr_row < height; ++curr_row) {
				 line = region.readLine();
				 
				 tokens = line.split("\\s+");
				 
				 for (int curr_col = 0; curr_col < width; ++curr_col) {
					 int regiontype = Integer.parseInt(tokens[curr_col]);
					 
					 GroundControl fieldUnit = null;
	                 fieldUnit = new GroundControl();
	                 
	                 if (regiontype > 0) {
	                	 fieldUnit.setFieldID(regiontype);
	                	 
	                	 if(regiontype == 1 || regiontype == 2 || regiontype == 3)
	                	 {
	                		 f.RegionSites.add(fieldUnit);
	                	 }
	                	 
	                	 if(regiontype == 1) //Central
	                	 {
	                            fieldUnit.setRegionID(1);
	                     }
	                     else if(regiontype == 2) //East
	                     {
	                            fieldUnit.setRegionID(2);                    
	                     }
	                     else if(regiontype == 3){ //Northeast
	                            fieldUnit.setRegionID(3);  
	                     }
                         else if(regiontype == 4){ //North
                            	fieldUnit.setRegionID(4);    
	                     }
                         else if(regiontype == 5){ //West
                        	 	fieldUnit.setRegionID(5);    
                         }
                         else if(regiontype == 6){ //South
                        	 	fieldUnit.setRegionID(6);    
                         }
                         else {
                        	 	fieldUnit.setRegionID(0);
                         }
	                 }
	                 
	                 else {
	                	 fieldUnit.setFieldID(0);
	                 }
	                 fieldUnit.setX(curr_col);
	                 fieldUnit.setY(curr_row);
	                 f.allRegions.field[curr_col][curr_row] = fieldUnit;
				 }
			 }
			 
			// read elev and change Region locations id to elev
			// URL uu = Flood.class.getResource(Regionfile);
			InputStream inputStream = Flood.class.getResourceAsStream("ascii_region.txt");
			ArcInfoASCGridImporter.read(inputStream, GridDataType.INTEGER, f.allRegionGeoGrid);
			 
			// read water grid
			BufferedReader water = new BufferedReader(new FileReader(Waterfile));
			for (int i = 0; i < 6; i++) {
				water.readLine();
            }
			
			int FloodDeep = f.params.global.getFloodDeep();
			
			for (int curr_row = 0; curr_row < height; ++curr_row) {
				    line = water.readLine();
	                tokens = line.split("\\s+");
	                              
	                for (int curr_col = 0; curr_col < width; ++curr_col) {
	                    
	                	Double waterArea = Double.parseDouble(tokens[curr_col]);   	                                    
		                GroundControl waterField = (GroundControl) f.allRegions.get(curr_col, curr_row);	                
	                    if (waterArea > 0) {	                    	                	
	                                                   
	                        waterField.setWaterID(1);
	                        waterField.setWaterArea((waterArea*100000) + FloodDeep);
	                       	                                      	                        
	                    }
	                    else {
	                    	waterField.setWaterID(0);
	                    	waterField.setWaterArea(FloodDeep);
		                 }
	                    
	                    waterField.setX(curr_col);
	                    waterField.setY(curr_row);
	                    f.allRegions.field[curr_col][curr_row] = waterField;
	                    //f.waterGrid.set(curr_col, curr_row, waterArea);
	                }
	                
			}
			
			//InputStream ipStream = Flood.class.getResourceAsStream("ascii_water.txt");
			//ArcInfoASCGridImporter.read(ipStream, GridDataType.DOUBLE, f.waterShape);
			
			
	        // now read facility grid
			 BufferedReader fac = new BufferedReader(new FileReader(facilityfile));
            // skip the irrelevant metadata
            for (int i = 0; i < 6; i++) {
                fac.readLine();
            }
            int countFoodC = 0;
            int countHealthC = 0;
            int countTerminalC = 0;
            int countOther = 0;
            for (int curr_row = 0; curr_row < height; ++curr_row) {
                line = fac.readLine();
                tokens = line.split("\\s+");

                for (int curr_col = 0; curr_col < width; ++curr_col) {
                    int facilitytype = Integer.parseInt(tokens[curr_col]);   
                                     
                    if (facilitytype > 0 && facilitytype <11) {
                        
                        Utility facility = new Utility(); 
                        GroundControl facilityField = (GroundControl) f.allRegions.get(curr_col, curr_row);
                        facility.setLoc(facilityField);
                        facilityField.setFacility(facility);
                        f.allFacilities.add(facilityField);
                        
           
                        if (facilitytype == 1 || facilitytype == 7 || facilitytype == 8) {                       
                            

                            facility.setUtilityID(1);                        
                            facilityField.setFood(f.params.global.getFoodInCenterSupplyPerDay());                           
                            f.foodCenter.add(facilityField);
                            countFoodC += 1;
                        } 
                        else if (facilitytype == 2 || facilitytype == 3 || facilitytype == 4 ) {
                          
                            facility.setUtilityID(2);  
                            f.healthCenters.add(facilityField);
                            countHealthC += 1;
                            
                        } 
                        else if (facilitytype == 5 || facilitytype == 6) {
                           
                            facility.setUtilityID(3);
                            f.terminalCenter.add(facilityField);
                            countTerminalC += 1;
                        }                        
                        else {
                            
                            facility.setUtilityID(5);
                            f.other.add(facilityField);
                            countOther += 1;
                        }                     
                        f.facilityGrid.setObjectLocation(facility, curr_col, curr_row);
                        
                    }
                }
            }
            
            
                   
			// now read road grid
           
            BufferedReader road = new BufferedReader(new FileReader(roadfile));
            
            // skip the irrelevant metadata
            for (int i = 0; i < 6; i++) {
                road.readLine();
            }
            
            for (int curr_row = 0; curr_row < height; ++curr_row) {
            	line = road.readLine();
            	
            	tokens = line.split("\\s+");
            	
            	for (int curr_col = 0; curr_col < width; ++curr_col) {
            		double r = Double.parseDouble(tokens[curr_col]); // no need
            		int roadID = (int)r *1000;
            		if (roadID >= 0) {                
                        f.roadGrid.set(curr_col, curr_row, roadID);
                    }
            	}
            }
		
			// now read elev file and store in bag
			// ...
			
			// read shape file		 
			Bag maskedRegion = new Bag();
			maskedRegion.add("RE_ROYIN");
            File file=new File("src/Road/Polbndry_2554_geo_send03_Prov.shp");
			//maskedRegion.add("Region");
			//File file=new File("src/Road/Polbndry_2554_geo_send03_region.shp");
            URL RegionShapUL = file.toURL();            
            ShapeFileImporter.read(RegionShapUL, f.regionShape, maskedRegion);
       
            // read water shape file	
            Bag maskedwater = new Bag();
            maskedwater.add("AREA");
            File file3=new File("src/Road/water_rtsd_latlong_edit.shp");
            URL waterShapUL = file3.toURL(); 
            ShapeFileImporter.read(waterShapUL, f.waterShape, maskedwater);
            
            // read ของ road บ้าง
            Bag masked = new Bag();
            String RoadFileName = f.params.global.getRoadShapeFileName();
            
            if (RoadFileName.equals("")){
            	RoadFileName = "src/Road/L06_Transportation_MOT_road_merge_2011.shp";
            }
            
            File file2=new File (RoadFileName);
            URL raodLinkUL = file2.toURL();
            ShapeFileImporter.read(raodLinkUL, f.roadLinks, masked);
            
            extractFromRoadLinks(f.regionShape, f.waterShape, f.roadLinks, f); // construct a network of roads
            
            f.closestNodes = setupNearestNodes(f);
		 
		 }catch (IOException ex) {
			 Logger.getLogger(RegionGenerator.class.getName()).log(Level.SEVERE, null, ex);
	     } 
		 
		 populate(random,f);
		 
		 int max = f.params.global.getMaximumNumberRelative();
         int[] numberOfFamilies = new int[f.allFamilies.numObjs];
		 
         for(int i=0;i<f.allFamilies.numObjs; i++){
        	 Family fm = (Family)f.allFamilies.objs[i];
        	 int tot = 0;
        	 if(f.allFamilies.numObjs > max){
                 tot = max;
             }
             
             else
                 tot = f.allFamilies.numObjs;
        	 
        	 int numOfRel =  1 + f.random.nextInt(tot -1);
        	 
        	// swap the array index
             for (int kk = 0; kk < numberOfFamilies.length; kk ++) {     
	             int idx =  f.random.nextInt(numberOfFamilies.length);
	             int temp = numberOfFamilies[idx];
	             numberOfFamilies[idx] = numberOfFamilies[i];
	             numberOfFamilies[i] = temp; 
             }
             
             for (int jj = 0; jj < numOfRel; jj++) {
            	 if(fm.equals(f.allFamilies.objs[numberOfFamilies[jj]]) !=true){
                     GroundControl l = ((Family)f.allFamilies.objs[numberOfFamilies[jj]]).getRegionLocation();        
                     fm.addRelative(l) ;
                  } 
             }
             
         }
	 }
	 
	 private static void createGrids(int width, int height, Flood floodsim) {
		 floodsim.allRegions = new ObjectGrid2D(width, height);
		 floodsim.allVictims = new Continuous2D(0.1, width, height);
		 floodsim.facilityGrid = new SparseGrid2D(width, height);
			 
		 floodsim.roadGrid = new IntGrid2D(width, height);
		 floodsim.nodes = new SparseGrid2D(width, height);
		 floodsim.closestNodes = new ObjectGrid2D(width, height);
		 floodsim.roadLinks = new GeomVectorField(width, height);
		 floodsim.regionShape =  new GeomVectorField(width, height);
		 floodsim.waterShape = new GeomVectorField(width, height);
		 
		 floodsim.allRegionGeoGrid = new GeomGridField();
		 
	 }
	 
	 // add households
	 private static void addAllVictims(int age, int sex, Family hh, MersenneTwisterFast random,Flood f) {
		 
		// int AgentType = f.params.global.getNumberOfAgentType();
		// final Color Agent1 = new Color(83,134, 139);
		// final Color Agent2 = new Color(255, 246, 143);
		 
		 //for (int i = 0 ; i < AgentType; i++){
			 
		 Victim newVictim = new Victim(age, sex, hh, hh.getRegionLocation(), hh.getRegionLocation(), random, f.allVictims);
	     hh.addMembers(newVictim);
	     hh.getRegionLocation().addVictim(newVictim);
	     
	     int rand = f.random.nextInt(100);
	     if (rand < 50){
	    	 newVictim.setHealthStatus(1);
	     }
	     else if (rand > 50 && rand < 95){
	    	 newVictim.setHealthStatus(2);
	     }
	     else{
	    	 newVictim.setHealthStatus(3);
	     }
	     newVictim.setHealthStatus(1);
	     newVictim.setCurrentActivity(0);
	     	     
	     newVictim.setFoodLevel(2*f.params.global.getMinimumFoodRequirement() + f.params.global.getMaximumFoodRequirement() * random.nextDouble());
	     //newVictim.setWaterLevel(f.params.global.getFloodDeep());
		     
		     
		 //    newVictim.setAgentType(i+1);
		   
		 //}
		 
	     newVictim.setStoppable(f.schedule.scheduleRepeating(newVictim, Victim.ORDERING, 1.0));
	 }
	 
	 // random searching of next parcel to populate houses
	 public static GroundControl nextAvailRegion(Flood floodsim) {
		 
		 int x = floodsim.random.nextInt(floodsim.RegionSites.numObjs);
		 while (((GroundControl) floodsim.RegionSites.objs[x]).isRegionOccupied(floodsim) == true){
			 x = floodsim.random.nextInt(floodsim.RegionSites.numObjs);
		 }
		 return (GroundControl) floodsim.RegionSites.objs[x];		 
	 }
	 
	// create victim - first hh
	 private static void populateVictim(MersenneTwisterFast random, Flood f){
		 
		    // UNHCR stat
	        // age distibution 
	        // 1-4 = 0.20; 5-11 = 0.25; 12-17 = 0.12; 18-59 = 0.40;>= 60 = 0.;

	        // family size
	        // 1 = 30% , 2 =12% , 3 = 11%, 4=13%, 5 =12%, 6 = 10%, >6= 12%

	        // proportion of teta = families/ total population = 8481/29772 ~ 0.3
		 
		 double teta = 0.3;
		 int totalVictim = f.params.global.getInitialVictimNumber();
		 
		 double[] prop = {0.30, 0.12, 0.11, 0.13, 0.12, 0.10, 0.06, 0.03, 0.01, 0.01, 0.01}; // proportion of household
	     int[] size = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // family size - all are zero
	     // family size ranges from 1  to 11   
	     
	     int count = 0;
	     int remain = 0;// remaining
	     int curTot = 0;
	     
	     for (int i = 0; i < size.length; i++) {
	    	 double x = prop[i] * totalVictim * teta;
	    	 int hh = (int) Math.round(x);
	    	 size[i] = hh;
	    	 curTot = curTot + ((i + 1) * hh);
	     }
	     
	     if(curTot > totalVictim){
             size[0] = size[0] - (curTot - totalVictim);
         }
         
		  if(curTot < totalVictim){
		     size[0] = size[0] + (totalVictim - curTot);
		 }
		  
		// calculate total hh size 
		  int ts = 0;
	      for (int i = 0; i < size.length; i++) {	            
	          ts = ts + size[i];
	        }
	      
	    // initalize array based on hh size   
	      int[] sizeDist = new int[ts];
	      
	   // add each hh size
	      int c = 0;
	      int k = 0;
	      for (int t = 0; t < size.length; t++) {
	          int sum = size[t];
	          c = c + sum;
	          for (int j = k; j < c; j++) {
	              sizeDist[j] = t + 1;
	          }
	          k = c;
	      }
	      
	        // swaping with random posiion
	        for (int i = 0; i < sizeDist.length; i++) {

	            int change = i + f.random.nextInt(sizeDist.length - i);
	            int holder = sizeDist[i];
	            sizeDist[i] = sizeDist[change];
	            sizeDist[change] = holder;

	          //  System.out.println ("hh size: "+ sizeDist[i]);      
	        }
	        
	        // UNHCR stat
	        // age distibution 
	        // 1-4 = 0.20; 5-11 = 0.25; 12-17 = 0.12; 18-59 = 0.40;>= 60 = 0.03;
	      
	        for (int a = 0; a < sizeDist.length; a++) {
	        	int counter  = 0;
	        	int tot = sizeDist[a];
	        	
	            counter = counter + tot;
	            if (tot != 0 && counter <= totalVictim ) {
	            	GroundControl fu = nextAvailRegion(f);
	            	Family hh = new Family(fu);
	            	f.allFamilies.add(hh);
	            	//hh.setWaterAtHome(tot * f.params.global.getMaximumWaterRequirement() + (1.5 *f.params.global.getMaximumWaterRequirement() *f.random.nextDouble()));
	            
	            	fu.addVictimHH(hh);
	            	
	            	double rn = f.random.nextDouble();
	            	int age = 0;
	                for (int i = 0; i < tot; i++) {
	                	// a household head need to be between 26-50;
	                	
	                	if (i == 0) {
	                		age = 26 + f.random.nextInt(25); // 18-59
	                	}
	                	else {
	                		if (rn <= 0.3) {
	                            age = 10 + f.random.nextInt(25);  // 10-25 age
	                        } else if (rn > 0.30 && rn <= 0.70) {
	                            age = 26 + f.random.nextInt(25);  // 26-50
	                        } else {
	                            age = 51 + f.random.nextInt(40); // 51 +
	                        }
	                	}
	                	
	                	 int sex = 0; // sex 50-50 chance
	                     if (f.random.nextDouble() > 0.5) {
	                         sex = 1;
	                     } 
	                     else {
	                         sex = 2;
	                     }
	                     
	                     addAllVictims(age,sex, hh, random,f);
	                }
	            }
	        }
	 }
	 
	 private static void populate(MersenneTwisterFast random,Flood f) {
		 populateVictim(random,f);
	 }
	 
	///  road network methods 
	  static void extractFromRoadLinks(GeomVectorField regionShape ,GeomVectorField waterShape,GeomVectorField roadLinks, Flood f) {
	        Bag geoms = roadLinks.getGeometries();
	        
	       // Envelope e = roadLinks.getMBR(); //minimum bounding rectangle
	        
	        Envelope globalMBR  = regionShape.getMBR();
	        
	        globalMBR.expandToInclude(waterShape.getMBR());
	        globalMBR.expandToInclude(roadLinks.getMBR());
	        
	        regionShape.setMBR(globalMBR);
	        waterShape.setMBR(globalMBR);
	        roadLinks.setMBR(globalMBR);
	        
	        double xmin = globalMBR.getMinX(); 
	        double ymin = globalMBR.getMinY(); 
	        double xmax = globalMBR.getMaxX(); 
	        double ymax = globalMBR.getMaxY(); 
	        int xcols = gridWidth - 1, ycols = gridHeight - 1;
	          
	        // extract each edge
	        for (Object o : geoms) {

	            MasonGeometry gm = (MasonGeometry) o;
	            if (gm.getGeometry() instanceof LineString) {
	                readLineString((LineString) gm.getGeometry(), xcols, ycols, xmin, ymin, xmax, ymax, f);
	            } else if (gm.getGeometry() instanceof MultiLineString) {
	                MultiLineString mls = (MultiLineString) gm.getGeometry();
	                for (int i = 0; i < mls.getNumGeometries(); i++) {
	                    readLineString((LineString) mls.getGeometryN(i), xcols, ycols, xmin, ymin, xmax, ymax, f);
	                }
	            }
	        }
	    }
	 
     static void readLineString(LineString geometry, int xcols, int ycols, double xmin,
            double ymin, double xmax, double ymax, Flood f) {

        CoordinateSequence cs = geometry.getCoordinateSequence();

        // iterate over each pair of coordinates and establish a link between
        // them
        Node oldNode = null; // used to keep track of the last node referenced
        for (int i = 0; i < cs.size(); i++) {

            // calculate the location of the node in question
            double x = cs.getX(i), y = cs.getY(i);
            int xint = (int) Math.floor(xcols * (x - xmin) / (xmax - xmin)), yint = (int) (ycols - Math.floor(ycols * (y - ymin) / (ymax - ymin))); // REMEMBER TO FLIP THE Y VALUE

            if (xint >= gridWidth) {
                continue;
            } else if (yint >= gridHeight) {
                continue;
            }

            // find that node or establish it if it doesn't yet exist
            Bag ns = f.nodes.getObjectsAtLocation(xint, yint);
            Node n;
            if (ns == null) {
                n = new Node(new GroundControl(xint, yint));
                f.nodes.setObjectLocation(n, xint, yint);
            } else {
                n = (Node) ns.get(0);
            }

            if (oldNode == n) // don't link a node to itself
            {
                continue;
            }

            // attach the node to the previous node in the chain (or continue if
            // this is the first node in the chain of links)

            if (i == 0) { // can't connect previous link to anything
                oldNode = n; // save this node for reference in the next link
                continue;
            }
           
            int weight = (int) n.location.distanceTo(oldNode.location); // weight is just
            // distance

            // create the new link and save it
            Edge e = new Edge(oldNode, n, weight);
            f.roadNetwork.addEdge(e);
            oldNode.links.add(e);
            n.links.add(e);

            oldNode = n; // save this node for reference in the next link
        }
    }
	 
	    static class Node {

	        GroundControl location;
	        ArrayList<Edge> links;

	        public Node(GroundControl l) {
	            location = l;
	            links = new ArrayList<Edge>();
	        }
	    }
	    
	    /**
	     * Used to find the nearest node for each space
	     * 
	     */
	    static class Crawler {

	        Node node;
	        GroundControl location;

	        public Crawler(Node n, GroundControl l) {
	            node = n;
	            location = l;
	        }
	    }
	    
	    /**
	     * Calculate the nodes nearest to each location and store the information
	     * 
	     * @param closestNodes
	     *            - the field to populate
	     */
	    static ObjectGrid2D setupNearestNodes(Flood f) {
	         
	        ObjectGrid2D closestNodes = new ObjectGrid2D(gridWidth, gridHeight);
	        ArrayList<Crawler> crawlers = new ArrayList<Crawler>();

	        for (Object o : f.roadNetwork.allNodes) {
	            Node n = (Node) o;
	            Crawler c = new Crawler(n, n.location);
	            crawlers.add(c);
	        }

	        // while there is unexplored space, continue!
	        while (crawlers.size() > 0) {
	            ArrayList<Crawler> nextGeneration = new ArrayList<Crawler>();

	            // randomize the order in which cralwers are considered
	            int size = crawlers.size();
	            
	            for (int i = 0; i < size; i++) {

	                // randomly pick a remaining crawler
	                int index = f.random.nextInt(crawlers.size());
	                Crawler c = crawlers.remove(index);
	              
	                // check if the location has already been claimed
	                Node n = (Node) closestNodes.get(c.location.getX(), c.location.getY());
	                        

	                if (n == null) { // found something new! Mark it and reproduce

	                    // set it
	                    closestNodes.set(c.location.getX(), c.location.getY(), c.node);

	                    // reproduce
	                    Bag neighbors = new Bag();

	                    f.allRegions.getNeighborsHamiltonianDistance(c.location.getX(), c.location.getY(),
	                            1, false, neighbors, null, null);

	                    for (Object o : neighbors) {
	                        GroundControl l = (GroundControl) o;
	                        //Location l = (Location) o;
	                        if (l == c.location) {
	                            continue;
	                        }
	                        Crawler newc = new Crawler(c.node, l);
	                        nextGeneration.add(newc);
	                    }
	                }
	                // otherwise just die
	            }
	            crawlers = nextGeneration;
	        }
	        return closestNodes;
	    }
	    
}
