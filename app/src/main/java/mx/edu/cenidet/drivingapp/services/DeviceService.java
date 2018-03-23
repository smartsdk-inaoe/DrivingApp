package mx.edu.cenidet.drivingapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import www.fiware.org.ngsi.datamodel.entity.DeviceSensor;
import www.fiware.org.ngsi.utilities.Constants;

/**
 * Created by Cipriano on 3/3/2018.
 */

public class DeviceService extends Service{
    private Context context;
    private static final String STATUS = "STATUS";
    private double longitudeGPS, latitudeGPS;
    private double longitudeNetwork, latitudeNetwork;
    private double speedMS;
    private double speedKmHr;
    private LocationManager locationManager;
    //private UsersLocationService uLocationService;
    private int id;

    //Giroscopio y acelerometro
    private double ax, ay, az, gx, gy, gz;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private ArrayList<Double> listValueSensor;
    private String deviceId, androidId;
    DeviceSensor deviceSensor;
    private double speedMin = 0.0, speedMax = 3.0, speedLast = 0.0;
    private  double latitudeLast, longitudeLast;
    //variables que se utilizaran en el calculo paradas repentinas
    private LatLng latLngFrom, latLngTo;
    private double latitudeFrom, longitudeFrom, latitudeTo, longitudeTo;
    private double distance;
    private double speedFrom, speedTo;
    private HashMap<String, Double> hashMapSpeedFromTo;
    private HashMap<String, Double> hashMapLatLngFromTo;

