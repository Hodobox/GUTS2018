package bigdata;

public class Record {
    public String postCode;

    public Record(String line)
	{

        String[] columns = line.split(",");

        if(columns.length < 15)
        	System.out.println(line);
        
        final String gender = columns[3];
        final String birthDate = columns[4];
        final String admissionDate = columns[6];
        String postcodeStr = columns[14];
        
        if(postcodeStr.length() > 5)
		{
        	postcodeStr = postcodeStr.substring(0, 5);
		}
        
        this.postCode = postcodeStr;
        
        final String DRGcode = columns[17];
		// parse the input line and extract whatever information analyzer needs
	}
}
