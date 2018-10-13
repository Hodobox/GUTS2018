package bigdata.DataLogic;

import bigdata.Record;
import bigdata.WorldMap.MapAggregation;
import bigdata.WorldMap.WorldMapData;

import java.util.*;

import static bigdata.DataLogic.AnalysisMode.*;

public class Analyzer {

    private static final int DEFAULT_GRID_SIZE = 16;

    private MapAggregation aggregationFormat;
    private Hashtable<String, Integer> mapData = new Hashtable<String, Integer>();

    private Set<AnalysisMode> targetModes;


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

    public Hashtable<String, Integer> getMapData() {
        return this.mapData;
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
                    regionValues = aggregateByGrid(entries, DEFAULT_GRID_SIZE);
                    break;
                default:
                    regionValues = new Hashtable<String, Integer>();
            }

            if (this.mapData.isEmpty()) {
                this.mapData = regionValues;
            } else {
                // update the values of mapData with values from the new data chunk
                for (String region : this.mapData.keySet()) {
                    //if (this.mapData.get(region) < regionValues.get(region)) {
                    int currentCount = this.mapData.get(region);
                	this.mapData.put(region, currentCount + regionValues.get(region));
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
            String state = WorldMapData.getStateByZip(entry.postCode);
            dataByState.put(state, dataByState.get(state) + 1);
        }

        return dataByState;
    }

    private Hashtable<String, Integer>  aggregateByGrid(ArrayList<Record> entries, int gridSize) {
        return new Hashtable<String, Integer>();
    }
}
