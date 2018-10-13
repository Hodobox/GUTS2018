package bigdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataParser {
	
	private Data restrictions;
	
	public DataParser(Data restrictions)
	{
		this.restrictions = restrictions;
	}
	
	public ArrayList<Record> parse()
	{
		File dir = new File("../data");
		File[] directoryListing = dir.listFiles();
		
		ArrayList<Record> result = new ArrayList<Record>();
		DRGChecker drgcheck = new DRGChecker();
		for(File file: directoryListing)
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				reader.readLine(); // read header
				
				String line;
				
				while( (line = reader.readLine()) != null)
				{
					int priceStartIndex = line.indexOf('$');
					if(priceStartIndex == -1)
						continue;
					String priceStr = line.substring(priceStartIndex);
					line = line.substring(0, priceStartIndex-1);
					String[] columns = line.split(",");
					
					final String gender = columns[3];
					final String birthDate = columns[4];
					final String admissionDate = columns[6];
					final String postcodeStr = columns[14];
					final String DRGcode = columns[17];
					
					// check gender
					
					// check age
					
					// check time
					
					// check DRG
					
					if(!drgcheck.check(DRGcode, restrictions.getDRGInformation()))
						continue;
					
					// check price
					
					// all criteria passed, add it to analyzer data
					// TODO
					
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("Error: not found file " + file.getName());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Failed to read line in file " + file.getName());
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
