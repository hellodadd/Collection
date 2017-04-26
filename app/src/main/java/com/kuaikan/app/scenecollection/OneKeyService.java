package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.app.scenecollection.adapter.CDMADataAdapter;
import com.kuaikan.app.scenecollection.adapter.DataAdapter;
import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.bean.GsmResult;
import com.kuaikan.app.scenecollection.bean.Result;
import com.kuaikan.app.scenecollection.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.kuaikan.app.scenecollection.R.id.cdma;
import static com.kuaikan.app.scenecollection.R.id.startTime;
import static com.kuaikan.app.scenecollection.util.Util.EVENT_CELL_INFO;
import static com.kuaikan.app.scenecollection.util.Util.EVENT_COPS;

public class OneKeyService extends Service{

    //Ã¿Ò»¸öratÏÂËÑµ½µÄcellÊý£¬
    /**
     * 2g:2 cmcc cu
     * 3g:2 cmcc cu
     * 4g:3 cmcc cu ct
     */
    int resultCount = 0;

    //µ±Ç°ÔÚËÑË÷µÄrat
    /**
     * 0:2g
     * 2:3g
     * 7:4g
     */
    String currentRat = "0";

    private int attemptFlag = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final static int EVENT_GET_COPS = 99;
    private boolean save = true;

    private boolean isShowNow = false;
    private boolean isQuickSearch = false;

    private final static int EVENT_EBTSAP = 200;
    private final static int EVENT_EBTSAP_ON = 201;

