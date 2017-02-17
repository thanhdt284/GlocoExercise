package com.dpanic.glocoexercise;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dpanic.glocoexercise.model.SampleData;
import com.dpanic.glocoexercise.model.User;
import com.dpanic.glocoexercise.utils.Common;
import com.dpanic.glocoexercise.utils.LocationUtils;
import com.dpanic.glocoexercise.utils.NetworkUtils;
import com.dpanic.glocoexercise.utils.PermissionUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 101;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_list_data)
    RecyclerView rvListData;

    private Context mContext;
    private ArrayList<User> mDataList = new ArrayList<>();
    private DataAdapter mDataAdapter;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private boolean isPerformingClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;

        initUI();

        retrieveData();

        acquirePermission();
    }

    private void acquirePermission() {
        if (PermissionUtils.isHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (!LocationUtils.isGpsEnabled(this)) {
                displayLocationSettingsRequest(this);
            }
        } else {
            PermissionUtils.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void retrieveData() {
        Realm realm = Realm.getDefaultInstance();

        compositeSubscription.add(realm.where(User.class).findAllAsync().asObservable().subscribe(new Action1<RealmResults<User>>() {
            @Override
            public void call(RealmResults<User> contacts) {
                mDataList.clear();
                mDataList.addAll(contacts);
                if (mDataAdapter != null) {
                    mDataAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private void initUI() {
        setSupportActionBar(toolbar);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mDataAdapter = new DataAdapter(this, mDataList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                mLayoutManager.getOrientation());
        rvListData.addItemDecoration(dividerItemDecoration);
        rvListData.setLayoutManager(new LinearLayoutManager(this));
        rvListData.setAdapter(mDataAdapter);
    }

    @OnClick({R.id.btn_send_to_url1, R.id.btn_send_to_url2})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_to_url1:
                if (NetworkUtils.isConnectedWifi(mContext)) {
                    if (LocationUtils.isGpsEnabled(this)) {
                        sendRequestToUrl1();
                    } else {
                        isPerformingClick = true;
                        displayLocationSettingsRequest(this);
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.string_required_wifi), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_send_to_url2:
                if (NetworkUtils.isConnectedMobile(mContext)) {
                    sendRequestToUrl2();
                } else {
                    Toast.makeText(mContext, getString(R.string.string_required_cellular), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.d(permissions[0] + " has been granted");
            if (!LocationUtils.isGpsEnabled(this)) {
                displayLocationSettingsRequest(this);
            }
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Timber.i("All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Timber.i("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Timber.i("PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Timber.i("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (isPerformingClick) {
                            isPerformingClick = false;
                            sendRequestToUrl1();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent serviceIntent = new Intent(mContext, RegionMonitoringService.class);
        stopService(serviceIntent);

        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    private void sendRequestToUrl1() {
        String url = "https://jsonplaceholder.typicode.com/users";

        final HashMap<String, String> postParams = new HashMap<>();
        postParams.put(Common.LATITUDE, "21.0060154");
        postParams.put(Common.LONGITUDE, "105.8021994");
        postParams.put(Common.RADIUS, "10");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParams),
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Timber.d(response.toString());
                        double latitude = 0;
                        double longitude = 0;
                        int radius = 0;
                        try {
                            JSONObject root = new JSONObject(response.toString());
                            latitude =  root.getDouble(Common.LATITUDE);
                            longitude =  root.getDouble(Common.LONGITUDE);
                            radius =  root.getInt(Common.RADIUS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent serviceIntent = new Intent(mContext, RegionMonitoringService.class);
                        serviceIntent.putExtra(Common.LATITUDE, latitude);
                        serviceIntent.putExtra(Common.LONGITUDE, longitude);
                        serviceIntent.putExtra(Common.RADIUS, radius);
                        startService(serviceIntent);
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }

    private void sendRequestToUrl2() {
        String url = "https://jsonplaceholder.typicode.com/users";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonRequest = null;
        try {
            jsonRequest = new JsonArrayRequest(Request.Method.POST,
                    url, new JSONArray(SampleData.jsonArrayData),
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.createAllFromJson(User.class, response);
                            realm.commitTransaction();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonRequest != null) {
            requestQueue.add(jsonRequest);
        }
    }
}
