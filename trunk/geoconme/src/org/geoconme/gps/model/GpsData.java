
package org.geoconme.gps.model;

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.ProximityListener;
import javax.microedition.location.QualifiedCoordinates;

import org.geoconme.gps.ui.GpsUI;



public class GpsData implements LocationListener, ProximityListener
{
    /** A Reference to Tourist UI Canvas */
    private GpsUI gpsUI = null;

    /** Coordinate detection threshold radius in meters */
    public static final float PROXIMITY_RADIUS = 100.0f;


    /** The first location update done. */
    private boolean firstLocationUpdate = false;

    private ProviderStatusListener statusListener = null;

    /**
     * Construct instance of this model class.
     */
    public GpsData(ProviderStatusListener listener)
    {
        statusListener = listener;

        ConfigurationProvider config = ConfigurationProvider.getInstance();

        // 1. Register LocationListener
        LocationProvider provider = config.getSelectedProvider();
        if (provider != null)
        {
            int interval = -1; // default interval of this provider
            int timeout = 0; // parameter has no effect.
            int maxage = 0; // parameter has no effect.

            provider.setLocationListener(this, interval, timeout, maxage);
        }

        
    }

    
    public void setGpsUI(GpsUI ui)
    {
        gpsUI = ui;
    }

   
    public void createProximityListener(Coordinates coordinates)
    {
        try
        {
            LocationProvider.addProximityListener(this, coordinates,
                    PROXIMITY_RADIUS);
        }
        catch (LocationException e)
        {
            System.out.println("LocationException");
        }
    }

    public void locationUpdated(LocationProvider provider,
            final Location location)
    {
        
        if (!firstLocationUpdate)
        {
            firstLocationUpdate = true;
            statusListener.firstLocationUpdateEvent();
        }

        if (gpsUI != null)
        {
            new Thread()
            {
                public void run()
                {
                    if (location != null && location.isValid())
                    {
                        AddressInfo address = location.getAddressInfo();
                        QualifiedCoordinates coord = location.getQualifiedCoordinates();
                        float speed = location.getSpeed();
                        
                        gpsUI.setInfo(address, coord, speed);
                        gpsUI.setProviderState("Available");
                        gpsUI.repaint();
                    }
                    else
                    {
                        gpsUI.setProviderState("Unavailable");
                        gpsUI.repaint();
                    }
                }
            }.start();
        }
    }

    public void providerStateChanged(LocationProvider provider,
            final int newState)
    {
        if (gpsUI != null)
        {
            new Thread()
            {
                public void run()
                {
                    switch (newState) {
                        case LocationProvider.AVAILABLE:
                            gpsUI.setProviderState("Available");
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                            gpsUI.setProviderState("Out of service");
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            gpsUI.setProviderState("Temporarily unavailable");
                            break;
                        default:
                            gpsUI.setProviderState("Unknown");
                            break;
                    }

                    gpsUI.repaint();
                    try {
						this.wait(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }.start();
        }
    }

    
    public void proximityEvent(Coordinates coordinates, Location location)
    {
        if (gpsUI != null)
        {
            gpsUI.setProviderState("Control point found!");

                gpsUI.setInfo(location.getAddressInfo(), location
                        .getQualifiedCoordinates(), location.getSpeed());
            

            gpsUI.repaint();
        }
    }

   
    public void monitoringStateChanged(boolean isMonitoringActive)
    {
        if (gpsUI != null)
        {
            if (isMonitoringActive)
            {
                // proximity monitoring is active
                gpsUI.setProximityState("Active");
            }
            else
            {
                // proximity monitoring can't be done currently.
                gpsUI.setProximityState("Off");
            }

            gpsUI.repaint();
        }
    }
}
