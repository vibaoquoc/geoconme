package org.geoconvertor.ellipsoid;
/**
 *  @(#)AbstractEllipsoid.java	0.1 07/05/2010 
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
 *	Abstract Class ellipsoid calculate.
 *<p>
 * -flatattening earth
 * </p>
 *<p>
 * -eccentricity (E) and (E')
 * </p>
 *<p>
 * -Big N
 * </p>
 *<p>
 * -Delta Long 
 * </p>
 *<p>
 * -P
 * </p>
 * 
 * </p>
 * 
 * 
 * @author Mario C. Ponciano | a.k.a Razec - mrazec@gmail.com
 * @version 0.1 - 07/05/2010
 * @since 0.1
 */

public abstract class AbstractEllipsoid extends AbstractCoefficient {
	/**
	 *Method Calculate flattening earth.
	 * 
	 * @param majorAxis
	 * @param minorAxis
	 * @return
	 */
	public double flattening(double majorAxis, double minorAxis) {
		return (majorAxis - minorAxis) / majorAxis;
	}

	/**
	 * Method is the eccentricity of the earth's elliptical.
	 * 
	 * @param majorAxis
	 * @param minorAxis
	 * @return - the double eccentricity one.
	 */
	protected double eccentricityOne(double majorAxis, double minorAxis){
		return (Math.sqrt((majorAxis*majorAxis) - (minorAxis*minorAxis))) / majorAxis;
	}

	/**
	 * 
	 * 
	 * @param majorAxis
	 * @param minorAxis
	 * @return
	 */
	protected double eccentricityTwo(double majorAxis, double minorAxis){
		return (Math.sqrt((majorAxis * majorAxis) - (minorAxis*minorAxis))) / minorAxis;
	}

	/**
	 * 
	 * Calculate the big normal, ellipse-rotation method used to measure around minorAxis.
	 * 
	 * @param majorAxis
	 * @param eccentricityOne
	 * @param latRadian
	 * @return
	 */
	protected double bigN(double majorAxis, double eccentricityOne, double latRadian){
		return majorAxis / Math.sqrt((1 - (eccentricityOne * eccentricityOne)* (Math.sin(latRadian)*Math.sin(latRadian))));
	}

	protected double deltaLong(double lngDecimal, int meridianCentral){
		return (lngDecimal - meridianCentral) * 3600;
	}

	protected double p(double deltaLong){
		return  0.0001 * deltaLong;
	}

}
