package com.glance.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.glance.R;
import com.google.android.glass.widget.CardBuilder;

public class ImageDetectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_detection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        View view = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Sample Card")
                .getView();
        setContentView(view);
    }

}
