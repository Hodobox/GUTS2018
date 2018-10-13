package bigdata;

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
	
	public DataParser(Data restrictions)
	{
		this.restrictions = restrictions;
	}
	
	public void parse(Analyzer analyzer)
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
					Record record = new Record(line);
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
					priceStr.replaceAll(",", "");
					double price = Double.parseDouble(priceStr);
					
					if(price < restrictions.getPriceLimitLow() || price > restrictions.getPriceLimitHigh())
						continue;
					
					// all criteria passed, add it to analyzer data
					result.add(record);
					
					if (result.size() == 1024)
					{
						analyzer.analyze(result);
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
			analyzer.analyze(result);
	}
}
