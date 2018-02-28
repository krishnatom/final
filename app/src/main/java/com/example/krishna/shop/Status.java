package com.example.krishna.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Status extends AppCompatActivity {

    TextView out;
    EditText desc;
    Button update,open,close;
    Boolean s;
    AlertDialog.Builder alert;
    Bundle bundle;
    final String status_url="https://nammakadai.000webhostapp.com/update.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        out=(TextView)findViewById(R.id.tv_shopname);
         bundle=getIntent().getExtras();
        desc=(EditText)findViewById(R.id.et_desc);
        alert=new AlertDialog.Builder(Status.this);
        out.setText(" " + bundle.getString("shop") + "-" + bundle.getString("domain"));
        open=(Button)findViewById(R.id.btn_open);
        close=(Button)findViewById(R.id.btn_closed);

        Boolean oldStatus;
        if(bundle.getString("status").equals("1")){
            oldStatus=true;
        }
        else{
            oldStatus=false;
        }
        //Toast.makeText(getApplicationContext(),"Status:"+oldStatus,Toast.LENGTH_LONG).show();
        if(oldStatus){
            open.setEnabled(false);
            close.setEnabled(true);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close.setBackgroundColor(Color.RED);
                    close.setEnabled(false);
                    s = false;
                }
            });
        }
        else {
            open.setEnabled(true);
            close.setEnabled(false);

            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open.setBackgroundColor(Color.GREEN);
                    open.setEnabled(false);
                    s=true;
                }
            });

        }


        update=(Button)findViewById(R.id.bt_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = desc.getText().toString();
                if (description.equals("")) {
                    alert.setTitle("No Data");
                    alert.setMessage("Description must be filled");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, status_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Toast.makeText(getApplicationContext(), " " + response, Toast.LENGTH_LONG).show();
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        final String code = jsonObject.getString("code");
                                        if (code.equals("failure") || (code.equals("fatal_error"))) {
                                            alert.setTitle("Update Failed");
                                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (code.equals("fatal_error") || code.equals("failure")) {
                                                        Intent i = new Intent(Status.this, login.class);
                                                        startActivity(i);
                                                    }

                                                }
                                            });
                                            AlertDialog alertDialog = alert.create();
                                            alertDialog.show();

                                        } else {
                                            alert.setTitle("Success");
                                            alert.setMessage("Status Updated Successfully ");
                                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent i = new Intent(Status.this, MainActivity.class);
                                                    startActivity(i);
                                                }
                                            });
                                            AlertDialog alertDialog = alert.create();
                                            alertDialog.show();

                                        }


                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error in Response", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("status", s.toString());
                            params.put("id", bundle.getString("id"));
                            params.put("description", description);
                            params.put("domain", bundle.getString("domain"));
                            return params;
                        }

                    };
                    MySingleton.getInstance(Status.this).addToRequestQueue(stringRequest);
                }

            }
        });

    }

}
