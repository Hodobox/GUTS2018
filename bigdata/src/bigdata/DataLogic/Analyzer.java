package bigdata.DataLogic;

import bigdata.Main;
import bigdata.Record;
import bigdata.WorldMap.Coordinator;
import bigdata.WorldMap.MapAggregation;
import bigdata.WorldMap.WorldMapData;

import java.util.*;

import static bigdata.DataLogic.AnalysisMode.*;

public class Analyzer {

    private static final int DEFAULT_GRID_SIZE = 16;

    private MapAggregation aggregationFormat;
    private Hashtable<String, Integer> mapDataState = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> mapDataGrid = new Hashtable<String, Integer>();

    private ArrayList<AnalysisMode> targetModes;

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

    public Analyzer(MapAggregation aggregationFormat) {
        // this.addTargetMode(AnalysisMode.Geography);
        this.aggregationFormat = aggregationFormat;
        WorldMapData.processStateZipRanges();
    }

    public Analyzer(){
    	this(MapAggregation.US_State);
    }

    public Hashtable<String, Integer> getMapDataState() {
        return this.mapDataState;
    }

    /* process list of data entries pre-filtered by parser into data structure
       usable by map plotter */
    public void processData(ArrayList<Record> entries) {
        String analysisMode = "geo";
        
        System.out.println("I am processing " + entries.size() + " entries");

        // geographical map plotting task
        if (analysisMode == "geo") {
            MapAggregation format = this.aggregationFormat;
            Hashtable<String, Integer> regionValues;
            switch(format){
                case US_State:
                    // Get list of us states with codes
                    String[] states = WorldMapData.US_STATES;
                    regionValues = aggregateByState(entries, states);
                    break;
                case Grid:
                    double[] topleft = {52.0, -133.0};
                    double[] botrgiht = {20.0, -70.0};
                    regionValues = aggregateByGrid(entries, DEFAULT_GRID_SIZE, topleft, botrgiht);
                    break;
                default:
                    regionValues = new Hashtable<String, Integer>();
            }

            if (this.mapDataState.isEmpty()) {
                this.mapDataState = regionValues;
            } else {
                // update the values of mapDataState with values from the new data chunk
                for (String region : this.mapDataState.keySet()) {
                    //if (this.mapDataState.get(region) < regionValues.get(region)) {
                    int currentCount = this.mapDataState.get(region);
                	this.mapDataState.put(region, currentCount + regionValues.get(region));
                    //}
                }
            }

        }
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

    private Hashtable<String, Integer> aggregateByGrid(ArrayList<Record> entries,
                                                       int gridSize, double[] topLeftEdge, double[] botRightEdge) {
        Hashtable<String, Integer> dataByGrid = new Hashtable<String, Integer>();

        Double width = Math.abs(topLeftEdge[0] - botRightEdge[0]);
        Double height = Math.abs(topLeftEdge[1] - botRightEdge[0]);

        if (height <= width) {
            Double boxSize = height / gridSize;
        } else {
            Double boxSize = width / gridSize;
        }

        for (Record entry : entries) {
            try {
                Double[] coords = Main.coordinator.getCoordinates(entry.postCode);
                Double xBukket = Math.abs(coords[0] - topLeftEdge[0]) / (width / gridSize);
                Double yBukket = coords[1];
            } catch (Exception e) {
                System.out.println("Exception caught while trying to update data for state: " + e.getMessage());
                System.out.println("faulty line: " + entry);
            }
        }

        return dataByGrid;
    }
}
