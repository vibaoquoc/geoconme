
package org.geoconme.gps.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.location.AddressInfo;
import javax.microedition.location.QualifiedCoordinates;

import org.geoconme.gps.model.CoordObj;
import org.geoconme.location.gps.Utils;
import org.geoconvertor.utm.GEOConvertor;

import com.sun.perseus.parser.ColorParser;


/**
 * Viewer class that renders current location updates.
 */
public class GpsUI extends Canvas 
{
	/** The current state of the location provider as a String */
	private String providerState = "Unknown";

	/** Proximity monitoring state. */
	private String proximityState = "Waiting";



	private QualifiedCoordinates coord;






	private static String confHost = null;
	private static HttpConnection conexaoHTTP = null;

	public GpsUI(String confHost)
	{

		this.confHost = confHost;
		
		try {
			conexaoHTTP = (HttpConnection) Connector.open(this.confHost);
			conexaoHTTP.getURL();
			String sRes = conexaoHTTP.getResponseMessage();
			conexaoHTTP.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}



	public void setProviderState(String state)
	{
		providerState = state;
	}

	public void setProximityState(String state)
	{
		proximityState = state;
	}

	public void setInfo(AddressInfo info, QualifiedCoordinates coord,
			float speed)
	{

		this.coord = coord;
	}


	static Image image;
	static boolean published = false;
	static int publishedTotal = 0;
	
	private static int error = 0;

	private static CoordObj[] coordArray = new CoordObj[100];
	private static int coordArrayIndex = 0; 

	protected void paint(Graphics g)
	{




		Font f = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,Font.SIZE_SMALL);
		g.setFont(f);

		int width = getWidth();
		int height = getHeight();

		g.setColor(0);
		g.fillRect(0, 0, width, height);

		if (image == null) {
			try {
				image = Image.createImage(getClass().getResourceAsStream("/geoconme.png"));
			} catch (IOException ex) {
				g.setColor(0xffffff);
				g.drawString("Failed to load image!", 0, 0, Graphics.TOP | Graphics.LEFT);
				ex.printStackTrace();
				return;
			}
		}


		// Draw it in the center
		g.drawImage(image, width/2, height/2, Graphics.VCENTER | Graphics.HCENTER);





		// use font height as a line height
		int lineHeight = f.getHeight();
		// current line counter
		int line = 0;

		// clean the backround
		g.setColor(0xffffff);
		g.fillRect(0, height+5, getWidth(), getHeight());





		g.drawString("Conf: "+confHost, 0, lineHeight
				* (line++), Graphics.TOP | Graphics.LEFT);




		Random generator = new Random();
		int r = generator.nextInt();

		if(r%2==0) proximityState = "Reading.";
		else proximityState = "Reading...";


		g.setColor(0x00ff00);
		g.drawString("GPS: " + providerState, 0, lineHeight
				* (line++), Graphics.LEFT | Graphics.TOP);
		g.drawString("Status: " + proximityState, 0, lineHeight
				* (line++), Graphics.LEFT | Graphics.TOP);


		double lat = 0.0;
		double lon = 0.0;

		if (coord != null)
		{
			lat = coord.getLatitude();
			lon = coord.getLongitude();

			String sLat = Utils.formatDouble(lat, 3);
			String sLon = Utils.formatDouble(lon, 3);

			g.drawString("Lat, Lon (" + sLat + ", "
					+ sLon + ")", 0, lineHeight
					* (line++), Graphics.TOP | Graphics.LEFT);

			GEOConvertor coord = new GEOConvertor();
			g.drawString("SAD6984 (" + coord.geoToUTM(lat, lon, "SAD69", 45) + ")", 0, lineHeight
					* (line++), Graphics.TOP | Graphics.LEFT);

			g.drawString("WGS84 (" + coord.geoToUTM(lat, lon, "WGS84", 45) + ")", 0, lineHeight
					* (line++), Graphics.TOP | Graphics.LEFT);

			Date date = new Date();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
			int currentMonth = calendar.get(Calendar.MONTH);
			int currentYear = calendar.get(Calendar.YEAR);
			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
			int currentMinute = calendar.get(Calendar.MINUTE);
			int currentSecond = calendar.get(Calendar.SECOND);

			String currStringDate = String.valueOf(currentYear)+"-"+String.valueOf(currentMonth)+"-"+String.valueOf(currentDay)+"_"+String.valueOf(currentHour)+":"+String.valueOf(currentMinute)+":"+String.valueOf(currentSecond);

			if(coordArrayIndex == 0 && coordArray != null){
				coordArray[coordArrayIndex] = new CoordObj(sLat, sLon, currStringDate);
				coordArrayIndex++;
			}else{
				CoordObj lastCoordObj = coordArray[coordArrayIndex-1];

				if(
						(lastCoordObj!=null) &&
						
						(lastCoordObj.getLat().equals(sLat)) && (lastCoordObj.getLon().equals(sLon))){
					//System.out.println("already added");
				}else if(coordArrayIndex > 0 && coordArray != null){
					coordArray[coordArrayIndex] = new CoordObj(sLat, sLon, currStringDate);
					coordArrayIndex++;
				}
			}

			//System.out.println(">>>> "+coordArrayIndex);

			g.drawString("Collected "+coordArrayIndex+" locs!", 0, lineHeight * (line++), Graphics.TOP | Graphics.LEFT);

			

			//if(coordArrayIndex == 1){
			Thread updateThread = new Thread(){

				public void run(){
					
					if(publishedTotal<coordArrayIndex){
						for(int i=publishedTotal; i<coordArrayIndex; i++){
							if(coordArray[i]!=null){
								try {

									String sUrl = confHost+"/Update/?lat="+coordArray[i].getLat()+"&lon="+coordArray[i].getLon()+"&date="+coordArray[i].getDate();

									conexaoHTTP = (HttpConnection) Connector.open(sUrl);
									conexaoHTTP.getURL();
									String sRes = conexaoHTTP.getResponseMessage();
									conexaoHTTP.close();
									//System.out.println(sRes);

									if(sRes.equals("OK")){
										coordArray[i] = null;
										published=true;
										publishedTotal++;
										sRes = "";
										error = 0;
									}else{
										error = 1;
									}
								} catch (IOException e) {
									error = 1;
								} catch (SecurityException sce) {
									error = 2;
								}
								
							}
							
						}
					}
					//coordArrayIndex=0;
				}

			};

			updateThread.start();
			
			if(error > 0){
				g.setColor(0xff0000);
				if(error == 1)
					g.drawString("URL not found!", 0, 11 * (10), Graphics.TOP | Graphics.LEFT);
				else if(error == 2)
					g.drawString("No permission to access URL!", 0, 12 * (10), Graphics.TOP | Graphics.LEFT);

			}else{
				g.setColor(0x003300);
				g.drawString("URL found.", 0, 11 * (10), Graphics.TOP | Graphics.LEFT);
				g.drawString("URL accessed.", 0, 12 * (10), Graphics.TOP | Graphics.LEFT);
			}
			
			
			
			//}

			g.setColor(0x00ff00);
			if(published) g.drawString("Published "+publishedTotal+" locs!", 0, lineHeight * (line++), Graphics.TOP | Graphics.LEFT);
			else g.drawString("Error publishing locs! ("+lineHeight+","+line+")", 0, lineHeight * (line++), Graphics.TOP | Graphics.LEFT);

		}


	}



}