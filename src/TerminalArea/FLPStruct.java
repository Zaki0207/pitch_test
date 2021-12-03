package TerminalArea;

import devstudio.generatedcode.datatypes.HLARWYSturct;

public class FLPStruct {
    String line;
    String FltNo;
    String DepAD;
    String ArrAD;
    String ACRigNum;
    String ACType;
    short CruAlt;
    short CruIAS;
    String type;
    HLARWYSturct RWYInfo;
    HLARWYSturct ArrRWYInfo;
    String Release_std_Time;
    int time_interval;

    public HLARWYSturct getArrRWYInfo() {
        return ArrRWYInfo;
    }

    public void setArrRWYInfo(HLARWYSturct arrRWYInfo) {
        ArrRWYInfo = arrRWYInfo;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFltNo() {
        return FltNo;
    }

    public void setFltNo(String fltNo) {
        FltNo = fltNo;
    }

    public String getDepAD() {
        return DepAD;
    }

    public void setDepAD(String depAD) {
        DepAD = depAD;
    }

    public String getArrAD() {
        return ArrAD;
    }

    public void setArrAD(String arrAD) {
        ArrAD = arrAD;
    }

    public String getACRigNum() {
        return ACRigNum;
    }

    public void setACRigNum(String ACRigNum) {
        this.ACRigNum = ACRigNum;
    }

    public String getACType() {
        return ACType;
    }

    public void setACType(String ACType) {
        this.ACType = ACType;
    }

    public short getCruAlt() {
        return CruAlt;
    }

    public void setCruAlt(short cruAlt) {
        CruAlt = cruAlt;
    }

    public short getCruIAS() {
        return CruIAS;
    }

    public void setCruIAS(short cruIAS) {
        CruIAS = cruIAS;
    }

    public HLARWYSturct getRWYInfo() {
        return RWYInfo;
    }

    public void setRWYInfo(HLARWYSturct RWYInfo) {
        this.RWYInfo = RWYInfo;
    }
}
