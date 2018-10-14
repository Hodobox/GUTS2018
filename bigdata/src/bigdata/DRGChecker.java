package bigdata;

public class DRGChecker {
	
	// returns true of DRGCode from .csv file matches the requirement given by DRG DRGInformation (entered by user)
	public boolean check(String DRGCode, DRG DRGInformation)
	{
		return (DRGInformation.lowerLimit.compareTo(DRGCode) < 0 && DRGInformation.upperLimit.compareTo(DRGCode) > 0);
	}
}
