package mx.edu.cenidet.drivingapp.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampusFragment extends Fragment implements OnMapReadyCallback, SendDataService.SendDataMethods {
    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private Marker marker;
    private CameraPosition camera;
    private Context context;
    private String name, location,  pointMap;
    private ArrayList<LatLng> listLocation;
    private JSONArray arrayLocation, arrayPoint;
    private double pointLatitude, pointLongitude;
    private SendDataService sendDataService;
    private int count = 1;
    public CampusFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(context, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_campus, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.mapCampus);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
        //Ocultar el boton
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        //createOrUpdateMarkerByLocation(pointLatitude, pointLongitude);
    }

    private void createOrUpdateMarkerByLocation(double latitude, double longitude){
        if(marker == null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true));
            zoomToLocation(latitude, longitude);
        }else{
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }

    private void zoomToLocation(double latitude, double longitude){
        camera = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(18)       //limit -> 21
                .bearing(0)    //orientación de la camara hacia el este 0°-365°
                .tilt(30)       //efecto 3D 0-90
                .build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {

    }

    @Override
    public void detectCampus(Campus campus, boolean statusLocation) {
        if(statusLocation == true){
            Log.i("STATUS: ","Count "+count++);
            name = campus.getName();
            location = campus.getLocation();
            pointMap = campus.getPointMap();
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            listLocation = new ArrayList<>();
            try {
                arrayLocation = new JSONArray(location);
                for (int j=0; j<arrayLocation.length(); j++){
                    originalString = arrayLocation.get(j).toString();
                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                    subString =  clearString.split(",");
                    latitude = Double.parseDouble(subString[0]);
                    longitude = Double.parseDouble(subString[1]);
                    listLocation.add(new LatLng(latitude,longitude));
                }
                arrayPoint = new JSONArray(pointMap);
                JSONObject jsonObject = arrayPoint.getJSONObject(0);
                pointLatitude = jsonObject.getDouble("latitude");
                pointLongitude = jsonObject.getDouble("longitude");
                //Dibuja el poligono
                gMap.addPolygon(new PolygonOptions()
                        .addAll(listLocation).strokeColor(Color.RED));
                //Centra el mapa
                zoomToLocation(pointLatitude, pointLongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{

        }
    }
}
