package com.kuaikan.app.scenecollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Random;

import static com.kuaikan.app.scenecollection.R.id.startTime;

/**
 * Created by server on 17-3-27.
 */

public class NonSimOneKeyAllActivity extends AppCompatActivity {


    private ProgressBar bar;
    private ListView list1;
    private ListView list2;
    private ListView list3;
    private ListView list4;
    private ListView list5;
    private ListView list6;
    private ListView list7;
    private ListView list8;
    private ScrollView sv;

    private TextView start;
    private TextView end;

    private int flag = 0;
    private int flagB = 0;
    private int flagG = 0;
    private int flagTD = 0;

    private ArrayList<String> resultLists = new ArrayList<String>();
    private ArrayList<String> cdmaResultList = new ArrayList<String>();

    ///private List<GsmResult> resultList;
    private List<GsmResult> result1;
    private List<GsmResult> result2;
    private List<GsmResult> result3;
    private List<GsmResult> result4;
    private List<GsmResult> result5;
    private List<GsmResult> result6;
    private List<GsmResult> result7;
    private List<CdmaResult> result8;
    private List<Result> totalList;

    private DataAdapter mDataAdapter1;
    private DataAdapter mDataAdapter2;
    private DataAdapter mDataAdapter3;
    private DataAdapter mDataAdapter4;
    private DataAdapter mDataAdapter5;
    private DataAdapter mDataAdapter6;
    private DataAdapter mDataAdapter7;
    private CDMADataAdapter mDataAdapter8;

    //    private Phone phone;
    private ArrayList<Integer> opList = new ArrayList<Integer>();
    private boolean cmcc2g = false;
    private boolean cmcc3g = false;
    private boolean cmcc4g = false;
    private boolean cu2g = false;
    private boolean cu3g = false;
    private boolean cu4g = false;
    private boolean telcom4g = false;

    private ArrayList<Integer> generations = new ArrayList<Integer>();

    private static final String ACTION_SEARCH_OVER = "com.kuaikan.action.SEARCH_OVER";
    private static final String ACTION_GET_CURRENT_SIM = "com.kuaikan.action.GET_SIM_INFO";

    private long startSearchTime;
    private int currentG;

    private long onekeyStart;

    private boolean isOneKeyAll = false;
    private boolean isSearchEnd = false;
    private ImageView saveBtn;
    private TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.one_key_all);
        Log.i("gejun","onCreate savedInstanceState = " + savedInstanceState);
        if(savedInstanceState != null){
//            opList = savedInstanceState.getIntegerArrayList("ops");
//            resultLists = savedInstanceState.getStringArrayList("resultList");
//            generations = savedInstanceState.getIntegerArrayList("generations");
//            currentG = savedInstanceState.getInt("currentg");
//            startSearchTime = savedInstanceState.getLong("searchtime");
            onekeyStart = savedInstanceState.getLong("onekeystart");
//            cdmaResult = savedInstanceState.getStringArray("cdmaresult");
            sid = savedInstanceState.getString("sid");
            bid = savedInstanceState.getString("bid");
            nid = savedInstanceState.getString("nid");
//            cdmaResultList = savedInstanceState.getStringArrayList("cdmalist");
        }  else {
//            initGs();
            onekeyStart = System.currentTimeMillis();
        }

        if(getIntent() != null){
            isOneKeyAll = getIntent().getBooleanExtra("one_key_all", false);
        }

        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);
        list3 = (ListView) findViewById(R.id.list3);
        list4 = (ListView) findViewById(R.id.list4);
        list5 = (ListView) findViewById(R.id.list5);
        list6 = (ListView) findViewById(R.id.list6);
        list7 = (ListView) findViewById(R.id.list7);
        list8 = (ListView) findViewById(R.id.list8);

        bar = (ProgressBar) findViewById(R.id.progress);
        sv = (ScrollView) findViewById(R.id.sv);
        saveBtn = (ImageView)findViewById(R.id.toolbar_right_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSearchEnd) {
                    Util.saveToXml(NonSimOneKeyAllActivity.this, totalList);
                }else{
                    Toast.makeText(NonSimOneKeyAllActivity.this,
                            R.string.none_info, Toast.LENGTH_SHORT).show();
                }
            }
        });
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        if(isSearchEnd){
            toolbar_title.setText(getResources().getString(R.string.search_end));
        }else{
            toolbar_title.setText(getResources().getString(R.string.search_ing));
        }

        start = (TextView) findViewById(startTime);
        start.setText(Util.getTime(onekeyStart));
        end = (TextView) findViewById(R.id.endTime);

        result1 = new ArrayList<GsmResult>();
        result2 = new ArrayList<GsmResult>();
        result3 = new ArrayList<GsmResult>();
        result4 = new ArrayList<GsmResult>();
        result5 = new ArrayList<GsmResult>();
        result6 = new ArrayList<GsmResult>();
        result7 = new ArrayList<GsmResult>();
        result8 = new ArrayList<CdmaResult>();
        totalList = new ArrayList<Result>();

        mDataAdapter1 = new DataAdapter(this);
        mDataAdapter1.setData(result1);
        list1.setAdapter(mDataAdapter1);

        mDataAdapter2 = new DataAdapter(this);
        mDataAdapter2.setData(result2);
        list2.setAdapter(mDataAdapter2);

        mDataAdapter3 = new DataAdapter(this);
        mDataAdapter3.setData(result3);
        list3.setAdapter(mDataAdapter3);

        mDataAdapter4 = new DataAdapter(this);
        mDataAdapter4.setData(result4);
        list4.setAdapter(mDataAdapter4);

        mDataAdapter5 = new DataAdapter(this);
        mDataAdapter5.setData(result5);
        list5.setAdapter(mDataAdapter5);

        mDataAdapter6 = new DataAdapter(this);
        mDataAdapter6.setData(result6);
        list6.setAdapter(mDataAdapter6);

        mDataAdapter7 = new DataAdapter(this);
        mDataAdapter7.setData(result7);
        list7.setAdapter(mDataAdapter7);

        mDataAdapter8 = new CDMADataAdapter(this);
        mDataAdapter8.setData(result8);
        list8.setAdapter(mDataAdapter8);


