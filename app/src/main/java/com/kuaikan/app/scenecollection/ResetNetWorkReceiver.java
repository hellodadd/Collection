package com.kuaikan.app.scenecollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kuaikan.app.scenecollection.util.Util;

/**
 * Created by zwb on 2017/5/11.
 */

public class ResetNetWorkReceiver extends BroadcastReceiver {

    final static int EVENT_AT_EBTSAP = 0;
    final static int EVENT_NETWORK_RESET = 1;
    final static int EVENT_EPBSE = 2;
    final static int EVENT_ECBAND = 3;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_AT_EBTSAP:{
                    Util.showOriginResult(msg, "recev_EBTSAP");
                    break;
                }
                case EVENT_NETWORK_RESET:{
                    Util.showOriginResult(msg, "reset_network");
                    break;
                }
                case EVENT_EPBSE:{
                    Util.showOriginResult(msg, "reset_epbse");
                    break;
                }
                case EVENT_ECBAND:{
                    Util.showOriginResult(msg, "reset_ecband");
                    break;
                }

            }
        }
    };

    private void resetModemBand(){
        Util.invokeAT(new String[]{"AT+EPBSE=10,1,5,480","+EPBSE"},
                mHandler.obtainMessage(EVENT_EPBSE));
        Util.invokeAT4CDMA(new String[]{"AT+ECBAND=0","+ECBAND"},
                mHandler.obtainMessage(EVENT_ECBAND));
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("gej","gej ------ com.kuaikan.action_reset_network------");
        final Context mContext = context;
        if(intent.getAction().equals("com.kuaikan.action_reset_network")){
            Util.invokeAT(context,new String[]{"AT+EBTSAP=1", "+EBTSAP"},
                    mHandler.obtainMessage(EVENT_AT_EBTSAP));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.setSettingsPutInt(mContext, mContext.getContentResolver(),
                            Util.PREFERRED_NETWORK_MODE + Util.getDefaultSubscription(),9);
                    Util.invokeSetPreferredNetworkType(9, mHandler.obtainMessage(EVENT_NETWORK_RESET));
                    resetModemBand();
                }
            },10000);
        }
    }
}
