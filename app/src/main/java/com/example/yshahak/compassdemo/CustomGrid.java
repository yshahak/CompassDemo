package com.example.yshahak.compassdemo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by B.E.L on 25/02/2016.
 */
public class CustomGrid extends FrameLayout {

    private final Paint paint;
    private final int smallIconDimen;
    private final Bitmap bm1, bm2, bm3, bm4;
    private final Bitmap[] bmArray;
    private final Paint[] paintArray;
    private final float[] leftMarginArray, topMarginArray;
    private int bubbleWidth, bubbleHeight;
    private Rect[] rects = new Rect[4];
    private float widthCell, heightCell, widthScreen, heightScreen, degreeFactor;
    private float offset;
    private boolean left;
    private Drawable target;
    private Drawable bubble;
    private int clicked = -1;
    private Drawable icon;
    //private View square1, square2, square3, square4 ;

    public CustomGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs,  R.styleable.CustomGrid);
        this.offset = attributes.getFloat(0, R.styleable.CustomGrid_offset);
        attributes.recycle();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.blue_grid));
        paint.setStrokeWidth(1);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;
        widthCell = widthScreen / 5f;
        heightCell = heightScreen / 8f;
        degreeFactor = widthCell / 360f;

        smallIconDimen = (int)(getDpInPixels(getContext(), 48) / 2f);
        bm1 =  decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_launcher, smallIconDimen, smallIconDimen);
        bm2 =  decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_launcher, smallIconDimen, smallIconDimen);
        bm3 =  decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_launcher, smallIconDimen, smallIconDimen);
        bm4 =  decodeSampledBitmapFromResource(getResources(), R.mipmap.ic_launcher, smallIconDimen, smallIconDimen);
        bmArray = new Bitmap[]{bm1, bm2, bm3, bm4};
        paintArray = new Paint[]{new Paint(), new Paint(), new Paint(), new Paint()};
        leftMarginArray = new float[]{(1 *widthScreen / 4)
                ,  (3f * widthScreen / 4)
                ,  (1.2f *widthScreen / 4)
                ,  (3f *widthScreen / 4)};
        topMarginArray = new float[]{(1.2f *heightScreen / 8f)
        ,  2.5f * (heightScreen / 8f)
        ,  4f * (heightScreen / 8f)
        ,  5.2f * (heightScreen / 8f)};
        updateRects();
        target = getResources().getDrawable(R.drawable.img_sonar_target);
        bubble = getResources().getDrawable(R.drawable.img_sonar_info_bubble);
        icon = getResources().getDrawable(R.mipmap.ic_launcher);
        if (bubble != null) {
            bubbleWidth = bubble.getIntrinsicWidth();
            bubbleHeight = bubble.getIntrinsicHeight();
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId, null), reqWidth, reqHeight, false);
    }
    private Random randomGenerator = new Random();
    private int random = randomGenerator.nextInt(4);
    private static final int FADE_MILLISECONDS = 1000; // 3 second fade effect
    private static final int FADE_STEP = 100;          // 120ms refresh

    // Calculate our alpha step from our fade parameters
    private static final int ALPHA_STEP = 255 / (FADE_MILLISECONDS / FADE_STEP);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //super.onDraw(canvas); // no need to call super
        int count = 0;
        for (Bitmap bm : bmArray){
            Paint paint = paintArray[count];
            int alpha = paint.getAlpha();
            if (alpha < 255) {
                // Update your alpha by a step
                alpha += ALPHA_STEP;
                paint.setAlpha(alpha);
                //postInvalidateDelayed(FADE_STEP, (int) widthCell, (int)(heightCell * 2), (int) widthCell + smallIconDimen, (int)(heightCell * 2) + smallIconDimen);
            } else if (alpha != 255){
                alpha = 255;
                paint.setAlpha(alpha);
            }
            float leftMargin = leftMarginArray[count];
            leftMargin += 1f; ;
            float topMargin = topMarginArray[count];
            if (leftMargin + smallIconDimen /2f < widthScreen / 2){
                topMargin -= getYCurve();
            } else {
                topMargin += getYCurve();
            }
            //Log.d("TAG", "left: " + leftMargin + "  top:  " + top );
            leftMarginArray[count] = leftMargin;
            topMarginArray[count] = topMargin;
            if (leftMargin > widthScreen){
                leftMarginArray[count] = 0;
                if (count == random){ // need to move the target
                    random = randomGenerator.nextInt(4);
                }
                if (count == clicked){
                    ((MainActivity)getContext()).removeOfferBubble();
                    clicked = -1;
                }
            } else if (!(count == clicked)) {
                canvas.drawBitmap(bm, leftMargin, topMargin, paint);
            }

            count++;
        }
        updateRects();
        if (clicked == -1) {
            target.setBounds(rects[random]);
            target.draw(canvas);
        } else {
            ((MainActivity)getContext()).updateOfferBubbleUi(leftMarginArray[clicked], topMarginArray[clicked]);
            //drawBubble(canvas);
        }
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

    private void drawBubble(Canvas canvas) {
        bubble.setBounds((int)leftMarginArray[clicked] - (bubbleWidth / 2), (int)topMarginArray[clicked] - (bubbleHeight / 2), (int)leftMarginArray[clicked] + (bubbleWidth / 2), (int)topMarginArray[clicked] + (bubbleHeight / 2));
        bubble.draw(canvas);
        icon.setBounds((int)leftMarginArray[clicked] + (bubbleWidth / 2) - icon.getIntrinsicWidth(),(int)topMarginArray[clicked] + (bubbleHeight / 2) - icon.getIntrinsicHeight(),  (int)leftMarginArray[clicked] + (bubbleWidth / 2), (int)topMarginArray[clicked] + (bubbleHeight / 2));
        icon.draw(canvas);
    }


    private float getYCurve(){
        float totY = heightScreen / 9f;
        float step = (widthScreen / 2f) / 1f;
        return totY / step;
    }



    public void setOffset(float azimuth) {
        this.offset = azimuth * degreeFactor;
        left = (azimuth > 180);
        invalidate();

    }

    public float getOffset() {
        return this.offset;
    }

    public static int getDpInPixels(Context context, int dpValue) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * d); // margin in pixels
    }

    private void updateRects() {
        rects [0] = new Rect( (int)leftMarginArray[0] - smallIconDimen / 2,(int) topMarginArray[0] - smallIconDimen / 2,(int) (leftMarginArray[0] + 1.5f* smallIconDimen),(int) (topMarginArray[0] + 1.5f* smallIconDimen));
        rects [1] = new Rect( (int)leftMarginArray[1] - smallIconDimen / 2,(int) topMarginArray[1] - smallIconDimen / 2,(int) (leftMarginArray[1] + 1.5f* smallIconDimen),(int) (topMarginArray[1] + 1.5f* smallIconDimen));
        rects [2] = new Rect( (int)leftMarginArray[2] - smallIconDimen / 2,(int) topMarginArray[2] - smallIconDimen / 2,(int) (leftMarginArray[2] + 1.5f* smallIconDimen),(int) (topMarginArray[2] + 1.5f* smallIconDimen));
        rects [3] = new Rect( (int)leftMarginArray[3] - smallIconDimen / 2,(int) topMarginArray[3] - smallIconDimen / 2,(int) (leftMarginArray[3] + 1.5f* smallIconDimen),(int) (topMarginArray[3] + 1.5f* smallIconDimen));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            int count = 0;
            for (Rect rect : rects) {
                if (rect.contains((int) event.getX(), (int) event.getY())) {
                    Toast.makeText(getContext(), "rect number: " + count, Toast.LENGTH_SHORT).show();
                    clicked = count;
                    break;
                }
                count++;
            }
        }
        return super.onTouchEvent(event);
    }

}
