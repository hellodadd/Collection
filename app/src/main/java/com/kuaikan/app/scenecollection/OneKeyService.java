package com.kuaikan.app.scenecollection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kuaikan.app.scenecollection.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by gejun on 2016/12/2.
 */

public class OneKeyService extends Service {

    private static int REQUEST_ONE_KEY = 0;

    private ArrayList<String> result = new ArrayList<String>();

    private ArrayList<Integer> opList = new ArrayList<Integer>();
    private ArrayList<Integer> generations = new ArrayList<Integer>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("gejun","OneKeyService onCreate()");
        initGs();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GET_CURRENT_SIM);
        filter.addAction(ACTION_SEARCH_OVER);
        registerReceiver(mReceiver, filter);

        Util.startSetOPService(this, "get_current_siminfo");

        mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
    }

    private void initGs(){
        generations.clear();
        generations.add(0);
        generations.add(1);
        generations.add(3);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        int whatRequest = intent.getIntExtra("request_type", -1);
//        if(whatRequest == REQUEST_ONE_KEY){
//            oneKey();
//        }
//    }

    private static final int EVENT_GET_CELLINFO = 0;
    private static final int EVENT_GET_NETWORKMODE = 1;
    private static final int EVENT_SET_GENERATION = 2;
    private static final int EVENT_GET_CDMA_INFO = 3;
    private static final int EVENT_GET_ACTIVE_CDMA = 4;
    private static final int EVENT_GET_NEIGHBOR_CDMA = 5;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case EVENT_GET_CELLINFO: {
                    try {
                        Util.getCellInfo(mHandler.obtainMessage(EVENT_GET_NETWORKMODE));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("gejun","e = " + e.toString());
                    }

                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
                    break;
                }
                case EVENT_GET_NETWORKMODE:{
                    dealRestult(msg);
                    break;
                }
                case EVENT_GET_CDMA_INFO:{
                    Util.invokeAT4CDMA(new String[]{"AT+VLOCINFO?", "+VLOCINFO"},
                            mHandler.obtainMessage(EVENT_GET_ACTIVE_CDMA));

                    Util.invokeAT4CDMA(new String[]{"AT+ECENGINFO=1,19","+ECENGINFO"},
                            mHandler.obtainMessage(EVENT_GET_NEIGHBOR_CDMA));
                    break;
                }
                case EVENT_GET_ACTIVE_CDMA:
                case EVENT_GET_NEIGHBOR_CDMA:{
                    showCDMAResult(msg, "EVENT" + msg.what);
                    break;
                }
            }
        }
    };

    private void showCDMAResult(Message msg, String tag){
        String[] arr = null;
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);

            Field exception = arC.getDeclaredField("exception");
            Object ex = exception.get(msg.obj);
            Log.i("gejun",tag + "ex:" + ex);
            Log.i("gejun",tag + "resultString = " + resultString);
            if(resultString == null) return;
            arr = (String[]) resultString;
            for(int i =0;i<arr.length;i++){
                Log.i("gejun",tag + "show Result = " + arr[i]);
            }

        } catch (Exception e){
            Log.i("gejun",tag + " e = " + e.toString());
        }
        if(arr!= null) {
            extraData1(arr);
        }
    }

    private String mcc;
    private String mnc;
    private String sid;
    private String bid;
    private String nid;
    private int rx_power1;

    private String vlocinfo;

    private void extraData1(String[] o){
        if(o.length == 0) return;
        if(o[0].startsWith("+VLOCINFO")){
            String[] info1 = o[0].split(",");
            mcc = info1[1];
            mnc = info1[2];
            sid = info1[3];
            nid = info1[4];
            bid = info1[5];
            Log.i("gejun","mcc: " + mcc + ", mnc: " + mnc + ", sid: " + sid + ", nid: " + nid + ", bid: " + bid);
            vlocinfo = o[0];
            return;
        }

        if(o[0].startsWith("+ECENGINFO:\"1xRTT_Radio_Info\"")){
            rx_power1 = Integer.parseInt(o[0].split(",")[4]);
            Log.i("gejun","rx_power1: " + rx_power1);
        }

        if(o[2].split(",")[1].equals("0")){
            mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
            return;
        }
        Log.i("gejun","************save result*************");
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
//        cdmaResult = o;
        result.add(vlocinfo);
        for(int i=0;i<o.length;i++){
//            cdmaResultList.add(o[i]);
            result.add(o[i]);
        }
        opList.add(Integer.parseInt(mnc));
        Log.i("gejun",mnc + " has search over!");
        if(opList.size() == 2){
            Log.i("gejun","search over!");
            sendResult();
            mHandler.removeMessages(EVENT_GET_CELLINFO);
        } else {
            initGs();
            //switch sim
            Util.startSetOPService(this, "switch");
        }
    }

    private void dealRestult(Message msg){
        String[] arr = null;
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);
            if(resultString == null) return;
            arr = (String[]) resultString;
        } catch (Exception e){
            Log.i("gejun","e = " + e.toString());
        }

        if(arr != null){
            extraData(arr);
        }
    }

    private long startSearchTime;
    private int currentG;

    private void extraData(String[] p){
        if(opList.size() > 2){
            mHandler.removeMessages(EVENT_GET_CELLINFO);
        }
        String o = p[0];
        Log.i("gejun", "cell: " + o);
        if("+ECELL: 0".equals(o)) return;
        String[] subItems = o.split(",");
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
        int g = 0;
        if(rat.equals("0")){//2g
            g = 0;
        } else if(rat.equals("2")) {//3g
            g = 1;
        } else if(rat.equals("7")) {//4g
            g = 3;
        }

        //log
        if(generations != null && generations.size() > 0){
            for(int i =0;i<generations.size();i++){
                Log.i("gejun","g" + i + ":" + generations.get(i));
            }
        }
        if(opList != null && opList.size() > 0){
            for(int i =0;i<opList.size();i++){
                Log.i("gejun","op" + i + ":" + opList.get(i));
            }
        }

        if(!generations.contains(g) || opList.contains(mnc)){//this g had got
            long lastTime = System.currentTimeMillis() - startSearchTime;
            Log.i("gejun","lasttime = " + lastTime);
            if(lastTime > 15000 && lastTime < 20000){
                Util.invokeAT(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"},
                        mHandler.obtainMessage(EVENT_SET_GENERATION));
            }
            if(startSearchTime != 0 && lastTime > 30000){
                Log.i("gejun","**********************fail search op: g = " + currentG + ", mnc = " + mnc);
                generations.remove(new Integer(currentG));
                if(generations.size() > 0){
                    if(generations.get(0) == 4){
                        startSearchTime = 0;
                        mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
                        return;
                    }
                    startSearchTime = System.currentTimeMillis();
                    currentG = generations.get(0);
                    Util.invokeAT(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"},
                            mHandler.obtainMessage(EVENT_SET_GENERATION));
                } else {
                    Log.i("gejun",mnc + " has search over!switch another or end!");
//                    sendResult();
                    opList.add(mnc);
                    if(opList.size() == 2){
                        Log.i("gejun","search end!!!!!!!");
                        //search over
                        sendResult();
                        mHandler.removeMessages(EVENT_GET_CELLINFO);
                    } else {
                        initGs();
                        //switch sim
                        Util.startSetOPService(this, "switch");
                    }
                }
            }
            return;
        } else {
            result.add(o);//add new result
            generations.remove(new Integer(g));
            //search other g
            if(generations.size() > 0){
                if(generations.get(0) == 4){
                    startSearchTime = 0;
                    mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
                    return;
                }
                startSearchTime = System.currentTimeMillis();
                currentG = generations.get(0);
                Log.i("gejun","set nextG = " + currentG);
                Util.invokeAT(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"},
                        mHandler.obtainMessage(EVENT_SET_GENERATION));
            } else {
                Log.i("gejun",mnc + " has search over, change another or end!!!");
                opList.add(mnc);
                if(opList.size() == 2 ){
                    Log.i("gejun","search over!!!");
                    sendResult();
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                } else {
                    initGs();
                    //switch sim
                    Util.startSetOPService(this, "switch");
                }
            }
        }
    }

    private void sendResult(){
        String[] fileInfo = Util.saveToXml(this, Util.parseResults(result));
        Intent it = new Intent("com.kuaikan.send_result");
        it.putStringArrayListExtra("result", result);
        it.putExtra("uuid", fileInfo[0]);
        it.putExtra("file_path", fileInfo[1]);
        sendBroadcast(it);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.i("gejun","OneKeyService onDestroy!!!");
    }

    private static final String ACTION_SEARCH_OVER = "com.kuaikan.action.SEARCH_OVER";
    private static final String ACTION_GET_CURRENT_SIM = "com.kuaikan.action.GET_SIM_INFO";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("gejun","receive Action: " + action);
            if(action.equals(ACTION_SEARCH_OVER)){
                sendResult();
                mHandler.removeMessages(EVENT_GET_CELLINFO);
            } else if(action.equals(ACTION_GET_CURRENT_SIM)){
                generations.clear();
                generations.add(3);
                generations.add(4);
                Util.invokeAT(new String[]{"AT+ERAT=3,0","+ERAT"},
                        mHandler.obtainMessage(EVENT_SET_GENERATION));
            }
        }
    };
}
