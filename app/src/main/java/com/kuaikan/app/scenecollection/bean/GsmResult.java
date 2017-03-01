package com.kuaikan.app.scenecollection.bean;

public class GsmResult extends Result{
    private String lac;
    private String cellId;
    private String bsic;
    private String bcch;
    private String rssi;
    private String psc_or_pci;
    private String sig1;
    private String sig2;
    private String sig1_in_dbm;
    private String sig2_in_dbm;
    private String act;

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getBsic() {
        return bsic;
    }

    public void setBsic(String bsic) {
        this.bsic = bsic;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getBcch() {
        return bcch;
    }

    public void setBcch(String bcch) {
        this.bcch = bcch;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getPsc_or_pci() {
        return psc_or_pci;
    }

    public void setPsc_or_pci(String psc_or_pci) {
        this.psc_or_pci = psc_or_pci;
    }

    public String getSig1() {
        return sig1;
    }

    public void setSig1(String sig1) {
        this.sig1 = sig1;
    }

    public String getSig2() {
        return sig2;
    }

    public void setSig2(String sig2) {
        this.sig2 = sig2;
    }

    public String getSig1_in_dbm() {
        return sig1_in_dbm;
    }

    public void setSig1_in_dbm(String sig1_in_dbm) {
        this.sig1_in_dbm = sig1_in_dbm;
    }

    public String getSig2_in_dbm() {
        return sig2_in_dbm;
    }

    public void setSig2_in_dbm(String sig2_in_dbm) {
        this.sig2_in_dbm = sig2_in_dbm;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }
}
