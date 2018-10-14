package bigdata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

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

	public Date getDateLimitLow() {
		return dateLimitLow;
	}

	public void setDateLimitLow(Date dateLimitLow) {
		this.dateLimitLow = dateLimitLow;
	}

	public Date getDateLimitHigh() {
		return dateLimitHigh;
	}

	public void setDateLimitHigh(Date dateLimitHigh) {
		this.dateLimitHigh = dateLimitHigh;
	}
	
	private int ageLimitLow, ageLimitHigh;
	private boolean includeFemales, includeMales;
	private int priceLimitLow, priceLimitHigh;
	private DRG DRGInformation;
	Date dateLimitLow, dateLimitHigh;

	private int readInt()
	{
		Scanner sc = new Scanner(System.in);
		Integer x = null;

		try 
		{
			x = sc.nextInt();
		} catch(InputMismatchException e)
		{
			System.out.println("Please enter an integer.");			
			return readInt();
		}
	
		return x;
	}
	
	public Data()
	{
		System.out.println("Enter restrictions on considered data, one token per line.");
		System.out.println("Enter lower and upper age limit:");
		this.ageLimitLow = readInt();
		this.ageLimitHigh = readInt();
		
		if(this.ageLimitLow > this.ageLimitHigh)
		{
			System.out.println("Swapping limits due to inversion");
			int tmp = this.ageLimitLow;
			this.ageLimitLow = this.ageLimitHigh;
			this.ageLimitHigh = tmp;
		}
		
		System.out.println("Include genders: Female = 1, Male = 2, Female & Male = 3:");
		
		int genderVal = readInt();
		
		if(genderVal<=0 || genderVal>3)
		{
			System.out.println("Invalid gender inclusion, including both Male & Female");
			genderVal = 3;
		}
		 
		this.includeFemales = ( (genderVal & 1) > 0) ? true : false;
		this.includeMales = ( (genderVal & 2) > 0) ? true : false;
		
		try {
			System.out.println("Enter earliest and latest admission date, format = Month/Day/Year (invalid implies no limit):");
			Scanner sc = new Scanner(System.in);
			String low = sc.next();
			String high = sc.next();
			this.dateLimitLow = new SimpleDateFormat("MM/dd/yyyy").parse(low);
			this.dateLimitHigh = new SimpleDateFormat("MM/dd/yyyy").parse(high);
		} catch (ParseException e) {
			System.out.println("failed parsing dates, putting no restriction on admission date");
			try {
				this.dateLimitLow = new SimpleDateFormat("MM/dd/yyyy").parse("1/1/1900");
				this.dateLimitHigh = new SimpleDateFormat("MM/dd/yyyy").parse("1/1/2100");
			} catch (ParseException e1) {
				System.out.println("Failed parsing default date...");
				e1.printStackTrace();
			}

		}

		System.out.println("Enter lower and upper price limit:");
		this.priceLimitLow = readInt();
		this.priceLimitHigh = readInt();
		
		if(this.priceLimitLow > this.priceLimitHigh)
		{
			System.out.println("Swapping limits due to inversion");
			int tmp = this.priceLimitLow;
			this.priceLimitLow = this.priceLimitHigh;
			this.priceLimitHigh = tmp;
		}
		
		System.out.println("Enter lower DRG code bound, then upper DRG code bound: ");
		
		this.DRGInformation = new DRG();
				
		Scanner sc = new Scanner(System.in);
		this.DRGInformation.lowerLimit = sc.next();
		this.DRGInformation.upperLimit = sc.next();
		sc.close();
		
	}
}
