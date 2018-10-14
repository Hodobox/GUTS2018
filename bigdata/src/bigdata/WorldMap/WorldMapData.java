package bigdata.WorldMap;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WorldMapData {

    private static HashMap<String,ArrayList<Point>> stateZipRange;

    public static String getStateByZip(String zip)
    {
        int zipValue = 0;
        try {
            zipValue = Integer.parseInt(zip);
        }catch(NumberFormatException e) {
            System.out.println("Zip code is not a valid integer, exception thrown: " + e.getMessage());
            return null;
        }

        for (String stateCode : stateZipRange.keySet())
        {	
        	ArrayList<Point> ranges = stateZipRange.get(stateCode);
        	for(Point range : ranges)
        		if ( (range.x <= zipValue) && (zipValue <= range.y))
        		{
        			return stateCode;
        		}
        }
        System.out.println(zipValue);
        throw new RuntimeException("state not found");
    }

    public static void processStateZipRanges() {
    	System.out.println("constructing the fking map");
    	stateZipRange = new HashMap<String,ArrayList<Point>>();
        File file = new File("states.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            reader.readLine(); // read header

            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                int rangeLow = Integer.parseInt(tokens[tokens.length - 2]), rangeHigh = Integer.parseInt(tokens[tokens.length - 1]);

                ArrayList<Point> cur;
                if(stateZipRange.containsKey(tokens[tokens.length-3]))
                	cur = stateZipRange.get(tokens[tokens.length-3]);
                else cur = new ArrayList<Point>();
                cur.add(new Point(rangeLow, rangeHigh));
                stateZipRange.put(tokens[tokens.length-3], cur);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static final String[] US_STATE_NAMES = {
            "Alaska",
            "Alabama",
            "Arkansas",
            "American Samoa",
            "Arizona",
            "California",
            "Colorado",
            "Connecticut",
            "District of Columbia",
            "Delaware",
            "Florida",
            "Georgia",
            "Guam",
            "Hawaii",
            "Iowa",
            "Idaho",
            "Illinois",
            "Indiana",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Massachusetts",
            "Maryland",
            "Maine",
            "Michigan",
            "Minnesota",
            "Missouri",
            "Mississippi",
            "Montana",
            "North Carolina",
            "North Dakota",
            "Nebraska",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "Nevada",
            "New York",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Puerto Rico",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Virginia",
            "Virgin Islands",
            "Vermont",
            "Washington",
            "Wisconsin",
            "West Virginia",
            "Wyoming"
    };
    public static final String[] US_STATES = {"AK",
            "AL",
            "AR",
            "AS",
            "AZ",
            "CA",
            "CO",
            "CT",
            "DC",
            "DE",
            "FL",
            "GA",
            "GU",
            "HI",
            "IA",
            "ID",
            "IL",
            "IN",
            "KS",
            "KY",
            "LA",
            "MA",
            "MD",
            "ME",
            "MI",
            "MN",
            "MO",
            "MS",
            "MT",
            "NC",
            "ND",
            "NE",
            "NH",
            "NJ",
            "NM",
            "NV",
            "NY",
            "OH",
            "OK",
            "OR",
            "PA",
            "PR",
            "RI",
            "SC",
            "SD",
            "TN",
            "TX",
            "UT",
            "VA",
            "VI",
            "VT",
            "WA",
            "WI",
            "WV",
            "WY"};
}
