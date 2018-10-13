package bigdata;

public class Data {

	public int getAgeLimitLow() {
		return ageLimitLow;
	}

	public void setAgeLimitLow(int ageLimitLow) {
		this.ageLimitLow = ageLimitLow;
	}

	public int getAgeLimitHigh() {
		return ageLimitHigh;
	}

	public void setAgeLimitHigh(int ageLimitHigh) {
		this.ageLimitHigh = ageLimitHigh;
	}


	public int getPriceLimitLow() {
		return priceLimitLow;
	}

	public void setPriceLimitLow(int priceLimitLow) {
		this.priceLimitLow = priceLimitLow;
	}

	public int getPriceLimitHigh() {
		return priceLimitHigh;
	}

	public void setPriceLimitHigh(int priceLimitHigh) {
		this.priceLimitHigh = priceLimitHigh;
	}

	public DRG getDRGInformation() {
		return DRGInformation;
	}

	public void setDRGInformation(DRG dRGInformation) {
		DRGInformation = dRGInformation;
	}


	public boolean isIncludeFemales() {
		return includeFemales;
	}

	public void setIncludeFemales(boolean includeFemales) {
		this.includeFemales = includeFemales;
	}

	public boolean isIncludeMales() {
		return includeMales;
	}

	public void setIncludeMales(boolean includeMales) {
		this.includeMales = includeMales;
	}

	private int ageLimitLow, ageLimitHigh;
	private boolean includeFemales, includeMales;
	private int priceLimitLow, priceLimitHigh;
	private DRG DRGInformation;
	
	public Data()
	{
		
	}
}
