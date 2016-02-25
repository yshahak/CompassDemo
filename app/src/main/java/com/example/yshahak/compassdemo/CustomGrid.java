package com.example.yshahak.compassdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by B.E.L on 25/02/2016.
 */
public class CustomGrid extends RelativeLayout {

    private final Paint paint;
    private float widthCell, heightCell, widthScreen, heightScreen, degreeFactor;
    private float offset;
    private View square1, square2, square3, square4 ;

    public CustomGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs,  R.styleable.CustomGrid);
        this.offset = attributes.getFloat(0, R.styleable.CustomGrid_offset);
        attributes.recycle();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.blue_grid));
        paint.setStrokeWidth(1);

        init();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;
        widthCell = widthScreen / 5f;
        heightCell = heightScreen / 8f;
        degreeFactor = widthCell / 360f;

    }

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.small_offer_icon, this, false);
        addView(v);
        //inflate(getContext(), R.layout.custom_grid_layout, this);
//        square1 = findViewById(R.id.img_square1);
//        square2 = findViewById(R.id.img_square2);
//        square3 = findViewById(R.id.img_square3);
//        square4 = findViewById(R.id.img_square4);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setGravity(Gravity.CENTER);
        ImageView mainImage = new ImageView(getContext());
        LayoutParams params = new LayoutParams((int)widthCell, (int)heightCell);

        mainImage.setImageResource(R.mipmap.ic_launcher);
        mainImage.setLayoutParams(params);

        addView(mainImage);

        RelativeLayout.LayoutParams crossParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        crossParams.addRule(RelativeLayout.ALIGN_TOP | RelativeLayout.ALIGN_LEFT, mainImage.getId());

        TextView tv = new TextView(getContext());

        tv.setText("hello world");

        addView(tv);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //super.onDraw(canvas); // no need to call super
        int i = 0;
        while (widthCell * i + offset >= 0 && widthCell * i + offset < widthScreen ){
            if (widthCell * i + offset == 0){
                i++;
                continue;
            }
            canvas.drawLine(widthCell * i + offset, 0, widthCell * i + offset, heightScreen, paint);
            i++;
        }

        canvas.drawLine(0, heightCell * 1, widthScreen, heightCell * 1, paint);
        canvas.drawLine(0, heightCell * 2, widthScreen, heightCell * 2, paint);
        canvas.drawLine(0, heightCell * 3, widthScreen, heightCell * 3, paint);
        canvas.drawLine(0, heightCell * 4, widthScreen, heightCell * 4, paint);
        canvas.drawLine(0, heightCell * 5, widthScreen, heightCell * 5, paint);
        canvas.drawLine(0, heightCell * 6, widthScreen, heightCell * 6, paint);
        canvas.drawLine(0, heightCell * 7, widthScreen, heightCell * 7, paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void fadeInSquares(){
        getSquaresAnimation().start();
    }

    private AnimatorSet getSquaresAnimation() {
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(square1, View.ALPHA, 1);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(square2, View.ALPHA, 1);
        ObjectAnimator alpha3 = ObjectAnimator.ofFloat(square3, View.ALPHA, 1);
        ObjectAnimator alpha4 = ObjectAnimator.ofFloat(square4, View.ALPHA, 1);
        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(square1, View.SCALE_X, 1.3f, 1);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(square1, View.SCALE_Y, 1.3f, 1);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(square2, View.SCALE_X, 1.3f, 1);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(square2, View.SCALE_Y, 1.3f, 1);
        ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(square3, View.SCALE_X, 1.3f, 1);
        ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(square3, View.SCALE_Y, 1.3f, 1);
        ObjectAnimator scaleX4 = ObjectAnimator.ofFloat(square4, View.SCALE_X, 1.3f, 1);
        ObjectAnimator scaleY4 = ObjectAnimator.ofFloat(square4, View.SCALE_Y, 1.3f, 1);
        AnimatorSet animatorSet1 = new AnimatorSet().setDuration(1200);
        animatorSet1.playTogether(alpha1, scaleX1, scaleY1);
        AnimatorSet animatorSet2 = new AnimatorSet().setDuration(1200);
        animatorSet2.setStartDelay(300);
        animatorSet2.playTogether(alpha2, scaleX2, scaleY2);
        AnimatorSet animatorSet3 = new AnimatorSet().setDuration(1200);
        animatorSet3.setStartDelay(600);
        animatorSet3.playTogether(alpha3, scaleX3, scaleY3);
        AnimatorSet animatorSet4 = new AnimatorSet().setDuration(1200);
        animatorSet4.setStartDelay(900);
        animatorSet4.playTogether(alpha4, scaleX4, scaleY4);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorSet1, animatorSet2, animatorSet3, animatorSet4);
        return animatorSet;
    }

    public void setOffset(float azimuth) {
        this.offset = azimuth * degreeFactor;
        invalidate();

    }

    public float getOffset() {
        return this.offset;
    }
}
