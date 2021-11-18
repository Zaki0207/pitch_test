package TerminalArea;

public class RWYStruct {
    String RWYCode;
    double MagHeading;
    double lon;
    double lat;
    double alt;
    String CODE_AIRPORT;
    double len;
    double wid;

    public String getRWYCode() {
        return RWYCode;
    }

    public void setRWYCode(String RWYCode) {
        this.RWYCode = RWYCode;
    }

    public double getMagHeading() {
        return MagHeading;
    }

    public void setMagHeading(double magHeading) {
        MagHeading = magHeading;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public String getCODE_AIRPORT() {
        return CODE_AIRPORT;
    }

    public void setCODE_AIRPORT(String CODE_AIRPORT) {
        this.CODE_AIRPORT = CODE_AIRPORT;
    }

    public double getLen() {
        return len;
    }

    public void setLen(double len) {
        this.len = len;
    }

    public double getWid() {
        return wid;
    }

    public void setWid(double wid) {
        this.wid = wid;
    }
}
