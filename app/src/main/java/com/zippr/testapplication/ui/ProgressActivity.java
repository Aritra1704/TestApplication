package com.zippr.testapplication.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.zippr.testapplication.R;

public class ProgressActivity extends AppCompatActivity {

    FrameLayout.LayoutParams parms;
    LinearLayout.LayoutParams par;
    float dx=0,dy=0,x=0,y=0;
    private int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView ivArrow = (ImageView) findViewById(R.id.ivArrow);
        Animation animation = new AnimationUtils().loadAnimation(this, R.anim.left_to_right);
        ivArrow.setAnimation(animation);

        ImageView ivSeekArrow = (ImageView) findViewById(R.id.ivSeekArrow);
        ivSeekArrow.setAnimation(animation);

        SeekBar sbReceive = (SeekBar) findViewById(R.id.sbReceive);
        sbReceive.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() < 80)
                    seekBar.setProgress(0);
                else
                    Toast.makeText(ProgressActivity.this, "Seekbar Received", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView ivStartBar = (ImageView) findViewById(R.id.ivStartBar);
        ivStartBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        parms = (FrameLayout.LayoutParams) ivStartBar.getLayoutParams();
                        par = (LinearLayout.LayoutParams) getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLayoutParams();
                        dx = event.getRawX() - parms.leftMargin;
                    break;

                    case MotionEvent.ACTION_MOVE :
                        x = event.getRawX();
                        parms.leftMargin = (int) (x-dx);
                        ivStartBar.setLayoutParams(parms);
                    break;

                    case MotionEvent.ACTION_UP :
                        x = event.getRawX();
                        if(x < (width * .80))
                            parms.leftMargin = (int) (0);
                        else
                            Toast.makeText(ProgressActivity.this, "Received", Toast.LENGTH_SHORT).show();
                        ivStartBar.setLayoutParams(parms);
                    break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
    }
}
