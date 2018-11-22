package tw.com.edu.ntue.toybabysitter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String MQTTHOST = "tcp://iot.eclipse.org";
    //private static String USERNAME = "test1";
    //private static String PASSWORD= "123456";
    private static String topic = "tw/com/edu/ntue/gps";
    MqttAndroidClient client;
    MqttConnectOptions options;
    SupportMapFragment mapFragment;
    String latitude="0",longitude="0";
    Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);  原本的座標值是雪梨某處
        // 替換上美麗島站的座標
        Log.i("latitude",latitude);
        Log.i("longitude",longitude);

        LatLng location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));

        mMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能
        marker=mMap.addMarker(new MarkerOptions().position(location).title("目前gps位置"));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true); // 右上角的定位功能；這行會出現紅色底線，不過仍可正常編譯執行

    }

    @Override
    protected void onResume() {
        super.onResume();

        mapFragment.getMapAsync(MapsActivity.this);
        if(!Global.isNetworkAvailable(this)){
            showAlertView(this,getResources().getString(R.string.Please_check_your_network));
        }else{

            initialSetting();
            connect();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(Global.isNetworkAvailable(this))disconnect();

    }
    private void initialSetting(){
        String clientId = MqttClient.generateClientId();
        try {
            client =new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,clientId);
            options = new MqttConnectOptions();
            //options.setUserName(USERNAME);
            //options.setPassword(PASSWORD.toCharArray());
            client.setCallback ( new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //連接丟失後一般會在這裡進行重連
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish後會執行到這裡
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe後得到的消息會執行到這裡面

                    //if(topicName.equals(topicDryerInStr)){}
                    Log.d("MQTTMESSAGE",new String(message.getPayload()));
                    JSONArray array= new JSONArray(new String(message.getPayload()));
                    JSONObject jsonObject=array.getJSONObject(0);
                    if(!latitude.equals(jsonObject.getString("latitude"))||!longitude.equals(jsonObject.getString("longitude"))){
                        latitude=jsonObject.getString("latitude");
                        longitude=jsonObject.getString("longitude");
                        LatLng location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                        if(marker!=null) {
                            marker.remove();
                        }

                        if(mMap!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            marker=mMap.addMarker(new MarkerOptions().position(location).title("目前gps位置"));
                        }
                    }

                }
            } );
        }catch (Exception e){
            e.printStackTrace ();
        }
    }
    public void connect(){
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    showAlertView(MapsActivity.this,getResources().getString(R.string.connected));
                    /*setSubscription();
                    publish();
                    disconnect();*/
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    showAlertView(MapsActivity.this,getResources().getString(R.string.connected_failure));
                    disconnect();
                    connect();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    private void setSubscription(){
        if(client.isConnected()){
            try{
                client.subscribe(topic,0);

            }catch (MqttException e){
                e.printStackTrace();
            }
        }
    }
    public void disconnect(){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Toast.makeText(context,"disconnected!!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //Toast.makeText(context,"could not disconnect",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void showAlertView(Context context,String title){
        LayoutInflater inflater = LayoutInflater.from(context);
        View createView = inflater.inflate(R.layout.dialog_hint, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(createView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.Transparent);
        dialog.show();
        dialog.setCancelable(true);
        TextView txt_title=createView.findViewById(R.id.dialog_title);
        txt_title.setText(title);
        Handler Alerthandler = new Handler();
        Alerthandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);
    }
}
