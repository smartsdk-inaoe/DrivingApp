package mx.edu.cenidet.drivingapp.adapters;

/**
 * Created by Cipriano on 4/14/2018.
 */

import android.content.Context;
import android.support.v4.view.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * View pager adapter
 */
public class MyViewPagerAdapter extends android.support.v4.view.PagerAdapter {
    private LayoutInflater layoutInflater;
    private int[] layouts;
    private Context context;
    private int auxPosition;

    public MyViewPagerAdapter(Context context, int[] layouts) {
        this.layouts = layouts;
        this.context = context;
    }

    public MyViewPagerAdapter(Context context, int[] layouts, int auxPosition){
        this.layouts = layouts;
        this.context = context;
        this.auxPosition = auxPosition;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        /*if(auxPosition == -1){
            view = layoutInflater.inflate(layouts[position], container, false);
            Log.i("VIEW", "Position------------------------------------------"+position);
            container.addView(view);
        }else {
            view = layoutInflater.inflate(layouts[auxPosition], container, false);
            Log.i("VIEW", "Position------------------------------------------"+position);
            container.addView(view);
        }*/
        view = layoutInflater.inflate(layouts[auxPosition], container, false);
        Log.i("VIEW", "Position------------------------------------------"+position);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        Log.i("VIEW", "Length------------------------------------------"+layouts.length);
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
