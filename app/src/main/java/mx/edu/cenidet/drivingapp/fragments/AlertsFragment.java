package mx.edu.cenidet.drivingapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mx.edu.cenidet.cenidetsdk.controllers.AlertsControllerSdk;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.AlertMapDetailActivity;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterAlerts;
import www.fiware.org.ngsi.datamodel.entity.Alert;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertsFragment extends Fragment implements AlertsControllerSdk.AlertsServiceMethods{
    private View rootView;
    private Context context;
    private ListView listViewAlerts;
    private AlertsControllerSdk alertsControllerSdk;
    private ArrayList<Alert> listAlerts;
    private MyAdapterAlerts myAdapterAlerts;
    private AdapterView.AdapterContextMenuInfo info;
    private String category, description, location;

    public AlertsFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        alertsControllerSdk = new AlertsControllerSdk(context, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        alertsControllerSdk.readAlertsByCampus();
        listAlerts = new ArrayList<Alert>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAlerts = (ListView) rootView.findViewById(R.id.listViewAlerts);
        registerForContextMenu(listViewAlerts);
    }

    @Override
    public void readAlertsByCampus(Response response) {
        Log.i("Status: ", "Code Alerts: "+response.getHttpCode());
        switch (response.getHttpCode()){
            case 200:
                Log.i("Status: ", "Body: "+response.getBodyString());
                Alert alert;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        alert = new Alert();
                        JSONObject object = jsonArray.getJSONObject(i);
                        alert.setId(object.getString("id"));
                        alert.setType(object.getString("type"));
                        alert.getAlertSource().setValue(object.getString("alertSource"));
                        alert.getCategory().setValue(object.getString("category"));
                        alert.getDateObserved().setValue(object.getString("dateObserved"));
                        alert.getDescription().setValue(object.getString("description"));
                        alert.getLocation().setValue(object.getString("location"));
                        alert.getSeverity().setValue(object.getString("severity"));
                        alert.getSubCategory().setValue(object.getString("subCategory"));
                        alert.getValidFrom().setValue(object.getString("validFrom"));
                        alert.getValidTo().setValue(object.getString("validTo"));
                        listAlerts.add(alert);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(listAlerts.size() > 0){
                        myAdapterAlerts = new MyAdapterAlerts(context, R.id.listViewAlerts, listAlerts);
                        listViewAlerts.setAdapter(myAdapterAlerts);
                    }
                }

                break;
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();

        info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        //menu.setHeaderTitle(listAlerts.get(info.position).getId());
        menu.setHeaderTitle(listAlerts.get(info.position).getCategory().getValue());
        menuInflater.inflate(R.menu.alert_map_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_see_map_alert:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                category = listAlerts.get(info.position).getCategory().getValue();
                description = listAlerts.get(info.position).getDescription().getValue();
                location = listAlerts.get(info.position).getLocation().getValue();
                Intent intent = new Intent(context, AlertMapDetailActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("description", description);
                intent.putExtra("location", location);
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
