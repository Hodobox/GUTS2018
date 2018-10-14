package bigdata.DataLogic;

import bigdata.Main;
import bigdata.Record;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;
import bigdata.WorldMap.WorldMapData;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static bigdata.DataLogic.AnalysisMode.*;
import static bigdata.Main.cacher;

public class Analyzer {
    // map plot constants
    private static final int DEFAULT_GRID_SIZE = 32;

    // map plot vars
    private MapAggregation aggregationFormat;
    private Grid grid;
    private Hashtable<String, Integer> mapDataState = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> mapDataGrid = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> mapDataZIP = new Hashtable<String,Integer>();

    class Grid {
        private int gridSize;
        private int height;
        private int width;
        private double startPoint;
        private double realWidth;
        private double realHeight;
        private double boxSize;

        public Grid(int gridSize, double[] topLeftEdge, double[] botRightEdge) {
            this.gridSize = gridSize;
            this.height = gridSize;
            this.width = gridSize;

            this.realWidth = Math.abs(topLeftEdge[0] - botRightEdge[0]);
            this.realHeight = Math.abs(topLeftEdge[1] - botRightEdge[0]);

            if (realWidth <= realHeight) {
                this.boxSize = realHeight/ gridSize;
            } else {
                this.boxSize = realWidth  / gridSize;
            }
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public double getBoxSize() {
            return boxSize;
        }
    }

    // general functionality
    private ArrayList<AnalysisMode> targetModes;

    public Analyzer(MapAggregation aggregationFormat) {
        // this.addTargetMode(AnalysisMode.Geography);
        this.aggregationFormat = aggregationFormat;
        WorldMapData.processStateZipRanges();
    }

    public Hashtable<String, Integer> getMapDataState() {
        return this.mapDataState;
    }
    public String getMapDataGrid(String regionType, String variable) throws IOException {
        Hashtable<String, Integer> cachedData = cacher.loadFromFile(
                cacher.workpath + "cache_regionalVariables" + "/" + regionType + ".txt").get(variable);

        ArrayList<double[]> centersCoords = new ArrayList<>();
        ArrayList<Integer> regionValues = new ArrayList<>();
        int minValue = 0;
        int maxValue = 0;

        for (int x = 0; x < grid.getWidth(); x++){
            for (int y = 0; y < grid.getHeight(); y++){
                // calculate centre of square coordinates
                double[] coords = {grid.getBoxSize() * x - grid.getBoxSize() * 0.5,
                                    grid.getBoxSize() * y - grid.getBoxSize() * 0.5};
                centersCoords.add(coords);

                // process regions magnitudes
                int regionValue = cachedData.getOrDefault((Integer.toString(x) + ":" + Integer.toString(y)), 0);
                if ((regionValue != 0 && minValue == 0) || (regionValue != 0 && regionValue < minValue)) {minValue = regionValue;}
                if (regionValue > maxValue) {maxValue = regionValue;}
                regionValues.add(regionValue);
            }
        }

        String plotData = "";
        int scale = maxValue / 100;
        if(scale == 0)
        	scale = 1;

        // get list of coordinates with higher magnitude points represented by multiple of repeated coordinates
        for (int i = 0; i < centersCoords.size(); i++) {
            int multiple = (int) Math.ceil(regionValues.get(i) / scale);
            for (int j = 0; j < multiple; j++ ){
                plotData += Double.toString(centersCoords.get(i)[0]) + "," + Double.toString(centersCoords.get(i)[1]) + "\n";
            }
        }

        return plotData;
    }

    /* process list of data entries pre-filtered by parser into data structure
       usable by map plotter */
    public void processData(ArrayList<Record> entries) {
        System.out.println("I am processing " + entries.size() + " entries");

        MapAggregation format = this.aggregationFormat;
        Hashtable<String, Integer> regionValues;

        String regionType = "";

        switch(format){
            case Grid:
                int gridSize = DEFAULT_GRID_SIZE;
                regionType = "grid" + gridSize;
                double[] topleft = {133.0, 52.0};
                double[] botrgiht = {-70.0, 20.0};
                regionValues = aggregateByGrid(entries, gridSize, topleft, botrgiht);
                break;
            case US_State:
                regionType = "states";
                String[] states = WorldMapData.US_STATES; // Get list of us states with codes
                regionValues = aggregateByState(entries, states);
                break;
            case ZIP:
            	regionType = "zip";
            	regionValues = new Hashtable<String,Integer> ();
            default:
                regionValues = new Hashtable<String, Integer>();
        }

        for(Record rec : entries)
        {
        	String recZip = rec.postCode;
        	int cur = 0;
        	if(mapDataZIP.contains(rec.postCode)) cur = mapDataZIP.get(rec.postCode);
        	this.mapDataZIP.put(rec.postCode,cur+1);
        }
        
       // System.out.println("Caching regional variable...");
      //  cacher.cacheRegionalVariable(regionType, variableName, mapDataGrid);
    }

