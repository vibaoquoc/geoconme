
package org.geoconme.location.gps;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.geoconme.gps.model.ConfigurationProvider;
import org.geoconme.gps.model.GpsData;
import org.geoconme.gps.model.ProviderStatusListener;
import org.geoconme.gps.model.RecordDataManager;
import org.geoconme.gps.ui.GpsUI;
import org.geoconme.gps.ui.MessageUI;


/**
 * Tourist Route MIDlet class.
 */
public class MainMIDlet extends MIDlet implements ProviderStatusListener
{
	/** A static reference to Display object. */
	private static Display display = null;






	/** A Reference to TouristData. */
	private GpsData data = null;

	/** Lock object */
	private Object mutex = new Object();

	public MainMIDlet()
	{
		super();
	}

	static Image image;

	protected void startApp() throws MIDletStateChangeException
	{

		//MainMIDlet provider = this;

		display = Display.getDisplay(this);



		if (ConfigurationProvider.isLocationApiSupported())
		{
			ConfigurationProvider.getInstance().autoSearch(this);

		}
		else
		{
			MessageUI.showApiNotSupported();
		}
	}




	protected void pauseApp()
	{
	}





	protected void destroyApp(boolean unconditional)
	throws MIDletStateChangeException
	{
	}


	public static Display getDisplay()
	{
		return display;
	}


	public void providerSelectedEvent()
	{
		// Attempt to acquire the mutex
		synchronized (mutex)
		{
			// Start scanning location updates. Also set the TouristData
			// reference data.
			MessageUI.showLocationProviderState();

			// Inform the user that MIDlet is looking for location data.
			data = new GpsData((ProviderStatusListener) this);
		}
	}

	private Command confCMD = new Command("Conf", Command.EXIT, 1);
	private Command exitCMD = new Command("Exit", Command.OK, 0);
	
	private String confHost = "";
	private RecordDataManager rdc = new RecordDataManager("geconme");
	
	public void firstLocationUpdateEvent()
	{
		
		String[] confRs =  rdc.getConf();
		confHost = confRs[0];
		
		synchronized (mutex)
		{
			GpsUI ui = new GpsUI(confHost);

			data.setGpsUI(ui);
			ui.addCommand(exitCMD);
			ui.addCommand(confCMD);

			ui.setCommandListener(cmdListener);
			display.setCurrent(ui);
		}
	}


	private static Form cui = new Form("ConfEditor");
	private Command saveCmd = new Command("Save", Command.BACK, 2);
	private CommandListener cmdListener = new CommandListener() {

		

		public void commandAction(Command c, Displayable d) {
			if(c == exitCMD){
				notifyDestroyed();
				
				
				
				
			}else if(c==confCMD){
				
				String hostData = "http://";
				TextField confField = new TextField("Host:", hostData, 60, TextField.ANY);

				cui.append(confField);

				cui.addCommand(exitCMD);
				cui.addCommand(saveCmd);
				cui.setCommandListener(cmdListener);

				display.setCurrent(cui); 

			}else if(c==saveCmd){
				TextField hostField = (TextField)cui.get(0);
				String host = hostField.getString();
								rdc.saveConf(host);
				rdc.closeRecordStore();
				
				
				StringItem hLabel = new StringItem("Saved:", host); 
				cui.append(hLabel);

			}

		}
	};


}