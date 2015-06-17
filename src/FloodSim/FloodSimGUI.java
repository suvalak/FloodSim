package FloodSim;

import java.awt.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.dial.*;

import FloodSim.*;
import sim.display.*;
import sim.engine.*;
import sim.portrayal.grid.*;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.inspector.TabbedInspector;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Valuable;
import sim.util.geo.MasonGeometry;

public class FloodSimGUI extends GUIState{
	
	private Display2D display;
    private JFrame displayFrame;
    
   
	ContinuousPortrayal2D victimPortrayal = new ContinuousPortrayal2D();
	
	GeomVectorFieldPortrayal roadShapeProtrayal = new GeomVectorFieldPortrayal();
    GeomVectorFieldPortrayal regionShapeProtrayal = new GeomVectorFieldPortrayal();
    GeomVectorFieldPortrayal waterShapeProtrayal = new GeomVectorFieldPortrayal();
   
    SparseGridPortrayal2D facilPortrayal = new SparseGridPortrayal2D();
    
    sim.util.media.chart.TimeSeriesChartGenerator chartSeriesCholera;
    sim.util.media.chart.TimeSeriesChartGenerator chartSeriesCholeraNewly;
 
    sim.util.media.chart.TimeSeriesChartGenerator chartSeriesPopulation;
    
    sim.util.media.chart.ScatterPlotGenerator chartSeriesPopulation2;
	
	 public static void main(String[] args) 
	 {
		 
		 FloodSimGUI floodsimGUI = new FloodSimGUI(args);
		 Console console = new Console(floodsimGUI);
		 console.setVisible(true);
	 }
	 
	 public FloodSimGUI(String[] args)
		{
			super(new Flood(System.currentTimeMillis(),args));
		}
	 
	 public FloodSimGUI(SimState state)
	 {
		 super(state);
	 }
	 
	 public static String getName() 
	 {
		 return "Large-scale Flood Simulation";
	 }
	 
	// @Override
	public Object getSimulationInspectedObject() 
	{
	     return state;
	 }  // non-volatile
	
