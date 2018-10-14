package bigdata;

import bigdata.DataLogic.Analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class DataParser {
	
	private Data restrictions;
	
	// initializes the parser - requires list of restrictions (an instance of Data)
	public DataParser(Data restrictions)
	{
		this.restrictions = restrictions;
	}
	
	// reads all files in data/, line by line, and passes the ones that match the restrictions
	// to the analyzer
	public void parse(Analyzer analyzer)
	{
        File dir;
        try{
            dir = new File("../data");
        } catch (NullPointerException e) {
            dir = new File("data");
        };
		File[] directoryListing = dir.listFiles();

		ArrayList<Record> result = new ArrayList<Record>();
		DRGChecker drgcheck = new DRGChecker();
		for (File file : directoryListing)
		{
			System.out.println(file.getName());
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				reader.readLine(); // read header
				
				String line;
				
				while( (line = reader.readLine()) != null)
				{
					boolean withinEntry = false;
					for(int i=0;i<line.length();++i)
					{
						if(line.charAt(i)=='"') withinEntry = !withinEntry;
						if(line.charAt(i)==',' && withinEntry)
							line = line.substring(0,i) + line.substring(i+1);
					}
					line = line.replaceAll("\"", "");
					
					int priceStartIndex = line.indexOf('$');
					if(priceStartIndex == -1)
						continue;
					
					String priceStr = line.substring(priceStartIndex+1);
					line = line.substring(0,priceStartIndex);
					String[] columns = line.split(",");
					
					if(columns.length != 21)
					{
						System.out.println("Faulty line: " + line);
						continue;
					}
					
					Record record = new Record(line);
					
					final String gender = columns[3];
					final String birthDate = columns[4];
					final String admissionDate = columns[6];
					final String postcodeStr = columns[14];
					final String DRGcode = columns[17];
					
					// check gender
					
					if(!restrictions.isIncludeFemales() && gender.equals("F"))
						continue;
					
					if(!restrictions.isIncludeMales() && gender.equals("M"))
						continue;
					
					// check age
					Date dateOfBirth;
					Date dateOfAdmission;
					try {
						dateOfBirth = new SimpleDateFormat("MM/dd/yyyy").parse(birthDate);
					} catch (ParseException e) {
						System.out.println("Error: invalid date of birth " + birthDate);
						e.printStackTrace();
						continue;
					}  
					
					try {
						dateOfAdmission = new SimpleDateFormat("MM/dd/yyyy").parse(admissionDate);
					} catch (ParseException e) {
						System.out.println("Error: invalid date of admission " + admissionDate);
						e.printStackTrace();
						continue;
					}  
					
					int age = Period.between(LocalDate.ofInstant(dateOfBirth.toInstant(), ZoneId.systemDefault()), LocalDate.ofInstant(dateOfAdmission.toInstant(), ZoneId.systemDefault())).getYears();
					
					if(age < restrictions.getAgeLimitLow() || age > restrictions.getAgeLimitHigh())
						continue;
					
					
					// check time of incident
					
					if(restrictions.getDateLimitLow().after(dateOfAdmission) || restrictions.getDateLimitHigh().before(dateOfAdmission)) 
						continue;
					
					// check DRG
					
					if(!drgcheck.check(DRGcode, restrictions.getDRGInformation()))
						continue;
					
					// check price
					priceStr = priceStr.replaceAll(",", "");
					priceStr = priceStr.substring(0,priceStr.length()-1);
					double price = Double.parseDouble(priceStr);
					
					if(price < restrictions.getPriceLimitLow() || price > restrictions.getPriceLimitHigh())
						continue;
					
					// all criteria passed, add it to analyzer data
					result.add(record);
					
					if (result.size() == 1024)
					{
						analyzer.processData(result);
						result.clear();
					}
					
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("Error: not found file " + file.getName());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Failed to read line in file " + file.getName());
				e.printStackTrace();
			}
		}
		
		if(!result.isEmpty())
			analyzer.processData(result);
	}
}
