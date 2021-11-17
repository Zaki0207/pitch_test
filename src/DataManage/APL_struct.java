package DataManage;

import devstudio.generatedcode.datatypes.ATCCmdTypeFlightEnum;

public class APL_struct {
    String APLNo;
    String target_type;
    String target_ip;
    String target_port;
    String TargetPointID;
    ATCCmdTypeFlightEnum cmdType = ATCCmdTypeFlightEnum.NODATA;

    public String getTargetPointID() {
        return TargetPointID;
    }

    public void setTargetPointID(String targetPointID) {
        TargetPointID = targetPointID;
    }

    public ATCCmdTypeFlightEnum getCmdType() {
        return cmdType;
    }

    public void setCmdType(ATCCmdTypeFlightEnum cmdType) {
        this.cmdType = cmdType;
    }

    public void setAPLNo(String APLNo) {
        this.APLNo = APLNo;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public void setTarget_ip(String target_ip) {
        this.target_ip = target_ip;
    }

    public void setTarget_port(String target_port) {
        this.target_port = target_port;
    }

    public String getTarget_port() {
        return target_port;
    }

    public String getTarget_type() {
        return target_type;
    }

    public String getTarget_ip() {
        return target_ip;
    }

    public String getAPLNo() {
        return APLNo;
    }
}