//        Util.startSetOPService(this, "get_current_siminfo");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startOneKeyService(){
        Intent it = new Intent();
        it.setPackage("com.kuaikan.app.scenecollection");
        it.setAction("com.kuaikan.nonsim_one_key_all");
        it.putExtra("show", true);
        startService(it);
    }

    private void stopOneKeyService(){
        Intent it = new Intent();
        it.setPackage("com.kuaikan.app.scenecollection");
        it.setAction("com.kuaikan.nonsim_one_key_all");
        stopService(it);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!isSearchEnd) {
            startOneKeyService();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GET_CURRENT_SIM);
        filter.addAction(ACTION_SEARCH_OVER);
        filter.addAction("com.kuaikan.nonsim_send_result");
        registerReceiver(mReceiver, filter);

        /*if(opList.size() < 2) {
            mHandler.removeMessages(EVENT_GET_CELLINFO);
            mHandler.sendEmptyMessage(EVENT_GET_CELLINFO);
        } else {
            showList();
        }*/
//        mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_GET_CELLINFO);
        unregisterReceiver(mReceiver);
        stopOneKeyService();
    }

    private static final int EVENT_SET_GENERATION = 0;
    private static final int EVENT_SET_OP = 1;
    private static final int EVENT_GET_NETWORKMODE = 2;
    private static final int EVENT_GET_RSSI = 3;
    private static final int EVENT_GET_CELLINFO = 4;
    private static final int EVENT_GET_CDMA_INFO = 5;
    private static final int EVENT_GET_ACTIVE_CDMA =  6;
    private static final int EVENT_GET_NEIGHBOR_CDMA =  7;


    private void setGeneration(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_GENERATION));
        Log.i("gejun","ERAT: " + atCmd[0]);
        Util.invokeAT(atCmd, mHandler.obtainMessage(EVENT_SET_GENERATION));
    }

    private void setOp(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_OP));
    }

    private void init(String[] g, String[] op){
        setOp(op);
        setGeneration(g);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_SET_GENERATION:{
//                    AsyncResult ar = (AsyncResult) msg.obj;
//                    Log.i("gejun","exception = " + ar.exception);
                    showException(msg);

                    break;
                }
                case EVENT_GET_NETWORKMODE:{
                    showResult(msg, "EVENT_GET_NETWORKMODE");
                    break;
                }
                case EVENT_GET_RSSI:{
//                    showResult(msg, "EVENT_GET_NETWORKMODE");
                    break;
                }
                case EVENT_GET_CELLINFO:{
                    try {
                        Util.getCellInfo(mHandler.obtainMessage(EVENT_GET_NETWORKMODE));
                    } catch (Exception e){
                        Log.i("gejun","e = " + e.toString());
                    }
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
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
        cdmaResult = o;
        for(int i=0;i<o.length;i++){
            cdmaResultList.add(o[i]);
        }
        opList.add(Integer.parseInt(mnc));
        Log.i("gejun","@@@@@@@@@@@@@@@@@@@@@" + mnc);
        if(opList.size() == 2){
            Log.i("gejun","111111");
            showList();
            mHandler.removeMessages(EVENT_GET_CELLINFO);
        } else {
            initGs();
            //switch sim
            Util.startSetOPService(this, "switch");
        }
    }

    private void showException(Message msg){
        //                    AsyncResult ar = (AsyncResult) msg.obj;
//                    Log.i("gejun","exception = " + ar.exception);
        try {
            Class arC = Class.forName("android.os.AsyncResult");
            Field exception = arC.getDeclaredField("exception");
            Object resultString = exception.get(msg.obj);
            Log.i("gejun","exception: " + resultString);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

    //+ECELL: 1,0,"0000121C","144B",460,1,268435455,57,99,-216,1,255,268435455,268435455
    private void extraData(String[] p){
        String o = p[0];
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
            if(lastTime>15000 && lastTime < 20000){
                setGeneration(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"});
            }
            if(startSearchTime != 0 && lastTime > 30000){//search fail
                Log.i("gejun","**********************fail search op: g = " + currentG + ", mnc = " + mnc);
                generations.remove(new Integer(currentG));
                if(generations.size() > 0){
                    if(generations.get(0) == 4){
                        startSearchTime = 0;
                        sendCDMARequest();
                        return;
                    }
                    startSearchTime = System.currentTimeMillis();
                    currentG = generations.get(0);
                    setGeneration(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"});
                } else {
                    Log.i("gejun","@@@@@@@@@@@@@@@@@@@@1@" + mnc);
                    opList.add(mnc);
                    if(opList.size() >= 2){
                        Log.i("gejun","111111");
                        showList();
                    } else {
                        initGs();
                        //switch sim
                        Util.startSetOPService(this, "switch");
                    }
                }
            }
            return;
        } else {
            resultLists.add(o);//add new result
            generations.remove(new Integer(g));
            //search other g
            if(generations.size() > 0){
                if(generations.get(0) == 4){
                    startSearchTime = 0;
                    sendCDMARequest();
                    return;
                }
                startSearchTime = System.currentTimeMillis();
                currentG = generations.get(0);
                Log.i("gejun","set nextG = " + currentG);
                setGeneration(new String[]{"AT+ERAT="+ currentG +",0","+ERAT"});
            } else {
                Log.i("gejun","@@@@@@@@@@@@@@@@@@@@@2" + mnc);
                opList.add(mnc);
                if(opList.size() >= 2 ){
                    Log.i("gejun","222222222222222");
                    showList();
                } else {
                    initGs();
                    //switch sim
                    Util.startSetOPService(this, "switch");
                }
            }
        }
    }
    ArrayList<String> saved = new ArrayList<String>();
    private static final String CU_GSM = "cu_gsm";
    private static final String CU_3G = "cu_3g";
    private static final String CU_4G = "cu_4g";
    private static final String CMCC_GSM = "cmcc_gsm";
    private static final String CMCC_3G = "cmcc_3g";
    private static final String CMCC_4G = "cmcc_4g";
    private static final String TELECOM_4G = "telecom_4g";

    private void showList(){
        mHandler.removeMessages(EVENT_GET_CELLINFO);
        if(isSearchEnd) {
            bar.setVisibility(View.GONE);
            toolbar_title.setText(getResources().getString(R.string.search_end));
            end.setText(Util.getTime(System.currentTimeMillis()));
        }
        sv.scrollTo(0, 0);

        saved.clear();
        String tag = "";
        //cmcc and cu
        List<GsmResult> cmcc_cu = new ArrayList<GsmResult>();
        for(int i=0;i<resultLists.size();i++){
            List<GsmResult> resultList = new ArrayList<GsmResult>();
            GsmResult row1 = new GsmResult();

            String currentCell = resultLists.get(i);
            Log.i("gejun","currentCell" + currentCell);
            String[] cellArrays = currentCell.split(",");
            String g = cellArrays[1];
            String mnc = cellArrays[5];
            String array1 = cellArrays[0].substring(cellArrays[0].length() - 1);
            int count = Integer.parseInt(array1);
            if(g.equals("0")){
                if(mnc.equals("1")){
                    tag = CU_GSM;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cu_gsm));
                } else if(mnc.equals("0")){
                    tag = CMCC_GSM;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cmcc_gsm));
                }
            } else if(g.equals("2")){
                if(mnc.equals("1")){
                    tag = CU_3G;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.wcdma));
                } else if(mnc.equals("0")){
                    tag = CMCC_3G;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.tdscdma));
                }
            } else if(g.equals("7")){
                if(mnc.equals("1")){
                    tag = CU_4G;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cu_lte));
                } else if(mnc.equals("0")){
                    tag = CMCC_4G;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cmcc_lte));
                } else if(mnc.equals("11")){
                    tag = TELECOM_4G;
                    if(saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.telecom_lte));
                }
            }
            resultList.add(row1);

            GsmResult row2 = new GsmResult();
            row2.setLac(getResources().getString(R.string.lac));
            row2.setCellId(getResources().getString(R.string.cell_id));
            row2.setSig1(getResources().getString(R.string.rssi));
            resultList.add(row2);

            long cellId = Long.parseLong(toEight(cellArrays[2]));

            for(int j=0;j<count;j++){
                GsmResult item = new GsmResult();
                item.setAct(cellArrays[1]);//cellArrays[j * 13 + 1]
                if(g.equals("7") && j > 0 && j < 10) {//zhangwuba fake 4G
                    long tempId = Util.getCellidByAt(cellId, j);
                    item.setCellId("\"" + Long.toHexString(tempId) + "\"");
                    item.setLac(cellArrays[3]);
                }else{
                    item.setCellId(cellArrays[j * 13 + 2]);
                    item.setLac(cellArrays[j * 13 + 3]);
                }
                item.setMcc(cellArrays[4]);//cellArrays[j * 13 + 4]
                item.setMnc(cellArrays[5]);//cellArrays[j * 13 + 5]
                item.setPsc_or_pci(cellArrays[j * 13 + 6]);
                item.setSig1(cellArrays[j * 13 + 7]);
                item.setSig2(cellArrays[j * 13 + 8]);
                item.setSig1_in_dbm(cellArrays[j * 13 + 9]);
                item.setSig2_in_dbm(cellArrays[j * 13 + 10]);
                resultList.add(item);
                cmcc_cu.add(item);
                totalList.add(item);
            }

            if(tag.equals(CU_GSM)){
                mDataAdapter1.setData(resultList);
                setListViewHeightBasedOnChildren(list1);
                mDataAdapter1.notifyDataSetChanged();
            }
            if(tag.equals(CU_3G)){
                mDataAdapter2.setData(resultList);
                setListViewHeightBasedOnChildren(list2);
                mDataAdapter2.notifyDataSetChanged();
            }
            if(tag.equals(CU_4G)){
                mDataAdapter3.setData(resultList);
                setListViewHeightBasedOnChildren(list3);
                mDataAdapter3.notifyDataSetChanged();
            }
            if(tag.equals(CMCC_GSM)){
                mDataAdapter4.setData(resultList);
                setListViewHeightBasedOnChildren(list4);
                mDataAdapter4.notifyDataSetChanged();
            }
            if(tag.equals(CMCC_3G)){
                mDataAdapter5.setData(resultList);
                setListViewHeightBasedOnChildren(list5);
                mDataAdapter5.notifyDataSetChanged();
            }
            if(tag.equals(CMCC_4G)){
                mDataAdapter6.setData(resultList);
                setListViewHeightBasedOnChildren(list6);
                mDataAdapter6.notifyDataSetChanged();
            }

            if(tag.equals(TELECOM_4G)){
                mDataAdapter7.setData(resultList);
                setListViewHeightBasedOnChildren(list7);
                mDataAdapter7.notifyDataSetChanged();
            }
        }

        if(cdmaResultList != null && cdmaResultList.size()>0){
            String[] info2 = cdmaResultList.get(2).split(",");
            int cand_set_count = Integer.parseInt(info2[5]);
            Log.i("gejun","cand_set_count: " + cand_set_count);
            int cellcount = Integer.parseInt(info2[6 + cand_set_count * 3]) + 1;
            Log.i("gejun","cdma cell count: " + cellcount);
            result8.clear();
            CdmaResult item0 = new CdmaResult();
            item0.setSid(getResources().getString(R.string.cdma));
            result8.add(item0);

            CdmaResult item1 = new CdmaResult();
            item1.setSid(getResources().getString(R.string.sid));
            item1.setNid(getResources().getString(R.string.nid));
            item1.setBid(getResources().getString(R.string.bid));
            item1.setPn(getResources().getString(R.string.pn));
            item1.setRx(getResources().getString(R.string.rx));
            result8.add(item1);

            for(int i = 0;i<cellcount;i++){
                CdmaResult item = new CdmaResult();
                if(i == 0){
                    item.setSid(sid);
                    item.setNid(nid);
                    item.setBid(bid);
                    item.setPn(info2[2]);
                    item.setRx(""+ (rx_power1 + Integer.parseInt(info2[3])/-2));
                } else {
                    item.setSid(sid);
                    item.setNid(nid);
                    item.setBid(bid);
                    item.setPn(info2[6 + cand_set_count * 3 + 1 + (i-1) * 3]);
                    int rx = Integer.parseInt(info2[6 + cand_set_count * 3 + 1 + ((i-1) * 3) + 1]);
                    item.setRx("" + (rx_power1 + rx/-2));
                }
                result8.add(item);
                totalList.add(item);
            }
            setListViewHeightBasedOnChildren(list8);
            mDataAdapter8.notifyDataSetChanged();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            Result item = (Result) listAdapter.getItem(i);
//            Log.i("gejun","item.LAC = " + item.getLac());
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        Log.i("gejun","ListHeight = " + params.height);
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.save){
            if(bar.getVisibility() == View.VISIBLE){
                Toast.makeText(this, R.string.none_info, Toast.LENGTH_SHORT).show();
                return true;
            }
            Util.saveToXml(this, totalList);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("resultList", resultLists);
        outState.putIntegerArrayList("generations", generations);
        outState.putIntegerArrayList("ops", opList);
        outState.putInt("currentg", currentG);
        outState.putLong("searchtime", startSearchTime);
        outState.putLong("onekeystart", onekeyStart);
        Log.i("gejun","onSaveInstanceState cdmaResult = " + cdmaResult);
        outState.putStringArray("cdmaresult", cdmaResult);
        Log.i("gejun","onSaveInstanceState cdmaResultList = " + cdmaResultList);
        outState.putStringArrayList("cdmalist", cdmaResultList);

        outState.putString("sid",sid);
        outState.putString("bid",bid);
        outState.putString("nid",nid);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_SEARCH_OVER)){
                if(intent.getIntExtra("sim_count", -1) == 0){
                    sendCDMARequest();
                } else {
                    Log.i("gejun", "search over!!!!");
                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    showList();
                }
            } else if(action.equals(ACTION_GET_CURRENT_SIM)){
                generations.clear();
                generations.add(3);
                generations.add(4);
                setGeneration(new String[]{"AT+ERAT=3,0","+ERAT"});
            } else if(action.equals("com.kuaikan.nonsim_send_result")){
                resultLists = intent.getStringArrayListExtra("result");
                cdmaResultList = intent.getStringArrayListExtra("cdma_result");
                sid = intent.getStringExtra("sid");
                bid = intent.getStringExtra("bid");
                nid = intent.getStringExtra("nid");
                isSearchEnd = intent.getBooleanExtra("all_search_end", false);
                showList();
            }
        }
    };

    private void initGs(){
        generations.clear();
        generations.add(0);
        generations.add(1);
        generations.add(3);
    }

    private void sendCDMARequest(){
        mHandler.removeMessages(EVENT_GET_CDMA_INFO);
        mHandler.sendEmptyMessage(EVENT_GET_CDMA_INFO);
    }

    private String toEight(String shiliu) {
        shiliu = shiliu.substring(1, shiliu.length() - 1);
        return Long.parseLong(shiliu, 16) + "";
    }
}
