package org.geoconvertor.utm;

/**
 * @(#)GEOConvertor.java	0.1 07/05/2010 
 * 
 * This program is free software; you can redistribute it and/or                
 * modify it under the terms of the GNU General Public License                  
 * as published by the Free Software Foundation; either version 3               
 * of the License, or (at your option) any later version.  
 * 
 * <p>
 * This class is part of the GEOConvertor package. Visit the <a
 * href="http://code.google.com/p/geoconvertor/">GEOConvertor</a> website for more
 * information.
 * </p>
 * 
 * <p>
 *  Class have methods to convert/transform coordinate system associated with the object.
 *  Conversion to:
 * 	Decimal -> UTM.
 * 	DMS (Degrees, Minutes, Seconds) -> Decimal.
 * 	Decimal Degree ->  DMS (Degrees, Minutes, Seconds).
 * </p>
 * 
 * 
 * @author Mario C. Ponciano | a.k.a Razec - mrazec@gmail.com
 * @version 0.1 - 07/05/2010
 * @since 0.1
 */


import org.geoconvertor.datum.SAD69;
import org.geoconvertor.datum.WGS84;
import org.geoconvertor.ellipsoid.AbstractEllipsoid;

public class GEOConvertor extends AbstractEllipsoid {

	private double lat;
	private double lng;

	/**
	 * Method convert Decimal Coordinate to UTM Coordinate.
	 * Return string with X and Y.
	 *<p>
	 * Passing parameters example:
	 * geoToUTM(21.88684199, 47.30414058, Datum.SAD69, 45);
	 * </p>
	 * 
	 * @param lat  latitude coordinate (decimal/minute/second). 
	 * @param lng  longitude coordinate (decimal/minute/second).
	 * @param datum  a reference datum.
	 * @param meridianCentral  define central meridian.
	 * @return  the string UTM coordinates.
	 */
	public String geoToUTM(double lat, double lng, String datum, int meridianCentral){

		this.lat = Math.abs(lat);
		this.lng = Math.abs(lng);
		//Local Variables
		double e1;
		double e2;
		double N;
		double S;
		//Check Datum
		if(datum.equals("SAD69")){
			//eccentricity
			e1 = eccentricityOne(SAD69.MAJOR_AXIS, SAD69.MINOR_AXIS);
			e2 = eccentricityTwo(SAD69.MAJOR_AXIS, SAD69.MINOR_AXIS);
			//Calculate BigNormal
			N = bigN(SAD69.MAJOR_AXIS, e1, Math.toRadians(lat));
			//Coefficients
			S = sCoeff(SAD69.MAJOR_AXIS, lat, e1, aCoeff(e1), bCoeff(e1), cCoeff(e1), dCoeff(e1), eCoeff(e1), fCoeff(e1));
			return calcUTM(e1, e2, N, S, meridianCentral);
		}else if(datum.equals("WGS84")){
			//eccentricity
			e1 = eccentricityOne(WGS84.MAJOR_AXIS, SAD69.MINOR_AXIS);
			e2 = eccentricityTwo(WGS84.MAJOR_AXIS, SAD69.MINOR_AXIS);
			//Calculate BigNormal
			N = bigN(WGS84.MAJOR_AXIS, e1, Math.toRadians(lat));
			//Coefficients
			S = sCoeff(WGS84.MAJOR_AXIS, lat, e1, aCoeff(e1), bCoeff(e1), cCoeff(e1), dCoeff(e1), eCoeff(e1), fCoeff(e1));
			return calcUTM(e1, e2, N, S, meridianCentral);
		}
		return null;		 
	}

	/**
	 * Private method defines calculate coordinate systems.
	 * 
	 * @param e1  eccentricity one.
	 * @param e2  eccentricity two.
	 * @param N  big normal.
	 * @param S  calculate S.
	 * @param meridianCentral  define central meridian.
	 * @return  the string UTM coordinates.
	 */
	private String calcUTM(double e1, double e2, double N, double S, int meridianCentral){
		//Delta Long
		double deltaLong = deltaLong(lng, meridianCentral);

		//P Calculate
		double p = p(deltaLong);

		double I = i(S);

		double IIP2 = iIP2(N, lat, p);

		double IIP4 = iIP4(N, lat, e2, p);

		double IVP = iVP(N, lat, p);

		double VP3 = VP3(N, lat, p, e2);

		double A6P6 = a6P6(N, lat, e2, p);

		double B5P5 = b5P5(N, lat, e2, p);

		double nCoord = I + IIP2 + IIP4 + A6P6;
		double north = 10000000 - nCoord;
		double adjNorth = north -  191.82;//Coordinate Adjust | BUG: It's necessary to solve this.

		double eCoord = IVP + VP3 + B5P5;
		double east = 500000 - eCoord;
		double adjEast = east + 93.435;//Coordinate Adjust | BUG: It's necessary to solve this.

		return String.valueOf(adjNorth)+"N, "+ String.valueOf(adjEast)+"E";
	}

	/**
	 * Convert DMS (Degrees, Minutes, Seconds) to  Decimal Degree
	 * <p>
	 * For example:
	 * dmsToDEC(-215312.63115)
	 * </p>
	 * 
	 * @param coord  insert latitude or longitude (Degrees, Minutes, Seconds).
	 * @return  the double Decimal coordinates.
	 */
	public double dmsToDEC(double coord){
		String degrees = String.valueOf(Math.abs(coord)).substring(0,2);
		String auxMin = String.valueOf(Math.abs(coord)).substring(2,4);
		String auxSec = String.valueOf(Math.abs(coord)).substring(4,6);

		return Double.parseDouble(degrees) + Double.parseDouble(auxMin) / 60 + Double.parseDouble(auxSec) / 3600;
	}
 
	/**
	 *  Convert Decimal Degree to DMS (Degrees, Minutes, Seconds)   
	 * <p>
	 * For example:
	 * decToDMS(49.8488438831)
	 * </p>
	 * @param coord  insert latitude or longitude (Decimal Degree).
	 * @return  the string coordinates.
	 */
	public String decToDMS(double coord){
		String degrees = String.valueOf(Math.abs(coord)).substring(0,2);
		String auxMin = String.valueOf(Math.abs(coord)).substring(3);
		double minute =  Double.parseDouble(auxMin) * 60;
		String auxSec = String.valueOf(minute).substring(0);
		double second = Double.parseDouble(auxSec) * 60;

		return degrees + "�" + String.valueOf(auxSec).substring(0,3).replace('.', ' ') + "'" + String.valueOf(second).substring(3,5).replace('.', ' ')+"," + String.valueOf(second).substring(5)+"\"";
	}
}
 