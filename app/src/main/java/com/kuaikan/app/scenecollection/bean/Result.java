package com.kuaikan.app.scenecollection.bean;

/**
 * Created by gejun on 2016/12/2.
 */

public class Result {
    public static final String CREATE_TIME = "create_time";
    public static final String DATA = "datas";

    //cdma
    public static final String NID = "nid";
    public static final String BID = "bid";
    public static final String SID = "sid";
    public static final String RX = "rx";
    public static final String PN = "pn";

    //gsm
    public static final String LAC = "lac";
    public static final String CID = "cellid";
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";
    public static final String PSC_PCI = "psc_pci";
    public static final String SIG1 = "sig1";
    public static final String SIG1DBM = "sig1_dbm";
    public static final String SIG2 = "sig2";
    public static final String SIG2DBM = "sig2_dbm";

    public static final String RAT_2G = "gsm";
    public static final String RAT_3G_CMCC = "tdscdma";
    public static final String RAT_3G_CU = "wcdma";
    public static final String RAT_4G = "lte";
    public static final String RAT_CDMA = "cdma";

    private String mcc;
    private String mnc;
    private String rat;

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
}
