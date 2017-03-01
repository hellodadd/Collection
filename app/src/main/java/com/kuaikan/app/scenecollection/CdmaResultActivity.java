package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.bean.Result;
import com.kuaikan.app.scenecollection.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.kuaikan.app.scenecollection.util.Util.OP_TELCOM;

public class CdmaResultActivity extends Activity{

    private int networkModeType;
    List<Result> resultList;
    private ListView list;
    private DataAdapter mDataAdapter;
    private String mcc;
    private String mnc;
    private String sid;
    private String nid;
    private String bid;

    private int rx_power1;

    private RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("gejun","*************CDMA onCreate() savedInstanceState = " + savedInstanceState);
        setContentView(R.layout.cdma_list);
        if(savedInstanceState == null){
//            OpSetting.getIntentce().switchSimCard(this, OpSetting.OP_TELCOM);
            Util.startSetOPService(this, OP_TELCOM);
        } else {
            mcc = savedInstanceState.getString("mcc");
            mnc = savedInstanceState.getString("mnc");
            sid = savedInstanceState.getString("sid");
            bid = savedInstanceState.getString("bid");
            nid = savedInstanceState.getString("nid");
            Log.i("gejun","onCreate mcc: " + mcc + ", mnc: " + mnc + ", sid: " + sid + ", nid: " + nid + ", bid: " + bid);
            result = savedInstanceState.getStringArray("result");
            if(result != null && result.length > 0){
                for(int i =0;i<result.length;i++){
                    Log.i("gejun",""+result[i]);
                }
            }
        }
        networkModeType = getIntent().getIntExtra(Util.NETWORK_MODE_TYPE, 0);
        Log.i("gejun","networkModeType = " + networkModeType);

        resultList = new ArrayList<Result>();

        list = (ListView) findViewById(R.id.list);
        mDataAdapter = new DataAdapter(this);
        list.setAdapter(mDataAdapter);
        progress = (RelativeLayout) findViewById(R.id.progress);

        Util.invokeAT4CDMA(new String[]{"AT+CPON","+CPON"}, null);
        init(new String[]{"AT+ERAT=7,0", "+ERAT"},
                new String[]{"AT+COPS=1,2,46003","+COPS"});

