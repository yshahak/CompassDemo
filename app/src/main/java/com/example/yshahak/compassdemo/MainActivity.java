package com.example.yshahak.compassdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private TextView textViewSonarHot;
    private View hotFire;
    private View halfPanelAnimationView;
    private View halfPanel;
    private View impNeedle;
    private CustomGrid grid;
    private TextView textAzimuth;
    private View smallArrow;
    private View meterGrid;
    private boolean animationEnded;
    String azimuthHolder;
    private Compass mCompass;
    private ViewGroup root;
    private View offerBubble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = (ViewGroup) findViewById(R.id.root);

        mCompass = new Compass(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.mGravity = this.mSensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY );

        this.mAccelerometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );

        this.mMagnetometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );



//
//        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        azimuthHolder = getString(R.string.azimuth);

        textViewSonarHot = (TextView) findViewById(R.id.text_view_sonar_hot);
        hotFire = findViewById(R.id.img_hot_fire);
        halfPanelAnimationView = findViewById(R.id.half_panel_rotate_holder);
        halfPanel = findViewById(R.id.half_panel);
        impNeedle = findViewById(R.id.img_needle);
        grid = (CustomGrid) findViewById(R.id.grid);

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
        //AnimatorSet squaresAnimator = getSquaresAnimation();
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
        impNeedle.setPivotX(((ImageView)impNeedle).getDrawable().getIntrinsicWidth() / 2);
        impNeedle.setPivotY(((ImageView)impNeedle).getDrawable().getIntrinsicHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000).setStartDelay(1000);
        ObjectAnimator rotateNeedle = ObjectAnimator.ofFloat(impNeedle, View.ROTATION, 90, -270);
        rotateNeedle.setDuration(3000).setStartDelay(3700);
        rotateNeedle.setRepeatCount(ValueAnimator.INFINITE);
        rotateNeedle.setRepeatMode(ValueAnimator.RESTART);
        rotateNeedle.setInterpolator(new LinearInterpolator());
        rotateNeedle.start();
        ObjectAnimator alpha4 = ObjectAnimator.ofFloat(impNeedle, View.ALPHA, 1).setDuration(0);
        alpha4.setStartDelay(3750);
        alpha4.start();

        ObjectAnimator rotateToStart = ObjectAnimator.ofFloat(halfPanelAnimationView, View.ROTATION, -90, 0, -10);

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
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationEnded = true;

            }
        });
        return animatorSet;
    }

    private AnimatorSet getTriangleAnimation() {
        textAzimuth.setText(String.format(azimuthHolder, 0f));
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
        this.haveGravity = this.mSensorManager.registerListener( mSensorEventListener, this.mGravity, SensorManager.SENSOR_DELAY_UI);
        this.haveAccelerometer = this.mSensorManager.registerListener( mSensorEventListener, this.mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        this.haveMagnetometer = this.mSensorManager.registerListener( mSensorEventListener, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        // if there is a gravity sensor we do not need the accelerometer
        if( this.haveGravity ) {
            this.mSensorManager.unregisterListener(this.mSensorEventListener, this.mAccelerometer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this.mSensorEventListener);
        //mCompass.unregisterListener();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        if (animationEnded) {
            textAzimuth.setText(String.format(azimuthHolder, degree));
            grid.setOffset(degree);
            smallArrow.setRotation(degree);
        }
    }

    private float getRotationForSmallArrow(float degree) {
        float convert;
        if (degree  < 180){
            convert = degree / 2f;
        } else {
            convert =  -(((degree - 180)) / 2f);
        }
        Log.d("TAG", "degree: " +degree + "  ,   convert: " + convert);
        return convert;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private Sensor mGravity;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    boolean haveGravity = false;
    boolean haveAccelerometer = false;
    boolean haveMagnetometer = false;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float[] gData = new float[3]; // gravity or accelerometer
        float[] mData = new float[3]; // magnetometer
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] orientation = new float[3];

        public void onAccuracyChanged( Sensor sensor, int accuracy ) {}

        @Override
        public void onSensorChanged( SensorEvent event ) {
            switch ( event.sensor.getType() ) {
                case Sensor.TYPE_GRAVITY:
                    gData = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    gData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = event.values.clone();
                    break;
                default: return;
            }

            if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
                float mAzimuth = (float) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);
                if (animationEnded) {
                    textAzimuth.setText(String.format(azimuthHolder, mAzimuth));
                    grid.setOffset(mAzimuth);
                    smallArrow.setRotation(mAzimuth);
                }

            }
        }
    };

    public void updateOfferBubbleUi(float leftMargin, float topMargin){
        if (offerBubble == null ){
            offerBubble = LayoutInflater.from(this).inflate(R.layout.bubble_offer, root, false);
            root.addView(offerBubble, root.getChildCount() - 1);
        } else if (offerBubble.getAlpha() == 0){
            offerBubble.animate().alpha(1).setDuration(300).start();
        }
        offerBubble.setTranslationX(leftMargin);
        offerBubble.setTranslationY(topMargin);
    }

    public void removeOfferBubble(){
        if (offerBubble != null){
            offerBubble.animate().alpha(0).setDuration(300).start();
        }
    }

}
