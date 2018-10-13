package bigdata;

import bigdata.WorldMap.MapAggregation;
import bigdata.WorldMap.WorldMapData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Analyzer {

    private static final int DEFAULT_GRID_SIZE = 16;
    private static final String[] FIELDS_OF_INTEREST = {"age", "gender", "drg", "price"};

    private MapAggregation aggregationFormat;
    private ArrayList<String> mapData = new ArrayList<String>();

    public Analyzer(MapAggregation aggregationFormat) {
        this.aggregationFormat = aggregationFormat;
    }

    public Analyzer(){
        this(MapAggregation.US_State);
    }

    /* process list of data entries pre-filtered by parser into data structure
       usable by map plotter */
    public void processData(ArrayList<Record> entries) {
        MapAggregation format = this.aggregationFormat;
        String[] newEntries;

        switch(format){
            case US_State:
                // Get list of us states with codes
                String[] states = WorldMapData.US_STATES;
                newEntries = aggregateByState(entries, states);
                break;
            case Grid:
                newEntries = aggregateByGrid(entries, DEFAULT_GRID_SIZE);
                break;
            default:
                newEntries = new String[] {};
        }

        this.mapData.addAll(Arrays.asList(newEntries));
    }

    private String[] aggregateByState(ArrayList<Record> entries, String[] states) {
        int stateCount = states.length;
        String[] dataByState = new String[stateCount];

        String dataString;
        for (int i = 0; i < stateCount; i++ ) {
            dataString = states[i] + ";";
            for (String field : this.FIELDS_OF_INTEREST ) {

                dataString += field;
            }
            dataByState[i] = dataString;
        }

        return new String[] {};
    }

    private String[] aggregateByGrid(ArrayList<Record> entries, int gridSize) {
        return new String[] {};
    }
}
