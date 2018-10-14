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

public class Analyzer {
    // map plot constants
    private static final int DEFAULT_GRID_SIZE = 6;

    // map plot vars
    private MapAggregation aggregationFormat;
    private Hashtable<String, Integer> mapDataState = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> mapDataGrid = new Hashtable<String, Integer>();

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
    public Hashtable<String, Integer> getMapDataGrid() {
        return this.mapDataGrid;
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

        for (String key : regionValues.keySet()) {
            System.out.println(key + " " + regionValues.get(key));
        }
        for (String key : this.mapDataState.keySet()) {
            System.out.println(key + " " + this.mapDataState.get(key));
        }

        // update instance values and cache them into txt file
        if (this.mapDataState.isEmpty()) {
            this.mapDataState = regionValues;
        } else {
            // update the values of mapDataState with values from the new data chunk
            for (String region : this.mapDataState.keySet()) {
                int newCount = this.mapDataState.getOrDefault(region, 0) + regionValues.getOrDefault(region, 0);
                this.mapDataState.put(region, newCount);
            }
            Main.cacher.cacheRegionalVariable(regionType, variableName, mapDataState);
        }
    }

    private Hashtable<String, Integer> aggregateByGrid(ArrayList<Record> entries,
                                                       int gridSize, double[] topLeftEdge, double[] botRightEdge) {
        Hashtable<String, Integer> dataByGrid = new Hashtable<String, Integer>();

        double width = Math.abs(topLeftEdge[0] - botRightEdge[0]);
        double height = Math.abs(topLeftEdge[1] - botRightEdge[0]);

        double boxSize;
        if (height <= width) {
            boxSize = height / gridSize;
        } else {
            boxSize = width / gridSize;
        }

        for (Record entry : entries) {
            try {
                double[] coords = Main.coordinator.getCoordinates(entry.postCode);

                int xBukket = (int) (Math.floor(Math.abs(coords[0] / boxSize))) + 1;
                int yBukket = (int) (Math.floor(Math.abs(coords[1] / boxSize))) + 1;

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

        //for (String box : dataByGrid.keySet()) {
        //    System.out.print(box + ":" + dataByGrid.get(box) + ", ");
        //}

        /*for (int x = 0; x <= gridSize; x++) {
            for (int y = 0; y <= gridSize; y++) {
                String key = Integer.toString(x) + ":" + Integer.toString(y);
                System.out.print(dataByGrid.getOrDefault(key, 0 ) + "\t");
            }
            System.out.println();
        }*/
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
