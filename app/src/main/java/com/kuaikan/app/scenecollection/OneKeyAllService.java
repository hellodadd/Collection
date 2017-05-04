package com.kuaikan.app.scenecollection;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kuaikan.app.scenecollection.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.kuaikan.app.scenecollection.util.Util.EVENT_COPS;

/**
 * Created by server on 17-3-27.
 */

public class OneKeyAllService extends Service{

    //每一个rat下搜到的cell数，
    /**
     * 2g:2 cmcc cu
     * 3g:2 cmcc cu
     * 4g:3 cmcc cu ct
     */
    int resultCount = 0;

    //当前在搜索的rat
    /**
     * 0:2g
     * 2:3g
     * 7:4g
     */
    String currentRat = "0";

    private int attemptFlag = 0;

    private int currentModemType;
    private boolean isTDDRequst = false;
    private boolean isFDDRequst = false;
    private boolean isWCDMAFind = false;
    private boolean isCULTEFind = false;
    private boolean isTDSCDMAFind = false;
    private int td_scdma_flag = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final static int EVENT_GET_COPS = 99;
    public static final int EVENT_EPBSE = 200;
    public static final int EVENT_ECBAND = 201;
    private static final int EVENT_CFUN_0 = 202;
    private static final int EVENT_CFUN_1 = 203;

    private boolean save = true;
    @Override
    public void onCreate() {
        currentModemType = Util.reflectModemType();
        //Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Util.AtERAT(currentRat, mHandler.obtainMessage(Util.EVENT_ERAT));
        fristStepRequst();
        if(intent.getBooleanExtra("show", false)) save = false;
        return super.onStartCommand(intent, flags, startId);
    }

