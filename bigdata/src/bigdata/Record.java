package bigdata;

public class Record {
    public String postCode;

    public Record(String line)
	{
        int priceStartIndex = line.indexOf('$');
        if(priceStartIndex == -1)
            return;

        String priceStr = line.substring(priceStartIndex);
        line = line.substring(0, priceStartIndex-1);
        String[] columns = line.split(",");

        final String gender = columns[3];
        final String birthDate = columns[4];
        final String admissionDate = columns[6];
        final String postcodeStr = columns[14];
        
        this.postCode = postcodeStr;
        
        final String DRGcode = columns[17];
		// parse the input line and extract whatever information analyzer needs
	}
}
