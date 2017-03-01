package com.kuaikan.app.scenecollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaikan.app.scenecollection.bean.CdmaResult;
import com.kuaikan.app.scenecollection.R;

import java.util.List;

/**
 * Created by zhaozehong on 16-12-9.
 */

public class CDMADataAdapter extends BaseAdapter {

    private Context context;
    private List<CdmaResult> result;

    public void setData(List<CdmaResult> list) {
        result = list;
    }

    public CDMADataAdapter(Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.cdma_item, null);
        TextView bid = (TextView) view.findViewById(R.id.bid);
        TextView nid = (TextView) view.findViewById(R.id.nid);
        TextView sid = (TextView) view.findViewById(R.id.sid);
        TextView pn = (TextView) view.findViewById(R.id.pn);
        TextView rx = (TextView) view.findViewById(R.id.rx);
        bid.setVisibility(View.VISIBLE);
        nid.setVisibility(View.VISIBLE);
        pn.setVisibility(View.VISIBLE);
        rx.setVisibility(View.VISIBLE);

        CdmaResult resultItem = result.get(position);
        bid.setText(resultItem.getBid());
        nid.setText(resultItem.getNid());
        sid.setText(resultItem.getSid());
        pn.setText(resultItem.getPn());
        rx.setText(resultItem.getRx());

        if (position == 0) {
            view.setBackgroundResource(R.drawable.top_bg);
            bid.setVisibility(View.GONE);
            nid.setVisibility(View.GONE);
            pn.setVisibility(View.GONE);
            rx.setVisibility(View.GONE);
        } else if (position == getCount() - 1) {
            view.setBackgroundResource(R.drawable.bottom_bg);
        } else {
            view.setBackgroundResource(R.drawable.mid_bg);
        }

        return view;
    }

}