	public void start()
	{
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals() 
	{
		Flood flood = (Flood) state;
		
		
		// <---- Victim
		victimPortrayal.setField(flood.allVictims);
		
		OvalPortrayal2D vPortrayal = new OvalPortrayal2D(0.20)
		{
			
			final Color healthy = new Color(83, 134, 139); 
			final Color symptom = new Color(0,0,255);
			final Color sick = new Color(255,0,0); 
			final Color other = new Color(102,0,102);
			
			 // to draw each refugee type with differnet color
			//@Override
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
			{
				if ( object != null )
				{
					double cType = ((Valuable)object).doubleValue();
					
					 if(cType == 1){
	                    paint = healthy;
	                    }
					 else if (cType == 2){
						 paint = symptom;
					 }
					 else if (cType == 2){
						 paint = sick;
					 }
					 else{
	                     paint = other ;
	                    }
					 super.draw(object, graphics, info);
				}
				else
				{
					super.draw(object, graphics, info);
				}
			}		
		};
		victimPortrayal.setPortrayalForAll(vPortrayal);
		// --> End Victim
		
		// <-- Facility
		facilPortrayal.setField(flood.facilityGrid);
		RectanglePortrayal2D facPortrayal = new RectanglePortrayal2D(1.0, false)
		{
			final Color terminalCenter = new Color(0,0,205); //blue
			final Color healthCenter = new Color(255,0,0);  //red
			final Color foodCenter = new Color(173,255,47);  //green
			final Color other = new Color(255,255,255);
			
			 //@Override
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
            {
                if ( object != null )
                {
                    
                   double cType = ((Valuable)object).doubleValue();
                    if(cType == 1)
                    {paint = foodCenter;
                    
                    }
                    else
                    if (cType == 2){
                        paint = healthCenter;
                    }
                    else
                    if (cType == 3)
                    { 
                    	paint = terminalCenter;                
                    }
                    else
                    { 
                    	//paint = other ;
                    }
              
                    super.draw(object, graphics, info);
                }
                else
                {
                    super.draw(object, graphics, info);
                }
            }
        };
        
        facilPortrayal.setPortrayalForAll(facPortrayal);
		// --> End Facility
        
        // <-- Water
        waterShapeProtrayal.setField(flood.waterShape);
        GeomPortrayal gpw = new GeomPortrayal(true){
        	final Color water = new Color(30,144, 255); //light blue
        	
        	//@Override
	        public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
	        {
	        	if ( object != null )
	        	{
	        		MasonGeometry mg = (MasonGeometry) object;
	        		Double hydro = mg.getDoubleAttribute("AREA"); 
	        		if(hydro > 0)
                    {
	        			paint = water;
                    }
	        		super.draw(object, graphics, info);
	        	}
	        	else
	        	{
	        		super.draw(object, graphics, info);
	        	}
	        }
        };
        waterShapeProtrayal.setPortrayalForAll(gpw);
        // --> End Water
		
		// <-- Region
        regionShapeProtrayal.setField(flood.regionShape);
		GeomPortrayal gp = new GeomPortrayal(true){

			final Color c = new Color(255, 127, 0); // Orange
	        final Color e = new Color(71, 60, 139); // Purple
	        final Color ne = new Color(0, 205, 102); // Green
	        final Color n = new Color(220, 20, 60); // Pink
	        final Color w = new Color(255, 193, 37); // Yellow
	        final Color s = new Color(238, 0, 0); // Red 
	        final Color o = new Color(139, 99, 108); // Gray
	        
	        //@Override
	        public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
	        {
	        	
	        	if ( object != null )
	        	 	{
	        		MasonGeometry mg = (MasonGeometry) object;
	        		
	        		//String cType = mg.getStringAttribute("Region"); 
	        		String cType = mg.getStringAttribute("RE_ROYIN"); 
	        		// int cType = (Integer) cID.get(afterSize);
	        		
	        		if(cType.trim().equals("Central")){
	        			paint = c;
                      }
                    else if (cType.trim().equals("East")){
                        paint = e;                   
                      }
                    else if (cType.trim().equals("Northeast")){
                        paint = ne;
                      }
                    else if (cType.trim().equals("North")){
                        paint = n;
                      }
                    else if (cType.trim().equals("South")){
                        paint = s;
                      }
                    else if (cType.trim().equals("West")){
                        paint = w;
                      }
                    else{
                        paint = o ;
                    }
                  
                    super.draw(object, graphics, info);
                    
	        	    }
	        	else
	        	{
	                super.draw(object, graphics, info);
	            }
	             
	        }
	        
		};
			
		regionShapeProtrayal.setPortrayalForAll(gp);
		// --> End Region   
		
		roadShapeProtrayal.setField(flood.roadLinks);
		roadShapeProtrayal.setPortrayalForAll(new GeomPortrayal(Color.LIGHT_GRAY, false));
		
		display.reset();
		display.setBackdrop(Color.white);
		// redraw the display
        display.repaint();
            
	}
	
   // @Override
	public void init(Controller c) 
    {
		super.init(c);
		
		display = new Display2D(380, 760, this);
		
		display.attach(regionShapeProtrayal, "Region Layer");
		display.attach(roadShapeProtrayal, "Road Layer");
		display.attach(waterShapeProtrayal, "Water Layer");
		display.attach(facilPortrayal, "Utility");
		display.attach(victimPortrayal, "Victim");	
		
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);
	    displayFrame.setVisible(true);
	
	    
	    // ------------------- Portray activity chart
	    JFreeChart chart = ChartFactory.createBarChart("Victim's Activity", "Activity", "Percentage", ((Flood) this.state).dataset ,PlotOrientation.VERTICAL, false, false, false); 
	    		
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);
      
        CategoryPlot p = chart.getCategoryPlot();
        p.setBackgroundPaint(Color.WHITE);
        p.setRangeGridlinePaint(Color.red);
        
        // set the range axis to display integers only...  
        NumberAxis rangeAxis = (NumberAxis) p.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        int max = 100; //((Flood) this.state).getInitialRefugeeNumber();
        rangeAxis.setRange(0, max);
 
        ChartFrame frame = new ChartFrame("Activity Chart", chart);
        frame.setVisible(false);
        frame.setSize(400, 350);

        frame.pack();
        c.registerFrame(frame);
        
        // ------------------- Portray activity chart
        
        JFreeChart Victimchart = ChartFactory.createBarChart("Victim Leave", "Region", "Percentage of Total Population", ((Flood) this.state).VictimLeavedataset ,PlotOrientation.VERTICAL, false, false, false); 
        Victimchart.setBackgroundPaint(Color.WHITE);
        Victimchart.getTitle().setPaint(Color.BLACK);
	    
	    CategoryPlot pw = chart.getCategoryPlot();
        pw.setBackgroundPaint(Color.WHITE);
        pw.setRangeGridlinePaint(Color.CYAN);
        
        NumberAxis victimrangeAxis = (NumberAxis) pw.getRangeAxis();
        victimrangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        int Vmax = 100; //((Flood) this.state).getInitialRefugeeNumber();
        victimrangeAxis.setRange(0, Vmax);
        
        ChartFrame VictimLeaveframe = new ChartFrame("Migrate Chart", Victimchart);
        VictimLeaveframe.setVisible(false);
        VictimLeaveframe.setSize(400, 350);
        
