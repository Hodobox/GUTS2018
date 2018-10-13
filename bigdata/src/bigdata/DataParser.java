package bigdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
	
	public DataParser(Data restrictions)
	{
		File dir = new File("../data");
		File[] directoryListing = dir.listFiles();
		 
		for(File file: directoryListing)
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				reader.readLine(); // read header
				
				String line;
				
				while( (line = reader.readLine()) != null)
				{
					String[] columns = line.split(",");
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("Error: not found file " + file.getName());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Failed to read line in file " + file.getName());
				e.printStackTrace();
			}
		}
	}
}