    @Override
    public void onCreate() {
        //Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
        /*Util.invokeAT(this,new String[]{"AT+EBTSAP=0", "+EBTSAP"},
                mHandler.obtainMessage(EVENT_EBTSAP));*/
        startRequstByFDD();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Util.AtERAT(currentRat, mHandler.obtainMessage(Util.EVENT_ERAT));
        if(intent.getBooleanExtra("show", false)) save = false;
        if(intent.getBooleanExtra("is_show_now", false)){
            isShowNow = true;
        }

        if(Util.getSystemPropertiesBoolean(this,"persist.collection.complete",false)){
            isQuickSearch = true;
        }

        if(intent.getBooleanExtra("is_quick_search",false)){
            isQuickSearch = true;
        }

        if(intent.getBooleanExtra("is_all_search",false)){
            isQuickSearch = false;
        }

        Log.d("zwb","zwb -------- isQuickSearch = " + isQuickSearch);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startRequstByFDD(){
        int modemType = Util.reflectModemType();
        if(modemType != Util.MD_TYPE_LWG){
            Util.reflectSetModemSelectionMode(0, Util.MD_TYPE_LWG);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
                    Util.AtERAT(currentRat, mHandler.obtainMessage(Util.EVENT_ERAT));
                }
            },10000);
        }else{
            Log.d("gejun","zwb -------- startRequstByFDD ");
            Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
            Util.AtERAT(currentRat, mHandler.obtainMessage(Util.EVENT_ERAT));
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_EBTSAP:{
                    Util.showOriginResult(msg, "EVENT_EBTSAP");
                    startRequstByFDD();
                    break;
                }
                case Util.EVENT_ERAT:{
                    Util.showOriginResult(msg, Util.ERAT);
                    //search order cmcc cu ct
                    if(resultCount == 0){
                        Util.AtCOPS("46000", mHandler.obtainMessage(EVENT_COPS));
                    } else if(resultCount == 1){
                        Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                    } else if(resultCount == 2){
                        Util.AtCOPS("46011", mHandler.obtainMessage(EVENT_COPS));
                    }
                    break;
                }
                case EVENT_COPS:{
                    Util.showOriginResult(msg, Util.COPS);
                    //get cellinfo
                    try {
                        Util.getCellInfo(mHandler.obtainMessage(Util.EVENT_CELL_INFO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.removeMessages(EVENT_COPS);
                    mHandler.sendEmptyMessageDelayed(EVENT_COPS, 2000);
                    break;
                }
                case Util.EVENT_CELL_INFO:{
                    showResult(msg, Util.ECELL);
                    break;
                }
                case EVENT_GET_CDMA_INFO:{
                    Util.invokeAT4CDMA(new String[]{"AT+CPON","+CPON"}, null);

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
                case EVENT_GET_COPS:{
                    Util.showOriginResult(msg, "GET_COPS");
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
    private String[] cdmaResult;
    private int cdmaCount = 0;
    String vlocinfo="";
    private void extraData1(String[] o){
        if(o.length == 0) return;
        int repeatTime = isQuickSearch ? 2 : 10;
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
            cdmaCount++;
            if(cdmaCount < repeatTime) return;
        }
        Log.i("gejun","************save result*************");
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
        resultLists1.add(vlocinfo);
        if(cdmaCount != repeatTime) {
            cdmaResult = o;
            for (int i = 0; i < o.length; i++) {
                cdmaResultList.add(o[i]);
                resultLists1.add(o[i]);
            }
        }

        if(save){
            String[] fileInfo = Util.saveToXml(this, Util.parseResults(resultLists1));
            Intent it = new Intent("com.kuaikan.send_result");
            it.putStringArrayListExtra("result", resultLists1);
            it.putExtra("uuid", fileInfo[0]);
            it.putExtra("file_path", fileInfo[1]);
            sendBroadcast(it);
        }

        Intent it = new Intent("com.kuaikan.nonsim_send_result");
        it.putStringArrayListExtra("result", resultLists);
        it.putStringArrayListExtra("cdma_result", cdmaResultList);
        it.putExtra("sid", sid);
        it.putExtra("nid", nid);
        it.putExtra("bid", bid);
        it.putExtra("all_search_end",true);
        sendBroadcast(it);

        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

        stopSelf();
    }

    private void endRequst(){
        Intent it = new Intent("com.kuaikan.nonsim_send_result");
        it.putStringArrayListExtra("result", resultLists);
        it.putStringArrayListExtra("cdma_result", cdmaResultList);
        it.putExtra("sid", sid);
        it.putExtra("nid", nid);
        it.putExtra("bid", bid);
        sendBroadcast(it);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

       /* Util.invokeAT(this,new String[]{"AT+EBTSAP=1", "+EBTSAP"},
                mHandler.obtainMessage(EVENT_EBTSAP_ON));*/

        Log.i("gejun","OneKeyService onDestroy!");
    }

    private void showResult(Message msg, String tag){
        String[] arr = null;
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field result = arC.getDeclaredField("result");
            Object resultString = result.get(msg.obj);
            if(resultString == null) return;
            arr = (String[]) resultString;
            for(int i =0;i<arr.length;i++){
                Log.i("gejun","show Result = " + arr[i]);
            }

        } catch (Exception e){
            Log.i("gejun","e = " + e.toString());
        }
        if(arr != null && !arr[0].equals("+ECELL: 0")) {
            extraData(arr);
        }
    }

    private int getCellCount(String cellinfo){
        return Integer.parseInt(cellinfo.substring(8, 9));
    }

    private boolean isOneSearch = false;

    private void extraData(String[] p){
        String o = p[0];
        //o = "+ECELL: 7,7,\"01839603\",\"1877\",460,0,128,48,22,-372,-36,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,127,39,4,-408,-72,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,263,31,23,-440,-34,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,262,29,21,-448,-38,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,263,29,20,-448,-40,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,262,27,20,-456,-40,268435455,268435455,268435455,7,
        // \"0FFFFFFF\",\"FFFF\",268435455,268435455,470,24,11,-468,-58,268435455,268435455,268435455";
        String[] subItems = o.split(",");
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
        int repeatTime = 10;
        if(currentRat.equals(rat) && mnc == getMncFromResultCount(resultCount)){
            if(!isOneSearch){
                resultLists.add(o);
                resultLists1.add(o);
                isOneSearch = true;
            } else {
                int count = resultLists.size();
                if(count > 0){
                    if(getCellCount(resultLists.get(count - 1)) < getCellCount(o)){
                        resultLists.set(count - 1, o);
                        resultLists1.set(count - 1, o);
                    }
                }
            }
            attemptFlag++;

            if(attemptFlag == repeatTime || isQuickSearch) {
                isOneSearch = false;
                attemptFlag = 0;
                resultCount++;//ËÑË÷½á¹û¼Ó1¸ö
                if(isShowNow){
                    endRequst();
                }
                if ((currentRat.equals("0") || currentRat.equals("2"))
                        && resultCount < 2) {
                    Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                } else if (currentRat.equals("7")) {
                    if (resultCount < 2) {
                        Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                    } else if (resultCount < 3) {
                        Util.AtCOPS("46011", mHandler.obtainMessage(EVENT_COPS));
                    }
                }

                if (currentRat.equals("0") && resultCount == 2) {
                    resultCount = 0;
                    currentRat = "2";
                    Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
                } else if (currentRat.equals("2") && resultCount == 2) {
                    resultCount = 0;
                    currentRat = "7";
                    Util.AtERAT("3", mHandler.obtainMessage(Util.EVENT_ERAT));
                } else if (currentRat.equals("7") && resultCount == 3) {
                    resultCount = 0;
                    currentRat = "0";
                    sendCDMARequest();
                }
            }
        } else {
            attemptFlag++;
            if(attemptFlag == repeatTime){
                resultCount++;
            }

            if(attemptFlag == repeatTime) {
                attemptFlag = 0;
                if ((currentRat.equals("0") || currentRat.equals("2"))
                        && resultCount < 2) {
                    Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                } else if (currentRat.equals("7")) {
                    if (resultCount < 2) {
                        Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                    } else if (resultCount < 3) {
                        Util.AtCOPS("46011", mHandler.obtainMessage(EVENT_COPS));
                    }
                }

                if (currentRat.equals("0") && resultCount == 2) {
                    resultCount = 0;
                    currentRat = "2";
                    Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
                } else if (currentRat.equals("2") && resultCount == 2) {
                    resultCount = 0;
                    currentRat = "7";
                    Util.AtERAT("3", mHandler.obtainMessage(Util.EVENT_ERAT));
                } else if (currentRat.equals("7") && resultCount == 3) {
                    resultCount = 0;
                    currentRat = "0";
                    sendCDMARequest();
                }
            }
        }
    }

    private int getMncFromResultCount(int count){
        int mnc = 0;
        if(count == 0){
            mnc = 0;
        } else if(count == 1){
            mnc = 1;
        } else if(count == 2){
            mnc = 11;
        }
        return mnc;
    }

    private ArrayList<String> resultLists = new ArrayList<String>();
    private ArrayList<String> resultLists1 = new ArrayList<String>();
    private ArrayList<String> cdmaResultList = new ArrayList<String>();

    private static final int EVENT_GET_CDMA_INFO = 5;
    private static final int EVENT_GET_ACTIVE_CDMA =  6;
    private static final int EVENT_GET_NEIGHBOR_CDMA =  7;

    private void sendCDMARequest(){
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
        mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
    }
}