        VictimLeaveframe.pack();
        c.registerFrame(VictimLeaveframe);
        
        /*
	    JFreeChart waterchart = ChartFactory.createBarChart("Water Level", "Region", "Level (Area)", ((Flood) this.state).waterdataset ,PlotOrientation.VERTICAL, false, false, false); 
	    waterchart.setBackgroundPaint(Color.WHITE);
	    waterchart.getTitle().setPaint(Color.BLACK);
	    
	    CategoryPlot pw = chart.getCategoryPlot();
        pw.setBackgroundPaint(Color.WHITE);
        pw.setRangeGridlinePaint(Color.CYAN);
        
        NumberAxis waterrangeAxis = (NumberAxis) pw.getRangeAxis();
        waterrangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        int Wmax = 100; //((Flood) this.state).getInitialRefugeeNumber();
        waterrangeAxis.setRange(0, Wmax);
        
        ChartFrame Waterframe = new ChartFrame("Water Level Chart", waterchart);
        Waterframe.setVisible(false);
        Waterframe.setSize(400, 350);
        
        Waterframe.pack();
        c.registerFrame(Waterframe);
        */
        
        // -------------------  Portray activity chart
        JFreeChart agechart = ChartFactory.createBarChart("Age Distribution", "Age  Group", "Percentage of Total Population", ((Flood) this.state).agedataset, PlotOrientation.VERTICAL, false, false, false);
        agechart.setBackgroundPaint(Color.WHITE);
        agechart.getTitle().setPaint(Color.BLACK);

        CategoryPlot pl = agechart.getCategoryPlot();
        pl.setBackgroundPaint(Color.WHITE);
        pl.setRangeGridlinePaint(Color.BLUE);
        
        // set the range axis to display integers only...  
        NumberAxis agerangeAxis = (NumberAxis) pl.getRangeAxis();
        agerangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        
        ChartFrame ageframe = new ChartFrame("Age Chart", agechart);
        ageframe.setVisible(false);
        ageframe.setSize(400, 350);

        ageframe.pack();
        c.registerFrame(ageframe);
        
        // -------------------  Portray activity chart
        JFreeChart famchart = ChartFactory.createBarChart("Family Household Size", "Size", "Total", ((Flood) this.state).familydataset, PlotOrientation.VERTICAL, false, false, false);
        famchart.setBackgroundPaint(Color.WHITE);
        famchart.getTitle().setPaint(Color.BLACK);
        
        CategoryPlot pf = famchart.getCategoryPlot();
        pf.setBackgroundPaint(Color.WHITE);
        pf.setRangeGridlinePaint(Color.BLUE);
        
     // set the range axis to display integers only...  
        NumberAxis famrangeAxis = (NumberAxis) pf.getRangeAxis();
        famrangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
 

        ChartFrame famframe = new ChartFrame("Family Household Size Chart", famchart);
        famframe.setVisible(false);
        famframe.setSize(400, 350);

        famframe.pack();
        c.registerFrame(famframe);
        
        // ----------------------------------------------- 
        
        Dimension dm = new Dimension(30,30);
        Dimension dmn = new Dimension(30,30);
        
        chartSeriesCholera = new sim.util.media.chart.TimeSeriesChartGenerator();
        chartSeriesCholera.createFrame();
        chartSeriesCholera.setSize(dm);
        chartSeriesCholera.setTitle("Health Status");
        chartSeriesCholera.setRangeAxisLabel("Number of People");
        chartSeriesCholera.setDomainAxisLabel("Minutes");
        chartSeriesCholera.setMaximumSize(dm);
        chartSeriesCholera.setMinimumSize(dmn);
        
        chartSeriesCholera.addSeries(((Flood) this.state).totalOutOfAreaSeries , null);
        chartSeriesCholera.addSeries(((Flood) this.state).totalHealthySeries , null);
        chartSeriesCholera.addSeries(((Flood) this.state).totalSickSeries , null);
        chartSeriesCholera.addSeries(((Flood) this.state).totalSymptomSeries , null);
        
        chartSeriesCholera.addSeries(((Flood) this.state).totalTotalPopSeries , null);

        
        JFrame frameSeries = chartSeriesCholera.createFrame(this);
        frameSeries.pack();
        c.registerFrame(frameSeries);
        
        chartSeriesCholeraNewly = new sim.util.media.chart.TimeSeriesChartGenerator();
        
        chartSeriesCholeraNewly.createFrame();
        chartSeriesCholeraNewly.setSize(dm);
        chartSeriesCholeraNewly.setTitle("Health Status - Newly Sicked");
        chartSeriesCholeraNewly.setRangeAxisLabel("Number of People");
        chartSeriesCholeraNewly.setDomainAxisLabel("Minutes");
        chartSeriesCholeraNewly.setMaximumSize(dm);
        chartSeriesCholeraNewly.setMinimumSize(dmn);
        
