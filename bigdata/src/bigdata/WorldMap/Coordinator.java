package bigdata.WorldMap;

import java.awt.Point;
import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Coordinator {
	
	private HashMap<String,Double[]> knownCodes;
	
	/*instantiates coordinator - reads zip : coordinate pairs into hashmap*/
	public Coordinator()
	{
		knownCodes = new HashMap<String,Double[]>();

        try {
            File file;
            BufferedReader reader;

            try {
                file = new File("../US Zip Codes from 2013 Government Data");
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                file = new File("US Zip Codes from 2013 Government Data");
                reader = new BufferedReader(new FileReader(file));
            }
            reader.readLine(); // read header

            String line;

            while( (line = reader.readLine()) != null)
            {
                String[] parsedLine = line.split(",");
                Double x = Double.parseDouble(parsedLine[1]);
                Double y = Double.parseDouble(parsedLine[2]);

                //System.out.println(Arrays.asList(parsedLine).toString());

                knownCodes.put(parsedLine[0], new Double[] {x,y});

            }
        } catch (FileNotFoundException e) {
            System.out.println("Not found ZIP codes file");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.out.println("Failed to read ZIP codes file");
            e.printStackTrace();
            return;
        }
	}
	/*given zip code string, returns Double[2] with the resulting coordinates*/
	public double[] getCoordinates(String zipCode)
	{
		if(knownCodes.containsKey(zipCode))
		{
            double[] result = new double[] { knownCodes.get(zipCode)[0], knownCodes.get(zipCode)[1]};
			return result;
		}
		
		System.out.println("Not found zipCode: " + zipCode);
		return new double[] {0.0,0.0};
	}

}
	
