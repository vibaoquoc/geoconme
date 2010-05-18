
package org.geoconme.gps.model;

/**
 * Listener interface for location providers status information.
 */
public interface ProviderStatusListener
{
    /**
     * A Notification event that location provider has been selected.
     */
    public void providerSelectedEvent();

    /**
     * A Notification event about the first location update.
     */
    public void firstLocationUpdateEvent();
}
