package com.kuaikan.app.scenecollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

import com.kuaikan.app.scenecollection.util.Util;

public class MainActivity extends Activity implements OnClickListener{

    private Button dynamic;
    private Button undynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type);

        dynamic = (Button)findViewById(R.id.dynamic);
        dynamic.setOnClickListener(this);
        undynamic = (Button)findViewById(R.id.undynamic);
        undynamic.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        Intent intent = new Intent(this, OpActivity.class);
        switch(id){
            case R.id.dynamic:
                intent.putExtra(Util.COLLECT_TYPE, true);
                break;
            case R.id.undynamic:
                intent.putExtra(Util.COLLECT_TYPE, false);
                break;
        }
        startActivity(intent);
    }
}
