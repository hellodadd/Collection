package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kuaikan.app.scenecollection.util.Util;

//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;

//import android.os.AsyncResult;
//import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;

public class CmccActivity extends Activity implements OnClickListener{

    private Button g2;
    private Button g3;
    private Button g4;

//    private TextView info;
//
//    private TelephonyManager manager;
//    private Phone phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmcc);

//        phone=PhoneFactory.getDefaultPhone();
//        if(phone instanceof LteDcPhoneProxy){
//            Log.i("gejun","init LteDcPhoneProxy");
//            phone = ((LteDcPhoneProxy) phone).getLtePhone();
//        }
//        Log.i("gejun","phone = " + phone);
//
//        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        g2 = (Button)findViewById(R.id.cmcc_gsm);
        g2.setOnClickListener(this);
        g3 = (Button)findViewById(R.id.tdscdma);
        g3.setOnClickListener(this);
        g4 = (Button)findViewById(R.id.cmcc_lte);
        g4.setOnClickListener(this);

//        info = (TextView)findViewById(R.id.info);
    }

    @Override
    public void onResume() {
        super.onResume();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
//        atSetCOPS();
    }

    @Override
    public void onPause() {
        super.onPause();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

//    private static final int EVENT_SET_GENERATION = 0;
//    private static final int EVENT_SET_OP = 1;
//    private static final int EVENT_GET_NETWORKMODE = 2;
//    private static final int EVENT_GET_RSSI = 3;
//
//    private void setGeneration(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_GENERATION));
//    }
//
//    private void setOp(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_OP));
//    }
//
//    private void init(String[] g, String[] op){
//        setOp(op);
//        setGeneration(g);
//    }


    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent result;
        if(Util.NON_SIM){
            result = new Intent(this, NonSimGsmResultActivity.class);
        } else {
            result = new Intent(this, GsmResultActivity.class);
        }
        switch(id){
            case R.id.cmcc_gsm:
//                init(new String[]{"AT+ERAT=0","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_GSM);
                break;
            case R.id.tdscdma:
//                init(new String[]{"AT+ERAT=1","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_TDSCDMA);
                break;
            case R.id.cmcc_lte:
//                init(new String[]{"AT+ERAT=3","+ERAT"},
//                        new String[]{"AT+COPS=1,2,46000","+COPS"});
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_CMCC_LTE);
                break;
        }
        startActivity(result);
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case EVENT_SET_GENERATION:{
//                    AsyncResult ar = (AsyncResult) msg.obj;
//                    Log.i("gejun","exception = " + ar.exception);
//                    String atStr[] = {"AT+ECELL","+ECELL"};
//                    phone.invokeOemRilRequestStrings(atStr, mHandler
//                            .obtainMessage(EVENT_GET_NETWORKMODE));
////                    phone.invokeOemRilRequestStrings(new String[]{"AT+CSQ","+CSQ"}, mHandler
////                            .obtainMessage(EVENT_GET_RSSI));
//                    break;
//                }
//                case EVENT_GET_NETWORKMODE:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
//                    break;
//                }
//                case EVENT_GET_RSSI:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
//                    break;
//                }
//            }
//        }
//    };
//
//    private void showResult(Message msg, String tag){
//        AsyncResult ar1 = (AsyncResult)msg.obj;
//        String[] result1 = (String[]) ar1.result;
//        if(result1 == null){
//            Log.i("gejun","result null");
//            return;
//        }
//        int length1 = result1.length;
//        for(int i= 0;i<length1;i++){
//            Log.i("gejun",tag + i +" = " + result1[i]);
//            info.setText(result1[i]);
//        }
//
//        List<CellInfo> infos = manager.getAllCellInfo();
//        Log.i("gejun","infos = " + infos );
//        if(infos != null){
//            for(CellInfo info:infos){
//                Log.i("gejun","info = " + info);
//            }
//        }
//    }
//
//    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
//        @Override
//        public void onCellInfoChanged(List<CellInfo> cellInfo) {
//            Log.i("gejun","cellInfo = " + cellInfo);
//            if(cellInfo != null){
//                for(CellInfo info:cellInfo){
//                    Log.i("gejun","info = " + info);
//                }
//            }
//        }
//    };



    /**
     * enable supprt plmn
     */
    public void atSetCOPS(){
        Util.invokeAT(new String[]{"AT+COPS=1,2,\"46000\",0", "+COPS"}, mHandler.obtainMessage(EVENT_COPS));
    }

    private static final int EVENT_COPS = 0;
    private static final int EVENT_EINFO = 1;
    private static final int EVENT_ECELLINFO = 2;
    private static final int ERAT = 3;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_COPS:{
                    Util.showOriginResult(msg, "SetCOPS");

//                    Util.invokeAT(new String[]{"AT+EINFO=4", "+EINFO"}, mHandler.obtainMessage(EVENT_EINFO));
//                    Util.invokeAT(new String[]{"AT+ERAT=3,0","+ERAT"}, mHandler.obtainMessage(ERAT));
                    break;


                }
                case ERAT:{
                    Util.showOriginResult(msg, "ERAT");
                    Util.invokeAT(new String[]{"AT+ECELL", "+ECELL"}, mHandler.obtainMessage(EVENT_ECELLINFO));
                    break;
                }
                case EVENT_EINFO:{
                    Util.showOriginResult(msg, "EVENT_EINFO");

                    Util.invokeAT(new String[]{"AT+ECELLINFO=?", "+ECELLINFO"}, mHandler.obtainMessage(EVENT_ECELLINFO));
                    Util.invokeAT(new String[]{"AT+ECELL", "+ECELL"}, mHandler.obtainMessage(EVENT_ECELLINFO));
                    break;
                }
                case EVENT_ECELLINFO:{
                    Util.showOriginResult(msg, "EVENT_ECELLINFO");
                }
            }
        }
    };
}
