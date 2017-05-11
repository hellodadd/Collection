package com.kuaikan.app.scenecollection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

import com.kuaikan.app.scenecollection.util.Util;

public class OpActivity extends Activity implements OnClickListener{

    private Button cu;
    private Button cmcc;
    private Button telecom;
    private Button gps;
    private Button oneKey;
    private Button oneKeyAll;
    private Button OneKeyQuick;
    private Button Off;
    private Button On;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cu = (Button)findViewById(R.id.cu);
        cu.setOnClickListener(this);
        cmcc = (Button)findViewById(R.id.cmcc);
        cmcc.setOnClickListener(this);
        telecom = (Button)findViewById(R.id.telecom);
        telecom.setOnClickListener(this);
        gps = (Button)findViewById(R.id.gps);
        gps.setOnClickListener(this);
        oneKey = (Button)findViewById(R.id.one_key);
        oneKey.setOnClickListener(this);
        oneKeyAll = (Button)findViewById(R.id.one_key_all);
        oneKeyAll.setOnClickListener(this);
        OneKeyQuick = (Button)findViewById(R.id.one_key_quick);
        OneKeyQuick.setOnClickListener(this);

        boolean type = getIntent().getBooleanExtra(Util.COLLECT_TYPE, false);
        gps.setVisibility(type ? View.GONE : View.VISIBLE);
        oneKey.setVisibility(type ? View.GONE : View.VISIBLE);
        oneKeyAll.setVisibility(type ? View.GONE : View.VISIBLE);
        OneKeyQuick.setVisibility(type ? View.GONE : View.VISIBLE);

        setTitle(type ? R.string.dynamic : R.string.undynamic);

        //Off = (Button)findViewById(R.id.cfun_button_0);
        //Off.setOnClickListener(this);
        //On = (Button)findViewById(R.id.cfun_button_1);
        //On.setOnClickListener(this);
        //Off.setVisibility(View.GONE);
        //On.setVisibility(View.GONE);
    }

    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent intent;
        switch(id){
            case R.id.cu:
                intent = new Intent(this, CuActivity.class);
                startActivity(intent);
                break;
            case R.id.cmcc:
                intent = new Intent(this, CmccActivity.class);
                startActivity(intent);
                break;
            case R.id.telecom:
                intent = new Intent(this, TelecomActivity.class);
                startActivity(intent);
                break;
            case R.id.gps:
                intent = new Intent(this, GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.one_key:
                if(Util.NON_SIM){
                    intent = new Intent(this, NonSimOneKeyActivity.class);
                } else {
                    intent = new Intent(this, OneKeyActivityB.class);
                }
                startActivity(intent);
                break;
            case R.id.one_key_all:
                intent = new Intent(this, NonSimOneKeyAllActivity.class);
                intent.putExtra("one_key_all", true);
                startActivity(intent);
                break;
            case R.id.one_key_quick:
                intent = new Intent(this, NonSimOneKeyActivity.class);
                intent.putExtra("one_key_quick", true);
                startActivity(intent);
                break;
            /*case R.id.cfun_button_0:{
                Util.invokeAT(this,new String[]{"AT+EBTSAP=0", "+EBTSAP"},
                        mHandler.obtainMessage(EVENT_EBTSAP));
                break;
            }
            case R.id.cfun_button_1:{
                Util.invokeAT(this,new String[]{"AT+EBTSAP=1", "+EBTSAP"},
                        mHandler.obtainMessage(EVENT_EBTSAP));
                break;
            }*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int modem = Util.reflectModemType();
        Log.d("gejun","gejun --------- modem = " + modem);
        if(modem == Util.MD_TYPE_LTG){
            Util.reflectSetModemSelectionMode(0, Util.MD_TYPE_LWG);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.invokeAT(new String[]{"AT+EPBSE=10,1,5,480","+EPBSE"},
                            mHandler.obtainMessage(EVENT_EPBSE));
                    Util.invokeAT4CDMA(new String[]{"AT+ECBAND=0","+ECBAND"},
                            mHandler.obtainMessage(EVENT_ECBAND));
                    Util.atCOPS(mHandler.obtainMessage(EVENT_COPS));
                }
            },10000);
        }else {
            Util.invokeAT(new String[]{"AT+EPBSE=10,1,5,480","+EPBSE"},
                    mHandler.obtainMessage(EVENT_EPBSE));
            Util.invokeAT4CDMA(new String[]{"AT+ECBAND=0","+ECBAND"},
                    mHandler.obtainMessage(EVENT_ECBAND));
            Util.atCOPS(mHandler.obtainMessage(EVENT_COPS));
        }

        boolean isSimInsert = Util.isSimInsert();

        Log.d("gej", "gej --------- isSimInsert = " + isSimInsert);
    }

    private static final int EVENT_COPS = 0;
    private static final int EVENT_EBTSAP = 1;
    private static final int EVENT_CFUN_0 = 2;
    private static final int EVENT_CFUN_1 = 3;
    private static final int EVENT_EPBSE = 4;
    private static final int EVENT_ECBAND = 5;
    private static final int EVENT_NETWORK_LTE = 6;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_COPS:{
                    Util.showOriginResult(msg, "getCOPS");
                    break;
                }
                case EVENT_EBTSAP:{
                    Util.showOriginResult(msg, "ebtsap");
                    break;
                }
                case EVENT_CFUN_0:{
                    Util.showOriginResult(msg, "cfun_0");
                    break;
                }
                case EVENT_CFUN_1:{
                    Util.showOriginResult(msg, "cfun_1");
                    break;
                }
                case EVENT_EPBSE:{
                    Util.showOriginResult(msg, "EPBSE");
                    break;
                }
                case EVENT_ECBAND:{
                    Util.showOriginResult(msg, "ECBAND");
                    break;
                }
                case EVENT_NETWORK_LTE:{
                    Util.showOriginResult(msg, "lte");
                    break;
                }
            }
        }
    };
}
