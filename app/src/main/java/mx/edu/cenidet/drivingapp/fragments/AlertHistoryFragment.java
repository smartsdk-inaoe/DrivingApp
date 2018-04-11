package mx.edu.cenidet.drivingapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.edu.cenidet.drivingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertHistoryFragment extends Fragment {
    View rootView;

    public AlertHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_alert_history, container, false);
        return rootView;
    }

}
