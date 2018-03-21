package mx.edu.cenidet.drivingapp.fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SendDataService.SendDataMethods{
    private View rootView;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    private static final String STATUS = "Status";
    private Context context;
    private SendDataService sendDataService;
    private TextView tvDetailCampus;
    private ImageView imagenViewDetailCampus;

    public HomeFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(context, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tvDetailCampus = (TextView) rootView.findViewById(R.id.tvDetailCampus);
        imagenViewDetailCampus = (ImageView) rootView.findViewById(R.id.imagenViewDetailCampus);
        return rootView;
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        Log.i("STATUS: ","HomeFragment-sendLocationSpeed");
    }

    @Override
    public void detectCampus(Campus campus, boolean statusLocation) {
        if(statusLocation == true){
            //Log.i("STATUS: ","Campus Name: "+campus.getName()+" statusLocation: "+statusLocation);
            imagenViewDetailCampus.setImageResource(R.drawable.inside);
            tvDetailCampus.setText(context.getString(R.string.message_name_campus)+": "+campus.getName()+"\n"+context.getString(R.string.message_address_campus)+": "+campus.getAddress());
        }else{
            imagenViewDetailCampus.setImageResource(R.drawable.outside);
            tvDetailCampus.setText(context.getString(R.string.message_any_campus));
        }
        /*Log.i("STATUS: ","id: "+campus.getId());
        Log.i("STATUS: ","name: "+campus.getName());
        Log.i("STATUS: ","Address: "+campus.getAddress());
        Log.i("STATUS: ","Dentro/Fuera: "+statusLocation);*/
    }
}
