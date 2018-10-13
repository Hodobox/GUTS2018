package bigdata;

import java.util.ArrayList;

public class Analyzer {

    private static final int DEFAULT_GRID_SIZE = 16;
    private static final String[] FIELDS_OF_INTEREST = {"age", "gender", "drg", "price"};

    /* process list of data entries pre-filtered by parser into data structure
       usable by map plotter */
    public void processData(ArrayList<Record> entries, String format) {
        String mapData;

        switch(format){
            case "state":
                // Get list of us states with codes
                // String[] states = US_States.getStates();
                mapData = aggregateByState(entries, states);
                break;
            case "grid":
                mapData = aggregateByGrid(entries, DEFAULT_GRID_SIZE);
                break;
        }
    }

    private String aggregateByState(ArrayList<Record> entries, String[] states) {

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

        return "";
    }

    private String aggregateByGrid(ArrayList<Record> entries, int gridSize) {
        return "";
    }

}
