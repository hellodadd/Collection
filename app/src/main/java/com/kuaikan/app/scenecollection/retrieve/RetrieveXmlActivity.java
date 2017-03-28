package com.kuaikan.app.scenecollection.retrieve;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kuaikan.app.scenecollection.adapter.CDMADataAdapter;
import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.bean.GsmResult;
import com.kuaikan.app.scenecollection.R;
import com.kuaikan.app.scenecollection.bean.Result;
import com.kuaikan.app.scenecollection.util.Util;
import com.kuaikan.app.scenecollection.parse.SaxXmlParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gejun on 2017/1/5.
 * parse xml
 * deal list<Result>
 * showList
 */

public class RetrieveXmlActivity extends Activity {

    private InputStream is;
    private List<Result> origin;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_key_xml);

        list1 = (ListView) findViewById(R.id.list1);
        list2 = (ListView) findViewById(R.id.list2);
        list3 = (ListView) findViewById(R.id.list3);
        list4 = (ListView) findViewById(R.id.list4);
        list5 = (ListView) findViewById(R.id.list5);
        list6 = (ListView) findViewById(R.id.list6);
        list7 = (ListView) findViewById(R.id.list7);
        list8 = (ListView) findViewById(R.id.list8);

        //findViewById(R.id.progress).setVisibility(View.GONE);
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

    }

    private boolean lt2g = false;
    private boolean lt3g = false;
    private boolean lt4g = false;
    private boolean yd2g = false;
    private boolean yd3g = false;
    private boolean yd4g = false;
    private boolean dx4g = false;
    private boolean cdma = false;

    private List<GsmResult> arraylist1;
    private List<GsmResult> arraylist2;
    private List<GsmResult> arraylist3;
    private List<GsmResult> arraylist4;
    private List<GsmResult> arraylist5;
    private List<GsmResult> arraylist6;
    private List<GsmResult> arraylist7;
    private List<CdmaResult> arraylist8;

    @Override
    protected void onResume() {
        super.onResume();
        if(!lt2g && !lt3g && !lt4g && !yd2g && !yd3g && !yd4g && !dx4g && !cdma) {
            String filePath = getIntent().getStringExtra("xml");
            Log.i("gejun","parse xml : " + filePath);
//            String filePath = "/storage/sdcard0/attach_2a6fd8d6565847db805d02990bd5b814.xml";
            try {
                is = new BufferedInputStream(new FileInputStream(new File(filePath)));
                origin = new SaxXmlParser().parse(is);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(origin != null) {
                for (Result item : origin) {
                    String mnc = item.getMnc();
                    String rat = item.getRat();

                    int currentTag = getCellTag(rat, mnc);
                    if (currentTag == Util.TYPE_CMCC_GSM) {
                        if (!yd2g) {
                            arraylist1 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist1);
                            yd2g = true;
                        }
                        addItem(arraylist1, item);
                    } else if (currentTag == Util.TYPE_CMCC_TDSCDMA) {
                        if (!yd3g) {
                            arraylist2 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist2);
                            yd3g = true;
                        }
                        addItem(arraylist2, item);
                    } else if (currentTag == Util.TYPE_CMCC_LTE) {
                        if (!yd4g) {
                            arraylist3 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist3);
                            yd4g = true;
                        }
                        addItem(arraylist3, item);
                    } else if (currentTag == Util.TYPE_CU_GSM) {
                        if (!lt2g) {
                            arraylist4 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist4);
                            lt2g = true;
                        }
                        addItem(arraylist4, item);
                    } else if (currentTag == Util.TYPE_CU_WCDMA) {
                        if (!lt3g) {
                            arraylist5 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist5);
                            lt3g = true;
                        }
                        addItem(arraylist5, item);
                    } else if (currentTag == Util.TYPE_CU_LTE) {
                        if (!lt4g) {
                            arraylist6 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist6);
                            lt4g = true;
                        }
                        addItem(arraylist6, item);
                    } else if (currentTag == Util.TYPE_TELECOM_LTE) {
                        if (!dx4g) {
                            arraylist7 = new ArrayList<GsmResult>();
                            initFirst2Row(currentTag, arraylist7);
                            dx4g = true;
                        }
                        addItem(arraylist7, item);
                    } else if (currentTag == Util.TYPE_CDMA) {
                        if (!cdma) {
                            cdma = true;
                            arraylist8 = new ArrayList<CdmaResult>();
                            CdmaResult r1 = new CdmaResult();
                            r1.setSid(getString(R.string.cdma));
                            arraylist8.add(r1);

                            CdmaResult r2 = new CdmaResult();
                            r2.setSid(getString(R.string.sid));
                            r2.setNid(getString(R.string.nid));
                            r2.setBid(getString(R.string.bid));
                            r2.setPn(getString(R.string.pn));
                            r2.setRx(getString(R.string.rx));
                            arraylist8.add(r2);
                        }

                        CdmaResult c = new CdmaResult();
                        c.setSid(((CdmaResult) item).getSid());
                        c.setBid(((CdmaResult) item).getBid());
                        c.setNid(((CdmaResult) item).getNid());
                        c.setPn(((CdmaResult) item).getPn());
                        c.setRx(((CdmaResult) item).getRx());
                        arraylist8.add(c);
                    }

                    if (origin.indexOf(item) == origin.size() - 1) {
                        notify1();
                    }
                }
            }
        }
    }

    private void addItem(List<GsmResult> list, Result item){
        GsmResult gsm = new GsmResult();
        String lac = ((GsmResult)item).getLac();
        String cid = ((GsmResult)item).getCellId();
        String rsi = ((GsmResult)item).getRssi();
        Log.i("gejun","lac = "+lac+", cid = "+ cid +", rssi = " + rsi);
        gsm.setLac(lac);
        gsm.setCellId(cid);
        gsm.setRssi(rsi);
        list.add(gsm);
    }

    public void initFirst2Row(int tag, List<GsmResult> list){
        GsmResult r1 = new GsmResult();
        if(tag == Util.TYPE_CMCC_GSM){
            r1.setLac(getString(R.string.cmcc_gsm));
        } else if(tag == Util.TYPE_CMCC_TDSCDMA){
            r1.setLac(getString(R.string.tdscdma));
        } else if(tag == Util.TYPE_CMCC_LTE){
            r1.setLac(getString(R.string.cmcc_lte));
        } else if(tag == Util.TYPE_CU_GSM){
            r1.setLac(getString(R.string.cu_gsm));
        } else if(tag == Util.TYPE_CU_WCDMA){
            r1.setLac(getString(R.string.wcdma));
        } else if(tag == Util.TYPE_CU_LTE){
            r1.setLac(getString(R.string.cu_lte));
        } else if(tag == Util.TYPE_TELECOM_LTE){
            r1.setLac(getString(R.string.telecom_lte));
        }
        list.add(r1);

        GsmResult row2 = new GsmResult();
        row2.setLac(getResources().getString(R.string.lac));
        row2.setCellId(getResources().getString(R.string.cell_id));
        row2.setRssi(getResources().getString(R.string.rssi));
        list.add(row2);
    }

    public void notify1(){
        mDataAdapter1.setData(arraylist1);
        Util.setListViewHeightBasedOnChildren(list1);
        mDataAdapter1.notifyDataSetChanged();

        mDataAdapter2.setData(arraylist2);
        Util.setListViewHeightBasedOnChildren(list2);
        mDataAdapter2.notifyDataSetChanged();

        mDataAdapter3.setData(arraylist3);
        Util.setListViewHeightBasedOnChildren(list3);
        mDataAdapter3.notifyDataSetChanged();

        mDataAdapter4.setData(arraylist4);
        Util.setListViewHeightBasedOnChildren(list4);
        mDataAdapter4.notifyDataSetChanged();

        mDataAdapter5.setData(arraylist5);
        Util.setListViewHeightBasedOnChildren(list5);
        mDataAdapter5.notifyDataSetChanged();

        mDataAdapter6.setData(arraylist6);
        Util.setListViewHeightBasedOnChildren(list6);
        mDataAdapter6.notifyDataSetChanged();

        mDataAdapter7.setData(arraylist7);
        Util.setListViewHeightBasedOnChildren(list7);
        mDataAdapter7.notifyDataSetChanged();

        mDataAdapter8.setData(arraylist8);
        Util.setListViewHeightBasedOnChildren(list8);
        mDataAdapter8.notifyDataSetChanged();
    }

    private int getCellTag(String rat, String mnc){
        int tag = -1;
        if (rat.equals(Util.G2) && mnc.equals("1")) {
            tag = Util.TYPE_CU_GSM;
        } else if (rat.equals(Util.G3) && mnc.equals("1")) {
            tag = Util.TYPE_CU_WCDMA;
        } else if (rat.equals(Util.G4) && mnc.equals("1")) {
            tag = Util.TYPE_CU_LTE;
        } else if (rat.equals(Util.G2) && mnc.equals("0")) {
            tag = Util.TYPE_CMCC_GSM;
        }else if (rat.equals(Util.G3) && mnc.equals("0")) {
            tag = Util.TYPE_CMCC_TDSCDMA;
        }else if (rat.equals(Util.G4) && mnc.equals("0")) {
            tag = Util.TYPE_CMCC_LTE;
        }else if (rat.equals(Util.G4) && mnc.equals("11")) {
            tag = Util.TYPE_TELECOM_LTE;
        } else if (rat.equals("-1")) {
            tag = Util.TYPE_CDMA;
        }
        return tag;
    }

    public class DataAdapter extends BaseAdapter {
        private List<GsmResult> result;

        public void setData(List<GsmResult> list) {
            result = list;
        }

        private Context context;

        public DataAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return result == null ? 0 : result.size();
        }

        @Override
        public Object getItem(int position) {
            return result.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.gsm_item, null);
            TextView lac = (TextView) view.findViewById(R.id.lac);
            TextView cellId = (TextView) view.findViewById(R.id.cell_id);
            TextView rssi = (TextView) view.findViewById(R.id.rssi);
            TextView bcch = (TextView) view.findViewById(R.id.bcch);
            bcch.setVisibility(View.GONE);
            TextView bsic = (TextView) view.findViewById(R.id.bsic);
            bsic.setVisibility(View.GONE);
            if (position == 0) {
                view.setBackgroundResource(R.drawable.top_bg);
                cellId.setVisibility(View.GONE);
                rssi.setVisibility(View.GONE);
            } else if (position == getCount() - 1) {
                view.setBackgroundResource(R.drawable.bottom_bg);
                cellId.setVisibility(View.VISIBLE);
                rssi.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundResource(R.drawable.mid_bg);
                cellId.setVisibility(View.VISIBLE);
                rssi.setVisibility(View.VISIBLE);
            }

            GsmResult resultItem = result.get(position);
            if (position == 0 || position == 1) {
                lac.setText(resultItem.getLac());
                cellId.setText(resultItem.getCellId());
            } else {
                lac.setText(resultItem.getLac());
                cellId.setText(resultItem.getCellId());
            }
            bcch.setText(resultItem.getBcch());
            bsic.setText(resultItem.getBsic());
            rssi.setText(resultItem.getRssi());

            return view;
        }
    }
}
