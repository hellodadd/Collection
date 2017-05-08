package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kuaikan.app.scenecollection.util.Util;

//import com.android.internal.telephony.Phone;
//import com.android.internal.telephony.PhoneFactory;
//import android.os.AsyncResult;
//import com.mediatek.internal.telephony.ltedc.LteDcPhoneProxy;

public class TelecomActivity extends Activity implements OnClickListener{

    private Button g3;
    private Button g4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telecom);
        g3 = (Button)findViewById(R.id.cdma);
        g3.setOnClickListener(this);
        g4 = (Button)findViewById(R.id.telecom_lte);
        g4.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
    }

    @Override
    public void onPause() {
        super.onPause();
//        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

//    private static final int EVENT_SET_GENERATION = 0;
//    private static final int EVENT_SET_OP = 1;
//    private static final int EVENT_GET_NETWORKMODE = 2;
//    private static final int EVENT_SET_SUO = 3;
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
//        setGeneration(g);
////        setOp(op);
//    }


    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent result;
        switch(id){
            case R.id.cdma:
//                init(new String[]{"AT+ERAT=7,0", "+ERAT"},
//                        new String[]{"AT+COPS=1,2,46003","+COPS"});
                result = new Intent(this, CdmaResultActivity.class);
                startActivity(result);
                break;
            case R.id.telecom_lte:
//                init(new String[]{"AT+ERAT=3,0", "+ERAT"},
//                        new String[]{"AT+COPS=1,2,46003","+COPS"});
//                setGeneration(new String[]{"AT+ERAT=3,0","+ERAT"});
                if(Util.NON_SIM){
                    result = new Intent(this, NonSimGsmResultActivity.class);
                } else {
                    result = new Intent(this, GsmResultActivity.class);
                }
                result.putExtra(Util.NETWORK_MODE_TYPE, Util.TYPE_TELECOM_LTE);
                startActivity(result);
                break;
        }
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case EVENT_SET_GENERATION:{
//
//
//                    phone.invokeOemRilRequestStrings(new String[]{"AT+ESUO=9",""}, mHandler
//                            .obtainMessage(EVENT_SET_SUO));
//
//                    String atStr[] = {"AT+ECENGINFO=1,2","+ECENGINFO"};
//                    phone.invokeOemRilRequestStrings(atStr, mHandler
//                            .obtainMessage(EVENT_GET_NETWORKMODE));
//                    break;
//                }
//                case EVENT_GET_NETWORKMODE:{
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
//        if(result1 == null) {
//            Log.i("gejun","null................");
//            return;
//        }
//        int length1 = result1.length;
//        for(int i= 0;i<length1;i++){
//            Log.i("gejun",tag + i +" = " + result1[i]);
//            info.setText(result1[i]);
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
}
