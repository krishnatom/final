package com.example.krishna.shop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Status extends AppCompatActivity {

    TextView out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        out=(TextView)findViewById(R.id.tv_shopname);
        Bundle bundle=getIntent().getExtras();
        out.setText(bundle.getString("shop-"+bundle.getString("domain")));
    }
}
