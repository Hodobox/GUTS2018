package bigdata.WorldMap;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class WorldMapData {

    private static HashMap<String, Point> stateZipRange;

    public static String getStateByZip(String zip)
    {
        int zipValue = Integer.parseInt(zip);
        for (String stateCode : stateZipRange.keySet())
        {
            if (stateZipRange.get(stateCode).x <= Integer.parseInt(zip) && Integer.parseInt(zip) <= stateZipRange.get(stateCode).y);
                return stateCode;
        }
        throw new RuntimeException("state not found");
    }

    public static void processStateZipRanges() {
        File file = new File("../states.txt");
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
                String[] tokens = line.split("");
                int rangeLow = Integer.parseInt(tokens[tokens.length - 2]), rangeHigh = Integer.parseInt(tokens[tokens.length - 1]);
                int stateCodeIndex = 0;
                while (tokens[stateCodeIndex].length() != 2 || Character.isLowerCase(tokens[stateCodeIndex].charAt(0)))
                    stateCodeIndex++;

                String stateCode = tokens[stateCodeIndex];

                stateZipRange.put(stateCode, new Point(rangeLow, rangeHigh));
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
