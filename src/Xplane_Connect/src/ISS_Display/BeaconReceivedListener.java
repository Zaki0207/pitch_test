package ISS_Display;

public interface BeaconReceivedListener {

    /**
     * Called every time a BECN packet is received
     *
     * @param beacon The parsed beacon
     */
    void onBeaconReceived(Beacon beacon);
}