    private Hashtable<String, Integer> aggregateByGrid(ArrayList<Record> entries,
                                                       int gridSize, double[] topLeftEdge, double[] botRightEdge) {
        Hashtable<String, Integer> dataByGrid = new Hashtable<String, Integer>();
        
        if (this.grid == null)
        	this.grid = new Grid(gridSize, topLeftEdge, botRightEdge);

        for (Record entry : entries) {
            try {
                double[] coords = Main.coordinator.getCoordinates(entry.postCode);

                int xBukket = (int) (Math.floor(Math.abs(coords[0] / grid.boxSize))) + 1;
                int yBukket = (int) (Math.floor(Math.abs(coords[1] / grid.boxSize))) + 1;

                String key = Integer.toString(xBukket) + ":" + Integer.toString(yBukket);
                if (dataByGrid.get(key) == null) {
                    dataByGrid.put(key, 1);
                } else {
                    dataByGrid.put(key, dataByGrid.get(key) + 1);
                }

            } catch (Exception e) {
                System.out.println("Exception caught while trying to update data for: " + e.getMessage());
                System.out.println("faulty line: " + entry);
            }
        }

        return dataByGrid;
    }

    private Hashtable<String, Integer> aggregateByState(ArrayList<Record> entries, String[] states) {
        Hashtable<String, Integer> dataByState = new Hashtable<String, Integer>();

        // calculate number of incidents of given data per state
        for (String state : states) {
            dataByState.put(state, 0);
        }
        for (Record entry : entries) {
            try {
                String state = WorldMapData.getStateByZip(entry.postCode);
                dataByState.put(state, dataByState.get(state) + 1);
            } catch (Exception e) {
                System.out.println("Exception caught while trying to update data for state: " + e.getMessage());
                System.out.println("faulty line: " + entry);
            }
        }

        return dataByState;
    }
  
    
    public String getMapDataZIP()
    {
    	StringBuilder sb = new StringBuilder();
    	Coordinator coor = new Coordinator();
    	for(String s: this.mapDataZIP.keySet())
    	{
    		double[] c = coor.getCoordinates(s);
    		
    		for(int i=0;i<this.mapDataZIP.get(s);i++)
    		{
    			sb.append(c[0]);
    			sb.append(",");
    			sb.append(c[1]);
    			sb.append("\n");
    		}
    	}
    	
    	return sb.toString();
    }

    // Target modes which are not gonna be implemented because we don't have gui :`(
    public boolean checkTargetModes(AnalysisMode[] targetModes) {
        HashSet<AnalysisMode[]> ModeComplexes = new HashSet<AnalysisMode[]>();

        AnalysisMode[] TimeLocation = {Geography, Time}; // nothing to show but valid
        AnalysisMode[] NumericTime = {Numeric, Time}; // graph of numeric value over time
        AnalysisMode[] NumericLocation = {Numeric, Geography}; // color scale of value on the map, refine with aggregation mode
        AnalysisMode[] NumericTimeLocation = {Numeric, Geography, Time}; // color scale + time slider
        AnalysisMode[] QualitativeNumeric = {Qualitative, Numeric}; // bar chart, pie chart, histogram
        AnalysisMode[] NumericNumeric = {Numeric, Numeric}; // graph x against y

        ModeComplexes.add(TimeLocation);
        ModeComplexes.add(NumericTime);
        ModeComplexes.add(NumericLocation);
        ModeComplexes.add(NumericTimeLocation);
        ModeComplexes.add(QualitativeNumeric);
        ModeComplexes.add(NumericNumeric);

        if (ModeComplexes.contains(targetModes)) {
            return true;
        }
        return false;
    }
    public void addTargetMode(AnalysisMode targetMode) {
        this.targetModes.add(targetMode);
    }
    public void deleteTargetMode(AnalysisMode targetMode) {
        if (this.targetModes.contains(targetMode)) {
            this.targetModes.remove(targetMode);
        }
    }
}
