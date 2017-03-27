package com.example.krishna.shop;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    Button submit,loc;
    EditText shop,email,phone,fpwd,spwd;
    Spinner domain;
    AlertDialog.Builder alert;
    //RequestQueue req;
    String register_url="http://192.168.1.3/android/register.php";
    String tshop,temail,tphone,tdomain,pwd,cpwd,lat,lon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        shop=(EditText)findViewById(R.id.et_sname);
        email=(EditText)findViewById(R.id.et_email);
        phone=(EditText)findViewById(R.id.et_phone);
        fpwd=(EditText)findViewById(R.id.et_fpwd);
        spwd=(EditText)findViewById(R.id.et_spwd);
        domain=(Spinner)findViewById(R.id.spinner_domain);
        submit=(Button)findViewById(R.id.btn_submit);
        loc=(Button)findViewById(R.id.btn_location);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_domain);
        alert=new AlertDialog.Builder(register.this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.domain, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat="12.0";
                lon="12.0";
                Toast.makeText(getApplicationContext(),"lat:"+lat+"long:"+lon,Toast.LENGTH_LONG).show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tdomain="others";
                tshop=shop.getText().toString();
                //Toast.makeText(getApplicationContext(),"\t"+tshop,Toast.LENGTH_LONG).show();
                temail=email.getText().toString();
                tphone=phone.getText().toString();
                tdomain=domain.getSelectedItem().toString();
                pwd=fpwd.getText().toString();
                cpwd=spwd.getText().toString();

                if (tshop.equals("") || temail.equals("") || tphone.equals("") || pwd.equals("") || cpwd.equals("") || lat.equals("") || lon.equals("") ) {
                    alert.setTitle("Something went wrong");
                    alert.setMessage("Please fill in all details");
//                    Toast.makeText(getApplicationContext(), "\t" + tshop + "\t" + temail + "\t" + tphone + "\t"+pwd+"\t"+cpwd+"\t"+lat+"\t"+lon+"\t"+tdomain,Toast.LENGTH_LONG).show();
                    displayAlert("input_error");

                } else {
                    if (!pwd.equals(cpwd)) {
                        alert.setTitle("Password Error");
                        alert.setMessage("Passwords Doesn't Match");
                        displayAlert("input_error");
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, register_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            //Toast.makeText(getApplicationContext()," "+response,Toast.LENGTH_LONG).show();
                                            System.out.println(response);
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");

                                            alert.setTitle("Server Response");
                                            alert.setMessage("Message From Server:"+message);
                                            displayAlert(code);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"Error in Response",Toast.LENGTH_LONG).show();
                                error.printStackTrace();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("shop",tshop);
                                params.put("email",temail);
                                params.put("phone",tphone);
                                params.put("password",pwd);
                                params.put("latitude",lat);
                                params.put("longitude",lon);
                                params.put("domain",tdomain);


                                return params;
                            }
                        };
                        MySingleton.getInstance(register.this).addToRequestQueue(stringRequest);
                    }


                }

            }
        });


    }

public  void displayAlert(final  String code){
    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(code.equals("input_error")){
                fpwd.setText("");
                spwd.setText("");

            }
            else if(code.equals("reg_success")){
                Toast.makeText(getApplicationContext(),"Code is " +code,Toast.LENGTH_LONG).show();
                Intent i=new Intent(register.this,login.class);
                startActivity(i);
                //finish();
            }
            else if (code.equals("reg_failed")){
                shop.setText("");
                email.setText("");
                phone.setText("");
                fpwd.setText("");
                spwd.setText("");
                lat="";
                lon="";
            }
            else if (code.equals("fatal_error")){
                shop.setText("");
                email.setText("");
                phone.setText("");
                fpwd.setText("");
                spwd.setText("");
                lat="";
                lon="";
            }

        }
    });
    AlertDialog alertDialog=alert.create();
    alertDialog.show();
}
}







