package com.blacklist.sync;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.MainActivity;
import com.R;

/**
 * Created by SmartHub on 24-02-17.
 */

public class ActividadEstadisticas extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
    }

    public void back_button(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}