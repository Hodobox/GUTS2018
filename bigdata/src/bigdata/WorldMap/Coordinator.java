package bigdata.WorldMap;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Coordinator {
	
	private HashMap<String,Double[]> knownCodes;
	
	public Coordinator()
	{
		knownCodes = new HashMap<String,Double[]>();
		File file = new File("../US Zip Codes from 2013 Government Data");
		
		
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));
				reader.readLine(); // read header
				
				String line;
				
				while( (line = reader.readLine()) != null)
				{
					String[] parsedLine = line.split(",");
					Double x = Double.parseDouble(parsedLine[1]);
					Double y = Double.parseDouble(parsedLine[2]);
					
					System.out.println(Arrays.asList(parsedLine).toString());
					
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
	
	public Double[] getCoordinates(String zipCode)
	{
		if(knownCodes.containsKey(zipCode))
		{
			Double[] result = new Double[] { knownCodes.get(zipCode)[0], knownCodes.get(zipCode)[1]};
			return result;
		}
		
		System.out.println("Not found zipCode: " + zipCode);
		return new Double[] {0.0,0.0};
	}

}
	