    //Medir distancias
    float[] distanceArray;
    public void onCreate() {
        super.onCreate();
        context = HomeActivity.MAIN_CONTEXT;
        hashMapSpeedFromTo = new HashMap<String, Double>();
        hashMapLatLngFromTo = new HashMap<String, Double>();
        distanceArray = new float[2];
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

        //Sensor acelerometro y giroscopio
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(sensors, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensors, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
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

                // DETECTAR EVENTOS LOCATION-----

                //Detectar excesos de velocidad
                if(speedKmHr > speedMax){
                    String message = "Excedio la velodidad maxima...";
                }else if(speedKmHr < speedMin){
                    String message = "Su velocidad esta por debajo de los limites establecidos...";
                }else{
                    String message = "Conduce dentro de los parametros establecidos...";
                }
                Log.i(STATUS, "SPEED: "+speedKmHr);
                //Logica para obtener la velocidad anterior y actual
                if(hashMapSpeedFromTo.isEmpty() || hashMapSpeedFromTo.size() == 0){
                    speedFrom = speedKmHr;
                    speedTo = speedKmHr;
                    hashMapSpeedFromTo.put("speedFrom", speedFrom);
                    hashMapSpeedFromTo.put("speedTo", speedTo);
                    Log.i("STATUS", "SPEED INICIO VACIO: speedFrom: "+hashMapSpeedFromTo.get("speedFrom")+" speedTo: "+hashMapSpeedFromTo.get("speedTo"));
                }else{
                    speedFrom = hashMapSpeedFromTo.get("speedTo");
                    speedTo = speedKmHr;
                    hashMapSpeedFromTo.put("speedFrom", speedFrom);
                    hashMapSpeedFromTo.put("speedTo", speedTo);
                    Log.i("STATUS", "SPEED NO VACIO: speedFrom: "+hashMapSpeedFromTo.get("speedFrom")+" speedTo: "+hashMapSpeedFromTo.get("speedTo"));
                }

                Log.i(STATUS, "GPS LATITUDE: "+latitudeGPS+" longitude: "+longitudeGPS);
                //Logica para obtener location apartir de (location anterior) y location hasta (location actual)
                if(hashMapLatLngFromTo.isEmpty() || hashMapLatLngFromTo.size() == 0){
                    latitudeFrom = latitudeGPS;
                    longitudeFrom = longitudeGPS;
                    latitudeTo = latitudeGPS;
                    longitudeTo = longitudeGPS;
                    hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                    hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                    hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                    hashMapLatLngFromTo.put("longitudeTo", longitudeTo);
                    Log.i("STATUS", "hashMapLatLngFromTo INICIO VACIO:\nlatitudeFrom: "+hashMapLatLngFromTo.get("latitudeFrom")+" longitudeFrom: "+hashMapLatLngFromTo.get("longitudeFrom")+" latitudeTo: "+hashMapLatLngFromTo.get("latitudeTo")+" longitudeTo: "+hashMapLatLngFromTo.get("longitudeTo"));
                }else{
                    latitudeFrom = hashMapLatLngFromTo.get("latitudeTo");
                    longitudeFrom = hashMapLatLngFromTo.get("longitudeTo");
                    latitudeTo = latitudeGPS;
                    longitudeTo = longitudeGPS;
                    hashMapLatLngFromTo.put("latitudeFrom", latitudeFrom);
                    hashMapLatLngFromTo.put("longitudeFrom", longitudeFrom);
                    hashMapLatLngFromTo.put("latitudeTo", latitudeTo);
                    hashMapLatLngFromTo.put("longitudeTo", longitudeTo);

                    Log.i("STATUS", "hashMapLatLngFromTo NO VACIO:\nlatitudeFrom: "+hashMapLatLngFromTo.get("latitudeFrom")+" longitudeFrom: "+hashMapLatLngFromTo.get("longitudeFrom")+" latitudeTo: "+hashMapLatLngFromTo.get("latitudeTo")+" longitudeTo: "+hashMapLatLngFromTo.get("longitudeTo"));
                }

                //PARADA REPENTINAS-----
                if(!hashMapSpeedFromTo.isEmpty()){
                    if(hashMapSpeedFromTo.get("speedFrom") != 0 && hashMapSpeedFromTo.get("speedTo") == 0){
                        //Calculo de la distancia
                        // double distance = 0;
                    }else{

                    }
                }
                latLngFrom = new LatLng(hashMapLatLngFromTo.get("latitudeFrom"), hashMapLatLngFromTo.get("longitudeFrom"));
                latLngTo = new LatLng(hashMapLatLngFromTo.get("latitudeTo"), hashMapLatLngFromTo.get("longitudeTo"));
                //latLngTo = new LatLng(18.876807, -99.219968);
                distance = SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
                Log.i(STATUS, "DISTANCE 1: "+distance+"m");

                location.distanceBetween(latitudeLast, longitudeLast, latitudeGPS, longitudeGPS, distanceArray);
                Log.i(STATUS, "DISTANCE 2: "+distanceArray[0]+"km");
                 //location.distanceBetween();
                // FIN DETECTAR EVENTOS LOCATION-----



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

    //obtener los datos del sensor de acelerometro y giroscopio
    private final SensorEventListener sensors = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized(this) {
                //Fecha
                Date date = new Date();
                DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                if (sensorEvent.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
                    int id = sensorEvent.sensor.getId();
                    String name = sensorEvent.sensor.getName();
                    int type = sensorEvent.sensor.getType();
                    String typeString = sensorEvent.sensor.getStringType();
                    String vendor = sensorEvent.sensor.getVendor();
                    int version = sensorEvent.sensor.getVersion();
                    float power = sensorEvent.sensor.getPower();
                    long current_time = sensorEvent.timestamp;

                    ax = sensorEvent.values[0];
                    ay = sensorEvent.values[1];
                    az = sensorEvent.values[2];

                    listValueSensor = new ArrayList<Double>();
                    listValueSensor.add(ax);
                    listValueSensor.add(ay);
                    listValueSensor.add(az);
                    DeviceSensor deviceSensor = new DeviceSensor();
                    deviceSensor.setId("Accelerometer_Smartphone_"+vendor+"_"+version+"_"+androidId);
                    deviceSensor.setType("Device");
                    deviceSensor.getCategory().setValue("sensor");
                    deviceSensor.getFunction().setValue("sensing");
                    deviceSensor.getControlledProperty().setValue(name);
                    deviceSensor.getData().setValue(listValueSensor);
                    deviceSensor.getDateCreated().setValue(""+formatDate.format(date));
                    deviceSensor.getRefDevice().setValue(deviceId);

                    //almacenar la información en la DB local del dispositivo movil

                    //LOGICA PARA REALIZAR LOS CALCULOS CON EL ACELEROMETRO....

                    Intent localIntent = new Intent(Constants.SERVICE_RUNNING_SENSORS).putExtra(Constants.ACCELEROMETER_RESULT_SENSORS, deviceSensor);
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);

                    //Log.i("json ACCELEROMETER: ", ""+functions.checkForNewsAttributes(deviceSensor));
                    //Log.i("ACCELEROMETER", "AX "+ax+" AY "+ay+" AZ "+az);
                    //Log.i("ACCELEROMETER", "AX "+ax+" AY "+ay+" AZ "+az +" -time: "+current_time+" -Id: "+id+ " -name: "+name+" -type: "+type+" -typeString: "+typeString+" -vendor: "+vendor+" -versión: "+version+" -power: "+power);

                }else if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE){
                    int id = sensorEvent.sensor.getId();
                    String name = sensorEvent.sensor.getName();
                    int type = sensorEvent.sensor.getType();
                    String typeString = sensorEvent.sensor.getStringType();
                    String vendor = sensorEvent.sensor.getVendor();
                    int version = sensorEvent.sensor.getVersion();
                    float power = sensorEvent.sensor.getPower();
                    long current_time = sensorEvent.timestamp;
                    gx = sensorEvent.values[0];
                    gy = sensorEvent.values[1];
                    gz = sensorEvent.values[2];

                    listValueSensor = new ArrayList<Double>();
                    listValueSensor.add(gx);
                    listValueSensor.add(gy);
                    listValueSensor.add(gz);
                    DeviceSensor deviceSensor = new DeviceSensor();
                    deviceSensor.setId("Gyroscope_Smartphone_"+vendor+"_"+version+"_"+androidId);
                    deviceSensor.setType("Device");
                    deviceSensor.getCategory().setValue("sensor");
                    deviceSensor.getFunction().setValue("sensing");
                    deviceSensor.getControlledProperty().setValue(name);
                    deviceSensor.getData().setValue(listValueSensor);
                    deviceSensor.getDateCreated().setValue(""+formatDate.format(date));
                    deviceSensor.getRefDevice().setValue(deviceId);

                    //almacenar la información en la DB local del dispositivo movil

                    Intent localIntent = new Intent(Constants.SERVICE_RUNNING_SENSORS).putExtra(Constants.GYROSCOPE_RESULT_SENSORS, deviceSensor);
                    LocalBroadcastManager.getInstance(DeviceService.this).sendBroadcast(localIntent);
                    //Log.i("GYROSCOPE", "AX "+gx+" AY "+gy+" AZ "+gz);
                    //Log.i("GYROSCOPE", "AX "+gx+" AY "+gy+" AZ "+gz +" -time: "+current_time+" -Id: "+id+ " -name: "+name+" -type: "+type+" -typeString: "+typeString+" -vendor: "+vendor+" -versión: "+version+" -power: "+power+" ORIENTATION: "+getRotation(context));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public static double calculateAcceleration(ArrayList<Double> values) {
        double acceleration = Math.sqrt(Math.pow(values.get(0), 2)
                + Math.pow(values.get(1), 2) + Math.pow(values.get(2), 2));
        return acceleration;
    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "vertical";
            case Surface.ROTATION_90:
                return "horizontal";
            case Surface.ROTATION_180:
                return "vertical inversa";
            default:
                return "horizontal inversa";
        }
    }

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
