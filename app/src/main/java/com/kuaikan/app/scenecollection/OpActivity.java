package com.kuaikan.app.scenecollection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

import com.kuaikan.app.scenecollection.util.Util;

public class OpActivity extends Activity implements OnClickListener{

    private Button cu;
    private Button cmcc;
    private Button telecom;
    private Button gps;
    private Button oneKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cu = (Button)findViewById(R.id.cu);
        cu.setOnClickListener(this);
        cmcc = (Button)findViewById(R.id.cmcc);
        cmcc.setOnClickListener(this);
        telecom = (Button)findViewById(R.id.telecom);
        telecom.setOnClickListener(this);
        gps = (Button)findViewById(R.id.gps);
        gps.setOnClickListener(this);
        oneKey = (Button)findViewById(R.id.one_key);
        oneKey.setOnClickListener(this);

        boolean type = getIntent().getBooleanExtra(Util.COLLECT_TYPE, false);
        gps.setVisibility(type ? View.GONE : View.VISIBLE);
        oneKey.setVisibility(type ? View.GONE : View.VISIBLE);

        setTitle(type ? R.string.dynamic : R.string.undynamic);
    }

    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent intent;
        switch(id){
            case R.id.cu:
                intent = new Intent(this, CuActivity.class);
                startActivity(intent);
                break;
            case R.id.cmcc:
                intent = new Intent(this, CmccActivity.class);
                startActivity(intent);
                break;
            case R.id.telecom:
                intent = new Intent(this, TelecomActivity.class);
                startActivity(intent);
                break;
            case R.id.gps:
                intent = new Intent(this, GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.one_key:
                if(Util.NON_SIM){
                    intent = new Intent(this, NonSimOneKeyActivity.class);
                } else {
                    intent = new Intent(this, OneKeyActivityB.class);
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.atCOPS(mHandler.obtainMessage(EVENT_COPS));
    }

    private static final int EVENT_COPS = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_COPS:{
                    Util.showOriginResult(msg, "getCOPS");
                    break;
                }
            }
        }
    };
}
