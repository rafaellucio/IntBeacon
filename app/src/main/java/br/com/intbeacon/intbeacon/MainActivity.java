package br.com.intbeacon.intbeacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class MainActivity<T> extends FragmentActivity implements BeaconConsumer, OnMapReadyCallback {

    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private GoogleMap map;

    private static final LatLng FACULDADE = new LatLng(-23.599787, -46.676977);
    private static final String UUID_BEACON_01 = "0x699ebc80e1f311e39a0f";
    private static final String UUID_BEACON_02 = "003e8c80ea014ebbb88878da19df9e55";
    private static final String UUID_BEACON_03 = "0x699ebc80e1f311e39a0f";


    private static final long TIMEOUT = 10 * 1000;
    Class<T> clazz;

    Handler handler = new Handler();

    Runnable runnableFirstBeacon = new Runnable() {
        public void run() {
            dispatchFirstBeacon();
        }
    };

    Runnable runnableSecondBeacon = new Runnable() {
        public void run() {
            dispatchSecondBeacon();
        }
    };

    Runnable runnableThirdBeacon = new Runnable() {
        public void run() {
            dispatchThirdBeacon();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler.postDelayed(runnableFirstBeacon, TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                if (beaconManager.getBeaconParsers().get(0).getIdentifier() == UUID_BEACON_01) {
                    clazz = (Class<T>) FirstBeaconActivity.class;
                    notification(clazz, 001, "Siga em Frente por 10 segundos");
                }

                if (beaconManager.getBeaconParsers().get(1).getIdentifier() == UUID_BEACON_02) {
                    clazz = (Class<T>) SecondBeaconActivity.class;
                    notification(clazz, 002, "Vire a direita e siga em frente por 10 segundos");
                }

                if (beaconManager.getBeaconParsers().get(2).getIdentifier() == UUID_BEACON_03) {
                    clazz = (Class<T>) ThirdyBeaconActivity.class;
                    notification(clazz, 003, "Você chegou a sala 502b");
                }

                Log.i(TAG, "didEnterRegion - " + region);
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "didExitRegion - " + region);
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i(TAG, "didDetermineStateForRegion - " + region);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("BEACON-01", null, null, null));
        } catch (RemoteException e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        map.setTrafficEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setIndoorLevelPickerEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        map.addMarker(new MarkerOptions().position(FACULDADE).title("Universidade Anhembi Morumbi"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(FACULDADE, 15));
    }

    public void notification(Class<T> activity, int id, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("IntBeacon "+ id)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentText(text);

        Intent intent = new Intent(MainActivity.this, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    public void dispatchBeaconNotification() {
        Toast.makeText(getBaseContext(), "test", Toast.LENGTH_SHORT).show();
        handler.postDelayed(runnableFirstBeacon, TIMEOUT);
    }

    public void dispatchFirstBeacon() {
        clazz = (Class<T>) FirstBeaconActivity.class;
        notification(clazz, 001, "Siga em Frente por 10 segundos");
        handler.postDelayed(runnableSecondBeacon, TIMEOUT);
    }

    public void dispatchSecondBeacon() {
        clazz = (Class<T>) SecondBeaconActivity.class;
        notification(clazz, 002, "Vire a direita e siga em frente por 10 segundos");
        handler.postDelayed(runnableThirdBeacon, TIMEOUT);
    }

    public void dispatchThirdBeacon() {
        clazz = (Class<T>) ThirdyBeaconActivity.class;
        notification(clazz, 003, "Você chegou a sala 502b");
    }
}
