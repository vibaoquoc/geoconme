
package org.geoconme.gps.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Gauge;

import org.geoconme.location.gps.MainMIDlet;


/**
 * Viewer class containing a general suppose Alert notification(s).
 */
public class MessageUI
{
    /**
     * Shows a alert that Location API is not supported.
     */
    public static void showApiNotSupported()
    {
        Alert alert = new Alert("Information",  "Device do not support Location API", null, AlertType.INFO);
        MainMIDlet.getDisplay().setCurrent(alert);
    }

    /**
     * Shows a alert that Location data is not available.
     */
    public static void showLocationDataNotAvailable()
    {
        Alert alert = new Alert("Information",
                "Location data is not yet available.", null, AlertType.INFO);
        MainMIDlet.getDisplay().setCurrent(alert);
    }

    /**
     * Show a status dialog informing about state of location provider.
     */
    public static void showLocationProviderState()
    {
        Gauge indicator = new Gauge(null, false, 50, 1);
        indicator.setValue(Gauge.CONTINUOUS_RUNNING);

        Alert alert = new Alert("Information",
                "Please wait, looking for location data....", null,
                AlertType.INFO);
        alert.setIndicator(indicator);

        MainMIDlet.getDisplay().setCurrent(alert);
    }
}
