package org.geoconvertor.utm;
/**
 * @(#)IConstants.java	0.1 07/05/2010 
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
 *  An interface to implement constant values. 
 * </p>
 * 
 * @author Mario C. Ponciano | a.k.a Razec - mrazec@gmail.com
 * @version 0.1 - 07/05/2010
 * @since 0.1
 * @see org.geoconvertor.utm.GEOConvertor
 */
public interface IConstants {
	/**
	 * Sine of one second of arc = pi / (180*60*60) = 4.8481368x10 (exp.) -6.
	 * 
	 * The value of this constant is {@SIN_ONE_SECOND}.
	 */
	public static final double SIN_ONE_SECOND = 0.00000484813681108; //Sin 1"
	/**
	 * Indicates that the Scale Factor {@K0}.
	 */
	public static final double K0 = 0.999995; //Scale Factor
}
