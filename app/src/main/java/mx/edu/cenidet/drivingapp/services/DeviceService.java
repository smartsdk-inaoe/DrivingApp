package mx.edu.cenidet.drivingapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;

import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import www.fiware.org.ngsi.utilities.Constants;

/**
 * Created by Cipriano on 3/3/2018.
 */

public class DeviceService extends Service{
    private Context context;
    private static final String STATUS = "Status";
    private double longitudeGPS, latitudeGPS;
    private double longitudeNetwork, latitudeNetwork;
    private double speedMS;
    private double speedKmHr;
    private LocationManager locationManager;
    //private UsersLocationService uLocationService;
    private int id;
    @Override
    public void onCreate() {
        super.onCreate();
        context = HomeActivity.MAIN_CONTEXT;
        //uLocationService = new UsersLocationService(context,this);
        //id = HomeActivity.ID;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        return START_NOT_STICKY;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitudeGPS = (double) location.getLatitude();
                longitudeGPS = (double) location.getLongitude();
                speedMS = (double) location.getSpeed();
                speedKmHr = (double) (location.getSpeed() * 3.6);
                Intent intent = new Intent(Constants.SERVICE_CHANGE_LOCATION_DEVICE).putExtra(Constants.SERVICE_RESULT_LATITUDE, latitudeGPS)
                        .putExtra(Constants.SERVICE_RESULT_LONGITUDE, longitudeGPS).putExtra(Constants.SERVICE_RESULT_SPEED_MS, speedMS).putExtra(Constants.SERVICE_RESULT_SPEED_KMHR, speedKmHr);
                LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intent);
                /*UserLocation userLocation = updateUserLocation(HomeActivity.ID, latitudeGPS, longitudeGPS);
                try {
                    uLocationService.updateUserLocation(userLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                //Log.i(STATUS, "GPS latitude: "+latitudeGPS+" longitude: "+longitudeGPS);
            }else {
                Log.i(STATUS, "Error GPS...!");
                //Toast.makeText(getBaseContext(), "Error GPS...!", Toast.LENGTH_LONG).show();
            }
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
    };

    private final LocationListener locationListenerNetwork = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitudeNetwork = (double)location.getLatitude();
                longitudeNetwork = (double)location.getLongitude();
                speedMS = (double) location.getSpeed();
                speedKmHr = (double) (location.getSpeed() * 3.6);
                Intent intent = new Intent(Constants.SERVICE_CHANGE_LOCATION_DEVICE).putExtra(Constants.SERVICE_RESULT_LATITUDE, latitudeNetwork)
                        .putExtra(Constants.SERVICE_RESULT_LONGITUDE, longitudeNetwork).putExtra(Constants.SERVICE_RESULT_SPEED_MS, speedMS).putExtra(Constants.SERVICE_RESULT_SPEED_KMHR, speedKmHr);
                LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(intent);
                /*UserLocation userLocation = updateUserLocation(HomeActivity.ID, latitudeNetwork, longitudeNetwork);
                try {
                    uLocationService.updateUserLocation(userLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                // Log.i(STATUS, "Network latitude: "+latitudeNetwork+" longitude: "+longitudeNetwork);
            }else{
                Log.i(STATUS, "Error Network...!");
            }
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
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(STATUS, "Service destroyed...!");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationListenerGPS != null){
            locationManager.removeUpdates(locationListenerGPS);
        }
        if (locationListenerNetwork != null){
            locationManager.removeUpdates(locationListenerNetwork);
        }
    }

}