        chartSeriesCholeraNewly.addSeries(((Flood) this.state).totalHealthySeriesNewly , null);
        chartSeriesCholeraNewly.addSeries(((Flood) this.state).totalSickSeriesNewly , null);
        chartSeriesCholeraNewly.addSeries(((Flood) this.state).totalSymptomSeriesNewly , null);

        JFrame frameSeriesNewly = chartSeriesCholeraNewly.createFrame(this);
        frameSeriesNewly.pack();
        c.registerFrame(frameSeriesNewly);
        
     // population dynamics
        
        chartSeriesPopulation = new sim.util.media.chart.TimeSeriesChartGenerator();
     
        chartSeriesPopulation.resize(100, 50);
        chartSeriesPopulation.setTitle("Victim Population Dynamics");
        chartSeriesPopulation.setRangeAxisLabel(" Number of victims");
        chartSeriesPopulation.setDomainAxisLabel("Minutes");
        
        chartSeriesPopulation.addSeries(((Flood) this.state).totalTotalPopSeries , null);
        chartSeriesPopulation.addSeries(((Flood) this.state).totalOutOfAreaSeries , null);
        
        
        JFrame frameSeriesPop = chartSeriesPopulation.createFrame(this);
        frameSeries.pack();
        c.registerFrame(frameSeriesPop);
        
        StandardDialFrame dialFrame = new StandardDialFrame();
        DialBackground ddb = new DialBackground(Color.white);
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
        
        DialPlot plot = new DialPlot();
        plot.setView(0.0, 0.0, 1.0, 1.0);
        plot.setBackground(ddb);
        plot.setDialFrame(dialFrame);  
        
        plot.setDataset(0, ((Flood) this.state).hourDialer); 
        plot.setDataset(1,((Flood) this.state).dayDialer); 
        
        DialTextAnnotation annotation1 = new DialTextAnnotation("Hour");
        annotation1.setFont(new Font("Dialog", Font.BOLD, 14));
        annotation1.setRadius(0.1);  
        plot.addLayer(annotation1);
        
        DialValueIndicator dvi2 = new DialValueIndicator(1);
        dvi2.setFont(new Font("Dialog", Font.PLAIN, 22));
        dvi2.setOutlinePaint(Color.red);
        dvi2.setRadius(0.3);
        plot.addLayer(dvi2);
        
        DialTextAnnotation annotation2 = new DialTextAnnotation("Day");
        annotation2.setFont(new Font("Dialog", Font.BOLD, 18));
        annotation2.setRadius(0.4);  
        plot.addLayer(annotation2);
        
        StandardDialScale scale = new StandardDialScale(0.0, 23.99, 90, -360, 1.0,59);
        scale.setTickRadius(0.9);
        scale.setTickLabelOffset(0.15);
        scale.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        plot.addScale(0, scale);
        scale.setMajorTickPaint(Color.black);
        scale.setMinorTickPaint(Color.lightGray);
        
        DialPointer needle = new DialPointer.Pointer(0);
        plot.addPointer(needle);
       
        
        DialCap cap = new DialCap();
        cap.setRadius(0.10);
        plot.setCap(cap);
        
        JFreeChart chart1 = new JFreeChart(plot);    
        ChartFrame timeframe = new ChartFrame("Time Chart", chart1);
        timeframe.setVisible(false);
        timeframe.setSize(100, 100);
        timeframe.pack();
        c.registerFrame(timeframe);
        
/*      ...  
        Dimension dl = new Dimension(300,700);
        Legend legend = new Legend();
        legend.setSize(dl);
        
        JFrame legendframe = new JFrame();
        legendframe.setVisible(false);
        legendframe.setPreferredSize(dl);
        legendframe.setSize(300, 700);
        
        legendframe.setBackground(Color.white);
        legendframe.setTitle("Legend");
        legendframe.getContentPane().add(legend);   
        legendframe.pack();
        c.registerFrame(legendframe);
 */       
    }
	
  //  @Override
	public Inspector getInspector() {
       super.getInspector();
         TabbedInspector i = new TabbedInspector();


       i.addInspector(new SimpleInspector(
               ((Flood) state).params.global, this), "Paramters");
       return i;
   }

	 // @Override
		public void quit() {
	        super.quit();

	        if (displayFrame != null) {
	            displayFrame.dispose();
	        }
	        displayFrame = null;
	        display = null;	        
	        
	    }
/*		
		public static Object getInfo(){
			return "Flood";
		}
*/
}
