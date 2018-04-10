package mx.edu.cenidet.drivingapp.services;

import android.util.Log;
import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mx.edu.cenidet.cenidetsdk.controllers.DeviceTokenControllerSdk;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.drivingapp.activities.LoginActivity;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "Alertas";
    private ApplicationPreferences appPreferences;
    private String fcmToken;
    public MyFirebaseInstanceIDService(){
        appPreferences = new ApplicationPreferences();
    }
    @Override
    public void onTokenRefresh() { 
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        fcmToken = appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_FCMTOKEN);
        if(refreshedToken != null){
            if (fcmToken.equals("") || fcmToken == null){
                appPreferences.saveOnPreferenceString(getApplicationContext(), ConstantSdk.PREFERENCE_NAME_GENERAL, ConstantSdk.PREFERENCE_KEY_FCMTOKEN, refreshedToken);
                //deviceTokenControllerSdk.createDeviceToken(refreshedToken, new DevicePropertiesFunctions().getDeviceId(context));
            }else{
                //deviceTokenControllerSdk.updateDeviceToken(fcmToken, new DevicePropertiesFunctions().getDeviceId(context));
            }
        }

        sendRegistrationToServer(refreshedToken);
    }
    
    private void sendRegistrationToServer(String token) {

        Context context = getApplicationContext();

        /*Intent serviceIntent = new Intent(context, FCMToken.class);
        serviceIntent.putExtra("FCMToken", token);
        context.startService(serviceIntent);
        HeadlessJsTaskService.acquireWakeLockNow(context);*/

    }
}