
package org.geoconme.gps.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import org.geoconme.location.gps.MainMIDlet;


/**
 * Viewer class that is responsible for all the UI actions when the application
 * is seaching for the location provider.
 */
public class ProviderQueryUI
{
    /** Status information Form */
    private Form searchForm = new Form("Seaching location provider...");

    /** StringItem showing the current status. */
    private StringItem infoItem = new StringItem("Status:", "");

    /** Provider cost selection command - Yes. */
    private Command yesCmd = new Command("GetLocation", Command.OK, 1);

   
    /** A boolean indicating may user allow location provider cost. */
    private boolean result = false;

    

    private static final String OUT_OF_SERVICE_MESSAGE = "All Location providers are currently out of service. Please unsure "
            + "that location-providing module is properly connected.";

    private static final String SEACHING_FREE_PROVIDERS = "Seaching for free location providers.";

    private static final String SEACHING_COST_PROVIDERS = "Seaching for providers that may cost.";

    private static final String NOT_FOUND_MESSAGE = "Try again after location-providing module is properly connected.";

    
    /**
     * Construct the UI with default values.
     */
    public ProviderQueryUI()
    {
        infoItem.setText(SEACHING_FREE_PROVIDERS);
        searchForm.append(infoItem);
    }

    /**
     * Show out of service error message.
     */
    public void showOutOfService()
    {
        Alert alert = new Alert("Error", OUT_OF_SERVICE_MESSAGE, null,
                AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        MainMIDlet.getDisplay().setCurrent(alert, searchForm);
        infoItem.setText(NOT_FOUND_MESSAGE);
    }

    /**
     * Show no cost free location provider found error message.
     */
    
    public synchronized boolean confirmCostProvider()
    {
        
    	Canvas canvas = new DrawImageCanvas();

    	canvas.addCommand(yesCmd);
    	
    	
        // Set the monitoring object to be this instance.
        final ProviderQueryUI hinstance = this;

        // Add a CommandLister as anomynous inner class
        canvas.setCommandListener(new CommandListener()
        {
            /*
             * Event indicating when a command button is pressed.
             * 
             * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
             *      javax.microedition.lcdui.Displayable)
             */
            public void commandAction(Command command, Displayable d)
            {
                if (command == yesCmd)
                {
                    infoItem.setText(SEACHING_COST_PROVIDERS);
                    result = true;
                    synchronized (hinstance)
                    {
                        // Wake up the monitoring object
                        hinstance.notifyAll();
                    }
                }
                

            }
        });

        MainMIDlet.getDisplay().setCurrent(canvas);

        // Wait indefinitely for notification.
        try
        {
            wait();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        MainMIDlet.getDisplay().setCurrent(searchForm);

        return result;
    }

}
