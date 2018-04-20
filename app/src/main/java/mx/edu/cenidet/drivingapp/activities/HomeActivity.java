package mx.edu.cenidet.drivingapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.adapters.PagerAdapter;
import mx.edu.cenidet.drivingapp.fragments.HomeFragment;
import mx.edu.cenidet.drivingapp.services.DeviceService;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;
import www.fiware.org.ngsi.utilities.Tools;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SendDataService.SendDataMethods, AlertController.AlertResourceMethods{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static Context MAIN_CONTEXT = null;
    private FrameLayout frameLayout;
    private ApplicationPreferences appPreferences;
    private double latitude, longitude;
    private AlertController alertController;
    private SendDataService sendDataService;

    private FloatingActionButton btnFloatingUnknown;
    private FloatingActionButton btnFloatingAccident;
    private FloatingActionButton btnFloatingTraffic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MAIN_CONTEXT = HomeActivity.this;
        appPreferences = new ApplicationPreferences();
        sendDataService = new SendDataService(this);
        alertController = new AlertController(this);
        //Inicializa los datos de conexión
        try {
            Tools.initialize("config.properties", getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Mandar a llamar el toolbar una vez generado en el activity_main de la actividad
        setToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        btnFloatingGUI();
        setFragmentDefault();
        //frameLayout = (FrameLayout)findViewById(R.id.headerNavigationDrawer).findViewById(R.id.tvUserName);

        //TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_menu));//setText(R.string.menu_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_speed));//.setText(R.string.menu_speed));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_map));//setText(R.string.menu_campus_map));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_alerts));//.setText(R.string.menu_alerts));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_campus));//setText(R.string.menu_my_campus));
        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_my_location));//setText(R.string.menu_my_location));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                Log.i("Position: ", ""+position);
                if(position == 0){
                    changeDrawerMenu(position);
                }else if(position == 1){
                    changeDrawerMenu(position);
                }else if(position == 2){
                    changeDrawerMenu(position);
                }else if(position == 3){
                    changeDrawerMenu(position);
                /*}else if(position == 4){
                    changeDrawerMenu(position);*/
                }else{
                    changeDrawerMenu(position);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean fratmentTransaction = false;
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.menu_home:
                        //fragment = new HomeFragment();
                        viewPager.setCurrentItem(0);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_speed:
                        //fragment = new SpeedFragment();
                        viewPager.setCurrentItem(1);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_campus:
                        //fragment = new ZoneFragment();
                        viewPager.setCurrentItem(2);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_alerts:
                        //fragment = new AlertsFragment();
                        viewPager.setCurrentItem(3);
                        fratmentTransaction = true;
                        break;
                    case R.id.menu_my_campus:
                        viewPager.setCurrentItem(4);
                        fratmentTransaction = true;
                        break;
                    /*case R.id.menu_my_location:
                        //fragment = new MyLocationFragment();
                        viewPager.setCurrentItem(5);
                        fratmentTransaction = true;
                        break;*/
                    case R.id.menu_history:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(getApplicationContext(), AlertHistoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_profile:
                        drawerLayout.closeDrawers();
                        /*Intent intentMyProfile = new Intent(getApplicationContext(), MyProfileActivity.class);
                        startActivity(intentMyProfile);*/
                        break;
                }

                if(fratmentTransaction){
                    //changeFragment(fragment, item);
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.header_navigation_drawer, null);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        tvUserName.setText(appPreferences.getPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_USER_NAME));
        navigationView.addHeaderView(view);

        //Para preguntar si el usuario se encuentra manejando
        //isDrivingUser();

        //Inicia el servicio para la captura de la posición.
        Intent deviceService = new Intent(MAIN_CONTEXT, DeviceService.class);
        startService(deviceService);
        Log.i("onCreate", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "-----------------------------------------------------------------------------");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause", "-----------------------------------------------------------------------------");
    }


    public void btnFloatingGUI(){
        btnFloatingUnknown = (FloatingActionButton)findViewById(R.id.btnFloatingUnknown);
        btnFloatingUnknown.setOnClickListener(this);
        btnFloatingAccident = (FloatingActionButton)findViewById(R.id.btnFloatingAccident);
        btnFloatingAccident.setOnClickListener(this);
        btnFloatingTraffic = (FloatingActionButton)findViewById(R.id.btnFloatingTraffic);
        btnFloatingTraffic.setOnClickListener(this);
    }

    private void setToolbar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragmentDefault(){
        navigationView.getMenu().getItem(0);
        //changeFragment(new HomeFragment(), navigationView.getMenu().getItem(0));
    }

    private void changeFragment(Fragment fragment, MenuItem item){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());
    }

    private void changeDrawerMenu(int position){
        MenuItem item = navigationView.getMenu().getItem(position);
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //Abrir el menu lateral
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_notify:
                Intent intent = new Intent(getApplicationContext(), AlertHistoryActivity.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void isDrivingUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_is_driving_user_title)
                .setIcon(R.drawable.ic_car)
                .setNegativeButton(R.string.message_is_driving_user_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //acciones del boton Si
                            }
                        })
                .setPositiveButton(R.string.message_is_driving_user_yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //acciones del boton Si
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFloatingUnknown:
               // startActivity(new Intent(HomeActivity.this, SendManualAlertsActivity.class));
                //Toast.makeText(getApplicationContext(), "btnFloatingUnknown...!", Toast.LENGTH_LONG).show();
                if(latitude != 0 && longitude != 0) {
                    Alert alert = new Alert();
                    alert.setId(new DevicePropertiesFunctions().getAlertId(MAIN_CONTEXT));
                    alert.getAlertSource().setValue(new DevicePropertiesFunctions().getDeviceId(MAIN_CONTEXT));
                    alert.getCategory().setValue("UnknownAlert");
                    alert.getDateObserved().setValue(Functions.getActualDate());
                    alert.getDescription().setValue("Unknown alert");
                    alert.getLocation().setValue(latitude + ", " + longitude);
                    alert.getSeverity().setValue("critical");
                    alert.getSubCategory().setValue("Unknown");
                    alert.getValidFrom().setValue(Functions.getActualDate());
                    alert.getValidTo().setValue(Functions.getActualDate());
                    try {
                        alertController.createEntity(MAIN_CONTEXT, alert.getId(), alert);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnFloatingAccident:
                Intent intentAccident = new Intent(HomeActivity.this, SendManualAlertsActivity.class);
                intentAccident.putExtra("typeAlert", 1);
                startActivity(intentAccident);
                //Toast.makeText(getApplicationContext(), "btnFloatingAccident...!", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnFloatingTraffic:
                //Toast.makeText(getApplicationContext(), "btnFloatingTraffic...!", Toast.LENGTH_LONG).show();
                Intent intentTraffic = new Intent(HomeActivity.this, SendManualAlertsActivity.class);
                intentTraffic.putExtra("typeAlert", 2);
                startActivity(intentTraffic);
                break;
        }
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {

    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {

    }

    @Override
    public void sendEvent(String event) {

    }

    @Override
    public void onCreateEntityAlert(Response response) {
        if(response.getHttpCode() == 201 || response.getHttpCode() == 200){
            Toast.makeText(MAIN_CONTEXT, R.string.message_successful_sending, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MAIN_CONTEXT, R.string.message_failed_send, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }


    //.setCancelable(false)

}
