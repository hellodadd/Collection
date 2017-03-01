package com.kuaikan.app.scenecollection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.kuaikan.app.scenecollection.adapter.CDMADataAdapter;
import com.kuaikan.app.scenecollection.adapter.DataAdapter;
import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.bean.GsmResult;
import com.kuaikan.app.scenecollection.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaozehong on 16-12-9.
 */
public class ShowActivity extends Activity {

    private static final String CU_GSM = "cu_gsm";
    private static final String CU_3G = "cu_3g";
    private static final String CU_4G = "cu_4g";
    private static final String CMCC_GSM = "cmcc_gsm";
    private static final String CMCC_3G = "cmcc_3g";
    private static final String CMCC_4G = "cmcc_4g";
    private static final String TELECOM_4G = "telecom_4g";

    private RelativeLayout bar;
    private ListView list1;
    private ListView list2;
    private ListView list3;
    private ListView list4;
    private ListView list5;
    private ListView list6;
    private ListView list7;
    private ListView list8;
    private ScrollView sv;

    private DataAdapter mDataAdapter1;
    private DataAdapter mDataAdapter2;
    private DataAdapter mDataAdapter3;
    private DataAdapter mDataAdapter4;
    private DataAdapter mDataAdapter5;
    private DataAdapter mDataAdapter6;
    private DataAdapter mDataAdapter7;
    private CDMADataAdapter mDataAdapter8;

