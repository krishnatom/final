package com.example.krishna.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class login extends Activity {

EditText id,pwd;
    Button login;
    AlertDialog.Builder alert;
    final String login_url="http://192.168.1.3/android/login.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.domain, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_login);
        spinner.setAdapter(adapter);

        id=(EditText)findViewById(R.id.et_login);
        pwd=(EditText)findViewById(R.id.et_pwd);
        login=(Button)findViewById(R.id.bt_login);
login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final String user=id.getText().toString();
        final String pass=pwd.getText().toString();
        final String tdomain=spinner.getSelectedItem().toString();
        if(user.equals("")||pass.equals("")){
            alert.setTitle("Input Missing");
            alert.setMessage("Please Enter Login id or password");
            displayAlert("input_error");

        }
        else{

            StringRequest stringRequest= new StringRequest(Request.Method.POST, login_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray=new JSONArray(response);
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                String code=jsonObject.getString("code");
                                if(code.equals("login_failed")){
                                    alert.setTitle("Access Denied");
                                    displayAlert(jsonObject.getString("message"));
                                }
                                else{
                                    Intent i=new Intent(login.this,Status.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("shop",jsonObject.getString("shop"));
                                    bundle.putString("domain",jsonObject.getString("domain"));
                                    i.putExtras(bundle);
                                    startActivity(i);
                                }


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
                    Map<String,String> params=new HashMap<String, String>();
                    params.put("id",user);
                    params.put("password",pass);
                    params.put("domain",tdomain);
                    return params;
                }

            };
            MySingleton.getInstance(login.this).addToRequestQueue(stringRequest);


        }
    }
});
        }
    public  void displayAlert(final  String code){
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(code.equals("input_error")){
                     id.setText("");
                    pwd.setText("");

                }

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }
}