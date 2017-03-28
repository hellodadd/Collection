package com.kuaikan.app.scenecollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaikan.app.scenecollection.bean.GsmResult;
import com.kuaikan.app.scenecollection.R;

import java.util.List;

/**
 * Created by zhaozehong on 16-12-9.
 */

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
            lac.setText(toEight(resultItem.getLac()));
            cellId.setText(toEight(resultItem.getCellId()));
        }
        bcch.setText(resultItem.getBcch());
        bsic.setText(resultItem.getBsic());
        rssi.setText(resultItem.getSig1());

        return view;
    }

    private String toEight(String shiliu) {
        shiliu = shiliu.substring(1, shiliu.length() - 1);
        return Long.parseLong(shiliu, 16) + "";
    }
}
