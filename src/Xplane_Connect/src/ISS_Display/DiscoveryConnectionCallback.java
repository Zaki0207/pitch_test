package ISS_Display;

public interface DiscoveryConnectionCallback {

    /**
     * Helper callback called when a 1st XPlanePlugin is discovered. When the 1st packet is received, an ISS_Display.XPlaneConnect
     * instance is created and the discovery is stopped.
     *
     * @param xpc The ISS_Display.XPlaneConnect instance configured with the discovered
     */
    void onConnectionEstablished(XPlaneConnect xpc);
}
