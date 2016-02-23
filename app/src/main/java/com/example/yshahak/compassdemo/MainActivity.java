package com.example.yshahak.compassdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //    private float currentDegree = 0f;
//    private SensorManager mSensorManager;
//    private Sensor accelerometer;
//    private Sensor magnetometer;
    private TextView textViewSonarHot;
    private View hotFire;
    private View halfPanelAnimationView;
    private View halfPanel;
    private View grid;
    private TextView textAzimuth;
    private View smallArrow;
    private View meterGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        textViewSonarHot = (TextView) findViewById(R.id.text_view_sonar_hot);
        hotFire = findViewById(R.id.img_hot_fire);
        halfPanelAnimationView = findViewById(R.id.half_panel_rotate_holder);
        halfPanel = findViewById(R.id.half_panel);
        grid = findViewById(R.id.grid);
        textAzimuth = (TextView) findViewById(R.id.text_azimuth);
        smallArrow = findViewById(R.id.img_arrow);
        meterGrid = findViewById(R.id.meter_grid);
        //hot panel fade in animation
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        hotFire.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Remove the listener before proceeding
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    hotFire.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    hotFire.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int[] location = new int[2];
                hotFire.getLocationOnScreen(location);
                int xOffset = metrics.widthPixels/2 - location[0];
                hotFire.animate().translationX(xOffset).setDuration(0).start();
                hotFire.animate().translationY(hotFire.getHeight()).setDuration(0).start();
                startHotSonarEnterAnimation();
            }
        });


    }

    private void startHotSonarEnterAnimation() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(hotFire, View.ALPHA, 1).setDuration(1000);
        ObjectAnimator transY = ObjectAnimator.ofFloat(hotFire, View.TRANSLATION_Y, 1).setDuration(1000);
        ObjectAnimator transX = ObjectAnimator.ofFloat(hotFire, View.TRANSLATION_X, 1).setDuration(1000);
        ObjectAnimator fadeText = ObjectAnimator.ofFloat(textViewSonarHot, View.ALPHA, 1).setDuration(600);
        transX.setStartDelay(1000);
        fadeText.setStartDelay(1400);
        AnimatorSet hotPolarAnimation = new AnimatorSet();
        hotPolarAnimation.playTogether(alpha, transY, transX, fadeText);
        AnimatorSet halfPanelAnimation = getHalfPanelEnterAnimation();
        AnimatorSet triangleAnimation = getTriangleAnimation();
        ObjectAnimator meterGridAnimation = ObjectAnimator.ofFloat(meterGrid, View.ALPHA, 1).setDuration(1500);
        AnimatorSet animator = new AnimatorSet();
        animator.playSequentially(hotPolarAnimation, halfPanelAnimation, triangleAnimation, meterGridAnimation);
        animator.start();
    }



    private AnimatorSet getHalfPanelEnterAnimation() {
        float pivotX = ((ImageView)halfPanelAnimationView).getDrawable().getIntrinsicWidth() / 2;
        float pivotY = ((ImageView)halfPanelAnimationView).getDrawable().getIntrinsicHeight();
        halfPanelAnimationView.setPivotX(pivotX);
        halfPanelAnimationView.setPivotY(pivotY);
        halfPanel.setPivotX(pivotX);
        halfPanel.setPivotY(pivotY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000).setStartDelay(1000);
        ObjectAnimator rotateToStart = ObjectAnimator.ofFloat(halfPanelAnimationView,
                View.ROTATION, -90, 0, -10);
//        rotateToStart.setRepeatCount(ValueAnimator.INFINITE);
//        rotateToStart.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(halfPanel,
                View.ROTATION, 0, 90, 80);
//        rotate.setRepeatCount(ValueAnimator.INFINITE);
//        rotate.setRepeatMode(ValueAnimator.REVERSE);
//        rotate.setInterpolator(new DecelerateInterpolator());
//        rotateToStart.setInterpolator(new DecelerateInterpolator());
        rotate.setDuration(1500).setStartDelay(200);
        rotateToStart.setDuration(1500).setStartDelay(200);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(halfPanel, View.ALPHA, 1).setDuration(200);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(halfPanelAnimationView, View.ALPHA, 1).setDuration(200);
        ObjectAnimator alpha3 = ObjectAnimator.ofFloat(grid, View.ALPHA, 1).setDuration(1000);
        animatorSet.playTogether(alpha1, alpha2, alpha3, rotateToStart, rotate);
        return animatorSet;
    }

    private AnimatorSet getTriangleAnimation() {
        textAzimuth.setText("a: 0" + (char) 0x00B0);
        textAzimuth.animate().translationY(textAzimuth.getHeight() / 4).setDuration(0).start();

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(textAzimuth, View.ALPHA, 1).setDuration(500);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(smallArrow, View.ALPHA, 1).setDuration(500);
        ObjectAnimator transY = ObjectAnimator.ofFloat(textAzimuth, View.TRANSLATION_Y, 1).setDuration(500);
        alpha1.setStartDelay(100);
        transY.setStartDelay(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alpha1, alpha2, transY);
        return animatorSet;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),   SensorManager.SENSOR_DELAY_GAME);

//        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
//        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
//        float degree = Math.round(event.values[0]);
//        degree = SensorManager.getOrientation(event.values, event.values);
//        textView.setText("Heading: " + Float.toString(degree) + " degrees");
//        currentDegree = -degree;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                Float azimut = orientation[0];
                //textView.setText("Heading: " + Double.toString(Math.round(azimut)) + " degrees");

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
