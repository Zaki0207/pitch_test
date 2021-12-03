package TerminalArea;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ADStruct {
    String ICAOCodeID;
    double ADLon;
    double ADLat;
    double ADElevation;
    String RWYCode;
    int time_interval;
    long ref_time;
    Queue<FLPStruct> FLPque = new LinkedList<FLPStruct>();
    Stack<FLPStruct> ReleasedFLP = new Stack<FLPStruct>();

    public int getTime_interval() {
        return time_interval;
    }

    public void setTime_interval(int time_interval) {
        this.time_interval = time_interval;
    }

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
