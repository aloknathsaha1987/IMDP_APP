package com.exploreca.imdb.GoogleLicense;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.exploreca.imdb.R;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by ALOKNATH on 2/7/2015.
 */
public class GPSLicenseActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_license);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String license = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
        TextView tv = (TextView)findViewById(R.id.gps_license);
        if(license != null){
            tv.setText(license);
        }else{
            tv.setText("Google Play Services is not Installed on this device.");
        }



    }
}
