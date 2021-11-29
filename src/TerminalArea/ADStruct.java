package TerminalArea;

public class ADStruct {
    String ICAOCodeID;
    double ADLon;
    double ADLat;
    double ADElevation;
    String RWYCode;

    public String getRWYCode() {
        return RWYCode;
    }

    public void setRWYCode(String RWYCode) {
        this.RWYCode = RWYCode;
    }

    public String getICAOCodeID() {
        return ICAOCodeID;
    }

    public void setICAOCodeID(String ICAOCodeID) {
        this.ICAOCodeID = ICAOCodeID;
    }

    public double getADLon() {
        return ADLon;
    }

    public void setADLon(double ADLon) {
        this.ADLon = ADLon;
    }

    public double getADLat() {
        return ADLat;
    }

    public void setADLat(double ADLat) {
        this.ADLat = ADLat;
    }

    public double getADElevation() {
        return ADElevation;
    }

    public void setADElevation(double ADElevation) {
        this.ADElevation = ADElevation;
    }
}
