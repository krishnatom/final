package com.example.krishna.shop;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class display extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    Double lat, lon;
    final String find_url = "http://nammakadai.000webhostapp.com/find.php";
    LocationManager locationManager;
    String provider, tdomain;
    Location location;
    AlertDialog.Builder alert;
    private GoogleMap mMap;
    Shops shops[];
    BitmapDescriptor bitmapMarker;
    ArrayList<Shops> l=new ArrayList<Shops>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        alert = new AlertDialog.Builder(display.this);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (isNetworkEnabled) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            alert.setTitle("Location Error");
            alert.setMessage("Location cant be determined");
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Bundle bundle=getIntent().getExtras();
        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        if (location != null) {
            onLocationChanged(location);
            alert.setTitle("Location");
            alert.setMessage("Location successfully obtained");
            displayAlert("location");

        } else {
            alert.setTitle("Location Error");
            alert.setMessage("GPS  Error pleasee try again later");
            displayAlert("location_error");
        }


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        alert.setTitle("Shops");
        alert.setMessage("Getting shop infos");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, find_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //Toast.makeText(getApplicationContext(), "Response " + response, Toast.LENGTH_LONG).show();
                                    JSONArray jsonArray = new JSONArray(response);
                                    int length = jsonArray.length();
                                    shops = new Shops[length];
                                    for (int i = 0; i < length; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int id=(Integer.parseInt(jsonObject.getString("id")));
                                            Double tlat=(Double.parseDouble(jsonObject.getString("latitude")));
                                            Double tlon=(Double.parseDouble(jsonObject.getString("longitude")));
                                            String name=(jsonObject.getString("shop"));
                                            String desc=(jsonObject.getString("description"));
                                            String status=(jsonObject.getString("status"));
                                        Boolean stat;
                                        if(status.equals("1"))
                                            stat=true;
                                        else
                                           stat=false;
                                            shops[i]=new Shops(id,name,desc,stat,tlat,tlon);
                                            if (stat) {
                                              bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                            } else {
                                               bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                           }
                                        //Toast.makeText(getApplicationContext()," "+name,Toast.LENGTH_LONG).show();

                                             mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(tlat,tlon))
                                                .anchor(0.5f, 0.5f)
                                                .title(name)
                                                .snippet(desc)
                                                .icon(bitmapMarker))
                                                .setVisible(true);


                                            l.add(shops[i]);
                                            //Toast.makeText(getApplicationContext(), "Shop added to array list", Toast.LENGTH_LONG).show();

                                        /*catch (NullPointerException e){
                                            Toast.makeText(getApplicationContext(), "Exception"+e.toString(), Toast.LENGTH_LONG).show();
                                        }*/

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error in response"+error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("latitude", lat.toString());
                        params.put("longitude", lon.toString());
                        Bundle bundle = getIntent().getExtras();
                        tdomain = bundle.getString("domain");
                        params.put("domain", tdomain);
                        return params;
                    }
                };
                MySingleton.getInstance(display.this).addToRequestQueue(stringRequest);


            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();

        //Toast.makeText(getApplicationContext(),"After volley",Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }

        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        //Toast.makeText(getApplicationContext(),"Lat: "+lat+" Lon:"+lon,Toast.LENGTH_LONG).show();
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

    /*private void createMarker(String name, Double lat, Double lon, Boolean status, String description) {
        if (status) {
            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else {
            bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        Toast.makeText(getApplicationContext()," "+name,Toast.LENGTH_LONG).show();

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .anchor(0.5f, 0.5f)
                        .title(name)
                        .snippet(description)
                        .icon(bitmapMarker))
                .setVisible(true);
    }*/

    private void displayAlert(final String code) {
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (code.equals("location")) {

                } else if (code.equals("location_error")) {
                    //find.setEnabled(true );

                }


            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }



}