    private ArrayList<String> resultLists = new ArrayList<String>();
    private ArrayList<String> cdmaResultList = new ArrayList<String>();
    private ArrayList<String> saved = new ArrayList<String>();
    private String mcc;
    private String mnc;
    private String sid;
    private String bid;
    private String nid;
    private int rx_power1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.one_key);

        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);
        list3 = (ListView) findViewById(R.id.list3);
        list4 = (ListView) findViewById(R.id.list4);
        list5 = (ListView) findViewById(R.id.list5);
        list6 = (ListView) findViewById(R.id.list6);
        list7 = (ListView) findViewById(R.id.list7);
        list8 = (ListView) findViewById(R.id.list8);

        findViewById(R.id.progress).setVisibility(View.GONE);
        findViewById(R.id.startTime_layout).setVisibility(View.GONE);
        findViewById(R.id.endTime_layout).setVisibility(View.GONE);
        sv = (ScrollView) findViewById(R.id.sv);

        mDataAdapter1 = new DataAdapter(this);
        list1.setAdapter(mDataAdapter1);

        mDataAdapter2 = new DataAdapter(this);
        list2.setAdapter(mDataAdapter2);

        mDataAdapter3 = new DataAdapter(this);
        list3.setAdapter(mDataAdapter3);

        mDataAdapter4 = new DataAdapter(this);
        list4.setAdapter(mDataAdapter4);

        mDataAdapter5 = new DataAdapter(this);
        list5.setAdapter(mDataAdapter5);

        mDataAdapter6 = new DataAdapter(this);
        list6.setAdapter(mDataAdapter6);

        mDataAdapter7 = new DataAdapter(this);
        list7.setAdapter(mDataAdapter7);

        mDataAdapter8 = new CDMADataAdapter(this);
        list8.setAdapter(mDataAdapter8);

        showList();
    }

    private void showList() {
        List<String> data = getIntent().getStringArrayListExtra("result");
        if(data == null){

            return;
        }
        for (String str : data) {
            if (str.startsWith("+VLOCINFO")) {
                String[] info1 = str.split(",");
                mcc = info1[1];
                mnc = info1[2];
                sid = info1[3];
                nid = info1[4];
                bid = info1[5];
                Log.i("gejun", "mcc: " + mcc + ", mnc: " + mnc + ", sid: " + sid + ", nid: " + nid + ", bid: " + bid);
            } else if (str.startsWith("+ECENGINFO:\"1xRTT_Serving_Neighbr_Set_Info\"")) {
                cdmaResultList.add(str);
            } else if (str.startsWith("+ECENGINFO:\"1xRTT_Radio_Info\"")) {
                rx_power1 = Integer.parseInt(str.split(",")[4]);
                cdmaResultList.add(str);
            } else if (!str.startsWith("+ECENGINFO:\"1xRTT_Info\"")) {
                resultLists.add(str);
            }
        }
        sv.scrollTo(0, 0);

        String tag = "";
        for (int i = 0; i < resultLists.size(); i++) {
            List<GsmResult> resultList = new ArrayList<GsmResult>();
            GsmResult row1 = new GsmResult();

            String currentCell = resultLists.get(i);
            Log.i("gejun", "currentCell" + currentCell);
            String[] cellArrays = currentCell.split(",");
            String g = cellArrays[1];
            String mnc = cellArrays[5];
            String array1 = cellArrays[0].substring(cellArrays[0].length() - 1);
            int count = Integer.parseInt(array1);
            if (g.equals("0")) {
                if (mnc.equals("1")) {
                    tag = CU_GSM;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cu_gsm));
                } else if (mnc.equals("0")) {
                    tag = CMCC_GSM;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cmcc_gsm));
                }
            } else if (g.equals("2")) {
                if (mnc.equals("1")) {
                    tag = CU_3G;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.wcdma));
                } else if (mnc.equals("0")) {
                    tag = CMCC_3G;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.tdscdma));
                }
            } else if (g.equals("7")) {
                if (mnc.equals("1")) {
                    tag = CU_4G;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cu_lte));
                } else if (mnc.equals("0")) {
                    tag = CMCC_4G;
                    if (saved.contains(tag)) continue;
                    saved.add(tag);
                    row1.setLac(getResources().getString(R.string.cmcc_lte));
                } else if (mnc.equals("11")) {
                    tag = TELECOM_4G;
                    if (saved.contains(tag)) continue;
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

            for (int j = 0; j < count; j++) {
                GsmResult item = new GsmResult();
                item.setAct(cellArrays[j * 13 + 1]);
                item.setCellId(cellArrays[j * 13 + 2]);
                item.setLac(cellArrays[j * 13 + 3]);
                item.setMcc(cellArrays[j * 13 + 4]);
                item.setMnc(cellArrays[j * 13 + 5]);
                item.setPsc_or_pci(cellArrays[j * 13 + 6]);
                item.setSig1(cellArrays[j * 13 + 7]);
                item.setSig2(cellArrays[j * 13 + 8]);
                item.setSig1_in_dbm(cellArrays[j * 13 + 9]);
                item.setSig2_in_dbm(cellArrays[j * 13 + 10]);
                resultList.add(item);
            }

            if (tag.equals(CU_GSM)) {
                mDataAdapter1.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list1);
                mDataAdapter1.notifyDataSetChanged();
            }
            if (tag.equals(CU_3G)) {
                mDataAdapter2.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list2);
                mDataAdapter2.notifyDataSetChanged();
            }
            if (tag.equals(CU_4G)) {
                mDataAdapter3.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list3);
                mDataAdapter3.notifyDataSetChanged();
            }
            if (tag.equals(CMCC_GSM)) {
                mDataAdapter4.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list4);
                mDataAdapter4.notifyDataSetChanged();
            }
            if (tag.equals(CMCC_3G)) {
                mDataAdapter5.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list5);
                mDataAdapter5.notifyDataSetChanged();
            }
            if (tag.equals(CMCC_4G)) {
                mDataAdapter6.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list6);
                mDataAdapter6.notifyDataSetChanged();
            }

            if (tag.equals(TELECOM_4G)) {
                mDataAdapter7.setData(resultList);
                Util.setListViewHeightBasedOnChildren(list7);
                mDataAdapter7.notifyDataSetChanged();
            }
        }

        if (cdmaResultList != null && cdmaResultList.size() > 0) {
            String[] info2 = cdmaResultList.get(cdmaResultList.size() - 1).split(",");
            int cand_set_count = Integer.parseInt(info2[5]);
            Log.i("gejun", "cand_set_count: " + cand_set_count);
            int cellcount = Integer.parseInt(info2[6 + cand_set_count * 3]) + 1;
            Log.i("gejun", "cdma cell count: " + cellcount);
            ArrayList<CdmaResult> result8 = new ArrayList<CdmaResult>();
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

            for (int i = 0; i < cellcount; i++) {
                CdmaResult item = new CdmaResult();
                if (i == 0) {
                    item.setSid(sid);
                    item.setNid(nid);
                    item.setBid(bid);
                    item.setPn(info2[2]);
                    item.setRx("" + (rx_power1 + Integer.parseInt(info2[3]) / -2));
                } else {
                    item.setSid("--");
                    item.setNid("--");
                    item.setBid("--");
                    item.setPn(info2[6 + cand_set_count * 3 + 1 + (i - 1) * 3]);
                    int rx = Integer.parseInt(info2[6 + cand_set_count * 3 + 1 + ((i - 1) * 3) + 1]);
                    item.setRx("" + (rx_power1 + rx / -2));
                }
                result8.add(item);
            }
            Log.e("gejun", "[ShowActivity][showList] result8:" +result8.size());
            mDataAdapter8.setData(result8);
            Util.setListViewHeightBasedOnChildren(list8);
            mDataAdapter8.notifyDataSetChanged();
        }
    }
}
