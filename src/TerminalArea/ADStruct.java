package TerminalArea;

public class ADStruct {
    String ICAOCodeID;
    double ADLon;
    double ADLat;
    double ADElevation;
    int RWYNum;

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

    public int getRWYNum() {
        return RWYNum;
    }

    public void setRWYNum(int RWYNum) {
        this.RWYNum = RWYNum;
    }
}