        Log.i("a","mHandler = " + mHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_GET_CELLINFO);
    }

    private static final int EVENT_SET_GENERATION = 0;
    private static final int EVENT_SET_OP = 1;
    private static final int EVENT_GET_NETWORKMODE = 2;
    private static final int EVENT_GET_RSSI = 3;
    private static final int EVENT_SET_SUO = 4;
    private static final int EVENT_GET_CELLINFO = 5;

    private static final int EVENT_GET_BID = 6;
    private static final int EVENT_OPEN_CDMA = 7;

    private void setGeneration(String[] atCmd){
//        phone.invokeOemRilRequestStrings(atCmd, mHandler
//                .obtainMessage(EVENT_SET_GENERATION));
        Util.invokeAT4CDMA(atCmd, mHandler.obtainMessage(EVENT_SET_GENERATION));
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
                    Util.invokeAT4CDMA(new String[]{"AT+ESUO=9",""},
                            mHandler.obtainMessage(EVENT_SET_SUO));

                    mHandler.sendEmptyMessage(EVENT_GET_CELLINFO);
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

                    Util.invokeAT4CDMA(new String[]{"AT+VLOCINFO?", "+VLOCINFO"},
                            mHandler.obtainMessage(EVENT_GET_BID));

                    Util.invokeAT4CDMA(new String[]{"AT+ECENGINFO=1,19","+ECENGINFO"},
                            mHandler.obtainMessage(EVENT_GET_NETWORKMODE));

                    mHandler.removeMessages(EVENT_GET_CELLINFO);
                    mHandler.sendEmptyMessageDelayed(EVENT_GET_CELLINFO, 2000);
                    break;
                }
                case EVENT_GET_BID:{
                    showResult(msg, "EVENT_GET_BID");
                    break;
                }
                case EVENT_OPEN_CDMA:{
                    showResult(msg, "OPEN_CDMA");
                    break;
                }
            }
        }
    };

    private void showResult(Message msg, String tag){
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
            Log.i("gejun",tag + "e = " + e.toString());
        }
        extraData(arr);
    }

    private String[] result = null;

    private void extraData(String[] o){
        if(o.length == 0) return;
        /*
        get mcc,mnc,sid,bid,nid
        +VLOCINFO:6,460,3,13840,16,12642,448799,1748144
        +VLOCINFO:<rev>,<mcc>,<mnc>,<sid>,<nid>,<bs_id>,<bs_lat>,<bs_long>
         */
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

        /*
        get pn rx
        +ECENGINFO:“1xRTT_Radio_Info”,<channel>,<band_class>,<pilot_pn_offset>,<rx_power1>,<rx_power2>,<tx_power>,<tx_ant_id>,<FER>
+ECENGINFO:”1xRTT_Serving_Neighbr_Set_Info”,<num_in_active_set>,<pilot_pn_1>,<pilot_ecio_1>,<pilot_phase_1>,…,<pilot_pn_n>,<pilot_ecio_n>,<pilot_phase_n>,
<num_in_cand_set>,<pilot_pn_1>,<pilot_ecio_1>,<pilot_phase_1>,…,<pilot_pn_n>,<pilot_ecio_n>,<pilot_phase_n>,
<num_in_nghbr_set>,<pilot_pn_1>,<pilot_ecio_1>,<pilot_phase_1>,…,<pilot_pn_n>,<pilot_ecio_n>,<pilot_phase_n>

EVENT_GET_NETWORKMODE0 = +ECENGINFO:"1xRTT_Radio_Info",283,0,54,-67,-150,0
    EVENT_GET_NETWORKMODE1 = +ECENGINFO:"1xRTT_Info",5,1,0x00,0x00,0x02,0x0D,13840,16,51,3,448799,1748144,1,1,359,92,2,0,26,30,5,40
    EVENT_GET_NETWORKMODE2 = +ECENGINFO:"1xRTT_Serving_Neighbr_Set_Info",1,54,7,3457,0,9,250,35,16036,418,45,26752,206,42,13184,308,44,19712,72,41,4608,60,43,3840,420,42,26880,124,43,7936,432,43,27648
         */
        if(o[0].startsWith("+ECENGINFO:\"1xRTT_Radio_Info\"")){
            rx_power1 = Integer.parseInt(o[0].split(",")[4]);
            Log.i("gejun","rx_power1: " + rx_power1);
        }

        if(o[2].split(",")[1].equals("0")){
           return;
        }
        Log.i("gejun","************save result*************");
        result = o;
        if(o[2].startsWith("+ECENGINFO:\"1xRTT_Serving_Neighbr_Set_Info\"")){
            String[] info2 = o[2].split(",");
            int cand_set_count = Integer.parseInt(info2[5]);
            Log.i("gejun","cand_set_count: " + cand_set_count);
            int cellcount = Integer.parseInt(info2[6 + cand_set_count * 3]) + 1;
            Log.i("gejun","cdma cell count: " + cellcount);
            resultList.clear();
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
                resultList.add(item);
            }
        }
        mDataAdapter.notifyDataSetChanged();
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("result", result);
        outState.putString("mcc",mcc);
        outState.putString("mnc",mnc);
        outState.putString("sid",sid);
        outState.putString("bid",bid);
        outState.putString("nid",nid);
    }

    class DataAdapter extends BaseAdapter {
        private Context context;
        public DataAdapter(Context context)
        {
            this.context = context;
        }
        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.cdma_item, null);
            TextView bid = (TextView)view.findViewById(R.id.bid);
            TextView nid = (TextView)view.findViewById(R.id.nid);
            TextView sid = (TextView)view.findViewById(R.id.sid);
            TextView pn = (TextView)view.findViewById(R.id.pn);
            TextView rx = (TextView)view.findViewById(R.id.rx);

            CdmaResult resultItem = (CdmaResult) resultList.get(position);
            bid.setText(resultItem.getBid());
            nid.setText(resultItem.getNid());
            sid.setText(resultItem.getSid());
            pn.setText(resultItem.getPn());
            rx.setText(resultItem.getRx());

            return view;
        }

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
            Util.saveToXml(this, resultList);
        }
        return super.onOptionsItemSelected(item);
    }
}
