package com.example.krishna.shop;

//import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Ramya on 14-03-2017.
 */
public class shopkeepers extends AppCompatActivity {
    Button submit,login;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkeepers);
        Toast.makeText(shopkeepers.this, "checking in shopkeepers", Toast.LENGTH_LONG).show();
        submit=(Button)findViewById(R.id.btn_register);
        login=(Button)findViewById(R.id.btn_signup);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"test register bfre",Toast.LENGTH_LONG).show();
                Intent i = new Intent(shopkeepers.this,register.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"test login bfre",Toast.LENGTH_LONG).show();
                Intent i = new Intent(shopkeepers.this,login.class);
                startActivity(i);
            }
        });
    }
}
