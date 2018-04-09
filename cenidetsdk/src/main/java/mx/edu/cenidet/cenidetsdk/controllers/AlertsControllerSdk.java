package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 4/8/2018.
 */

public class AlertsControllerSdk implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback {
    private static String URL_BASE_ALERT = ConfigServer.http_host_alert.getPropiedad();
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;
    private Context context;
    private AlertsServiceMethods alertsServiceMethods;

    public AlertsControllerSdk(Context context, AlertsControllerSdk.AlertsServiceMethods alertsServiceMethods){
        this.context = context;
        this.alertsServiceMethods = alertsServiceMethods;
    }


    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "readAlertsByCampus":
                alertsServiceMethods.readAlertsByCampus(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {

    }

    public interface AlertsServiceMethods{
        void readAlertsByCampus(Response response);
    }

    /**
     * Leer las alertas de un determinado campus
     */
    public void readAlertsByCampus(){
        method = "readAlertsByCampus";
        String URL = URL_BASE_ALERT + "5a08f54972a5b81a7d040119";
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
