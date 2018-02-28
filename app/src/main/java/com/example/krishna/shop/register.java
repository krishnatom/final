package com.example.krishna.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity implements LocationListener {

    Button submit, loc;
    EditText shop, email, phone, fpwd, spwd;
    Spinner domain;
    AlertDialog.Builder alert;
    //RequestQueue req;
    String register_url = "https://nammakadai.000webhostapp.com/register.php";
    String tshop, temail, tphone, tdomain, pwd, cpwd;
    Double lat = -1.0, lon = -1.0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    LocationManager locationManager;
    String provider;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        shop = (EditText) findViewById(R.id.et_sname);
        email = (EditText) findViewById(R.id.et_email);
        phone = (EditText) findViewById(R.id.et_phone);
        fpwd = (EditText) findViewById(R.id.et_fpwd);
        spwd = (EditText) findViewById(R.id.et_spwd);
        domain = (Spinner) findViewById(R.id.spinner_domain);
        submit = (Button) findViewById(R.id.btn_submit);
        loc = (Button) findViewById(R.id.btn_location);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_domain);
        alert = new AlertDialog.Builder(register.this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.domain, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isGPSEnabled){
            provider = LocationManager.GPS_PROVIDER;
        }
        else if(isNetworkEnabled){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else{
            alert.setTitle("Location Error");
            alert.setMessage("Location cant be determined");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 0, 0, this);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loc.setEnabled(false);



                if (location!=null){
                    onLocationChanged(location);
                    alert.setTitle("Location");
                    alert.setMessage("Location successfully obtained");
                                     displayAlert("location");
                }
                else{

                    alert.setTitle("GPS Error");
                    alert.setMessage("GPS  Error pleasee try again later");

                    displayAlert("location_error");
                }


            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tdomain="others";
                tshop = shop.getText().toString();

                temail = email.getText().toString();
                tphone = phone.getText().toString();
                tdomain = domain.getSelectedItem().toString();
                pwd = fpwd.getText().toString();
                cpwd = spwd.getText().toString();

                if (tshop.equals("") || temail.equals("") || tphone.equals("") || pwd.equals("") || cpwd.equals("")|| lat==-1.0 || lon==-1.0  ) {
                     alert.setTitle("Something went wrong");
                    alert.setMessage("Please fill in all details");

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

                                            System.out.println(response);
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");

                                            alert.setTitle("Server Response");
                                            alert.setMessage("Message From Server:" + message);
                                            displayAlert(code);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error in Response", Toast.LENGTH_LONG).show();
                                error.printStackTrace();

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("shop", tshop);
                                params.put("email", temail);
                                params.put("phone", tphone);
                                params.put("password", pwd);
                                params.put("latitude", lat.toString());
                                params.put("longitude", lon.toString());
                                params.put("domain", tdomain);


                                return params;
                            }
                        };
                        MySingleton.getInstance(register.this).addToRequestQueue(stringRequest);
                    }


                }

            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void displayAlert(final String code) {
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    fpwd.setText("");
                    spwd.setText("");

                } else if (code.equals("reg_success")) {
                    Toast.makeText(getApplicationContext(), "Code is " + code, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(register.this, login.class);
                    startActivity(i);
                    //finish();
                } else if (code.equals("reg_failed")) {
                    shop.setText("");
                    email.setText("");
                    phone.setText("");
                    fpwd.setText("");
                    spwd.setText("");
                    lat =null;
                    lon = null;
                } else if (code.equals("fatal_error")) {
                    shop.setText("");
                    email.setText("");
                    phone.setText("");
                    fpwd.setText("");
                    spwd.setText("");
                    lat = null;
                    lon = null;
                }
                else if(code.equals("location")){

                }
                else if(code.equals("location_error")){
                    loc.setEnabled(true);
                }


            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();
        //Toast.makeText(getApplicationContext(), "Lat:" + lat + " long:" + lon, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}