    private void fristStepRequst(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetModemBand();
                Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
                Util.AtERAT(currentRat, mHandler.obtainMessage(Util.EVENT_ERAT));
            }
        },10000); //wait modem power on
    }

    private void resetModemBand(){
        /*Util.invokeAT(new String[]{"AT+CFUN=0", "+CFUN"},
                mHandler.obtainMessage(EVENT_CFUN_0));
        Util.invokeAT(new String[]{"AT+CFUN=1", "+CFUN"},
                mHandler.obtainMessage(EVENT_CFUN_1));*/
        Util.invokeAT(new String[]{"AT+EPBSE=10,1,5,480","+EPBSE"},
                mHandler.obtainMessage(EVENT_EPBSE));
        Util.invokeAT4CDMA(new String[]{"AT+ECBAND=0","+ECBAND"},
                mHandler.obtainMessage(EVENT_ECBAND));
    }

    private void switchToTDDModemRequst(){
        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

        Util.reflectSetModemSelectionMode(0,Util.MD_TYPE_LTG);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
                isTDDRequst = true;
                isOneSearch = false;
                resultCount = 0;
                currentRat = "2";
                td_scdma_flag = 0;
                Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
            }
        },10000); //delay wait modem power on
    }

    private void switchToFDDModemRequst(){
        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

        Util.reflectSetModemSelectionMode(0,Util.MD_TYPE_LWG);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.atCOPS(mHandler.obtainMessage(EVENT_GET_COPS));
                isFDDRequst = true;
                isOneSearch = false;
                resultCount = 0;
                currentRat = "2";
                Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
            }
        },10000);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Util.EVENT_ERAT:{
                    Util.showOriginResult(msg, Util.ERAT);
                    //search order cmcc cu ct
                    if(isTDDRequst) {
                        String str1 = "AT+COPS=1,2,\"46000\",2";
                        String str2 = "+COPS";
                        Util.invokeAT(new String[]{str1, str2}, mHandler.obtainMessage(EVENT_COPS));
                    }else if(isFDDRequst){
                        Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                    }else {
                        if (resultCount == 0) {
                            Util.AtCOPS("46000", mHandler.obtainMessage(EVENT_COPS));
                        } else if (resultCount == 1) {
                            Util.AtCOPS("46001", mHandler.obtainMessage(EVENT_COPS));
                        } else if (resultCount == 2) {
                            Util.AtCOPS("46011", mHandler.obtainMessage(EVENT_COPS));
                        }
                    }
                    break;
                }
                case EVENT_COPS:{
                    //Util.showOriginResult(msg, Util.COPS);
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
                case EVENT_EPBSE:{
                    Util.showOriginResult(msg, "GET_EPBSE");
                    break;
                }
                case EVENT_ECBAND:{
                    Util.showOriginResult(msg, "GET_ECBAND");
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
            if(cdmaCount < 10) return;
        }
        Log.i("gejun","************save result*************");
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
        resultLists1.add(vlocinfo);
        if(cdmaCount != 10) {
            cdmaResult = o;
            for (int i = 0; i < o.length; i++) {
                cdmaResultList.add(o[i]);
                resultLists1.add(o[i]);
            }
        }

        if(currentModemType == Util.MD_TYPE_LWG){
            switchToTDDModemRequst();
        }else if(currentModemType == Util.MD_TYPE_LTG){
            switchToFDDModemRequst();
        }else{
            endAllRequst();
        }
    }

    private void sendCurentReauslt(){
        Intent it = new Intent("com.kuaikan.nonsim_send_result");
        it.putStringArrayListExtra("result", resultLists);
        it.putStringArrayListExtra("cdma_result", cdmaResultList);
        it.putExtra("sid", sid);
        it.putExtra("nid", nid);
        it.putExtra("bid", bid);
        sendBroadcast(it);
    }

    private void endAllRequst(){

        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

        Util.reflectSetModemSelectionMode(0, currentModemType);

        /*
        if(save){
            String[] fileInfo = Util.saveToXml(this, Util.parseResults(resultLists1));
            Intent it = new Intent("com.kuaikan.send_result");
            it.putStringArrayListExtra("result", resultLists1);
            it.putExtra("uuid", fileInfo[0]);
            it.putExtra("file_path", fileInfo[1]);
            sendBroadcast(it);
        }
        */

        Intent it = new Intent("com.kuaikan.nonsim_send_result");
        it.putStringArrayListExtra("result", resultLists);
        it.putStringArrayListExtra("cdma_result", cdmaResultList);
        it.putExtra("sid", sid);
        it.putExtra("nid", nid);
        it.putExtra("bid", bid);
        it.putExtra("all_search_end",true);
        sendBroadcast(it);

        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(Util.EVENT_CELL_INFO);
        mHandler.removeMessages(EVENT_COPS);

        Util.reflectSetModemSelectionMode(0, currentModemType);
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

        if(isTDDRequst){
            if(arr != null && arr[0].equals("+ECELL: 0")){
                td_scdma_flag++;
                if(td_scdma_flag == 30){
                    endAllRequst();   //can not find td-scdma cell info
                }
            }
        }

        if(arr != null && !arr[0].equals("+ECELL: 0")) {
            if(isTDDRequst) {
                extraDataTDD(arr);
            }else if(isFDDRequst){
                extraDataFDD(arr);
            }else{
                extraData(arr);
            }
        }
    }

    private int getCellCount(String cellinfo){
        return Integer.parseInt(cellinfo.substring(8, 9));
    }

    private boolean isOneSearch = false;

    private void extraDataTDD(String[] p){
        String o = p[0];
        String[] subItems = o.split(",");
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
        if(rat.equals("2") && mnc == 0){//only td-scdma
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
            if(attemptFlag == 5){
                attemptFlag = 0;
                isTDSCDMAFind = true;
                SharedPreferences preferences = getSharedPreferences("tdscdma",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("key_tdscdma",o);
                editor.apply();
                endAllRequst();
            }else{
                //Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
            }
        }else{
            attemptFlag++;
            if(attemptFlag == 10){
                attemptFlag = 0;
                if(!isTDSCDMAFind){
                    SharedPreferences sharedPreferences = getSharedPreferences("tdscdma",MODE_PRIVATE);
                    String fakeTDSCDMA = sharedPreferences.getString("key_tdscdma","");
                    if(fakeTDSCDMA != null && !fakeTDSCDMA.isEmpty()){
                        resultLists.add(fakeTDSCDMA);
                        resultLists1.add(fakeTDSCDMA);
                    }
                }
                endAllRequst();
            }else{
                //Util.AtERAT("1", mHandler.obtainMessage(Util.EVENT_ERAT));
            }
        }
    }

    private void extraDataFDD(String[] p){
        String o = p[0];
        String[] subItems = o.split(",");
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
        if(rat.equals("2") && mnc == 1){//wcdma
            if(!isWCDMAFind){
                resultLists.add(o);
                resultLists1.add(o);
                isWCDMAFind = true;
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
            if(attemptFlag == 5){
                attemptFlag = 0;
                sendCurentReauslt();
                Util.AtERAT("3", mHandler.obtainMessage(Util.EVENT_ERAT));
            }

        }else if(rat.equals("7") && mnc == 1){//cu_lte
            if(!isCULTEFind){
                resultLists.add(o);
                resultLists1.add(o);
                isCULTEFind = true;
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
            if(attemptFlag == 5){
                attemptFlag = 0;
                sendCurentReauslt();
            }
        }else{
            attemptFlag++;
            if(attemptFlag == 10){
                attemptFlag = 0;
                endAllRequst();
            }
        }

        if(isCULTEFind && isWCDMAFind){
            endAllRequst();
        }
    }

    private void extraData(String[] p){
        String o = p[0];
        String[] subItems = o.split(",");
        String rat = subItems[1];
        int mnc = Integer.parseInt(subItems[5]);
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

            if(rat.equals("2") && mnc == 0 && currentModemType == Util.MD_TYPE_LTG){//td-scdma
                isTDSCDMAFind = true;
                SharedPreferences preferences = getSharedPreferences("tdscdma",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("key_tdscdma",o);
                editor.apply();
            }

            attemptFlag++;

            if(attemptFlag == 10) {
                isOneSearch = false;
                attemptFlag = 0;
                resultCount++;//搜索结果加1个

                sendCurentReauslt();

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

                    if(!isTDSCDMAFind && currentModemType == Util.MD_TYPE_LTG){
                        SharedPreferences sharedPreferences = getSharedPreferences("tdscdma",MODE_PRIVATE);
                        String fakeTDSCDMA = sharedPreferences.getString("key_tdscdma","");
                        if(fakeTDSCDMA != null && !fakeTDSCDMA.isEmpty()){
                            resultLists.add(fakeTDSCDMA);
                            resultLists1.add(fakeTDSCDMA);
                        }
                    }

                    sendCDMARequest();
                }
            }
        } else {
            attemptFlag++;
            if(attemptFlag == 10){
                resultCount++;
            }

            if(attemptFlag == 10) {
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

                    if(!isTDSCDMAFind && currentModemType == Util.MD_TYPE_LTG){
                        SharedPreferences sharedPreferences = getSharedPreferences("tdscdma",MODE_PRIVATE);
                        String fakeTDSCDMA = sharedPreferences.getString("key_tdscdma","");
                        if(fakeTDSCDMA != null && !fakeTDSCDMA.isEmpty()){
                            resultLists.add(fakeTDSCDMA);
                            resultLists1.add(fakeTDSCDMA);
                        }
                    }
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
