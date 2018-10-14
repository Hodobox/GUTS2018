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
            default:
                regionValues = new Hashtable<String, Integer>();
        }

        // variable for caching
        String variableName = "trauma"; // todo: merge filter parameters and pass to analyzer as a variable name

        // update instance values and cache them into txt file
        if (this.mapDataGrid.isEmpty()) {
            this.mapDataGrid = regionValues;
        } else {
            // update the values of mapDataState with values from the new data chunk
            for (String region : regionValues.keySet()) {
                int newCount = this.mapDataGrid.getOrDefault(region, 0) + regionValues.getOrDefault(region, 0);
                this.mapDataGrid.put(region, newCount);
            }
        }

        System.out.println("Caching regional variable...");
        cacher.cacheRegionalVariable(regionType, variableName, mapDataGrid);
    }

    private Hashtable<String, Integer> aggregateByGrid(ArrayList<Record> entries,
                                                       int gridSize, double[] topLeftEdge, double[] botRightEdge) {
        Hashtable<String, Integer> dataByGrid = new Hashtable<String, Integer>();
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
