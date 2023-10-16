package com.add.vpn.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;

import android.os.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Nullable;
import com.add.vpn.R;

public class AnalogView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] colors = {Color.GREEN, Color.YELLOW, Color.RED, Color.GREEN};
    private final float[] positions = {0f, 0.55f,0.58f, 0.8f};
    private final SweepGradient gradient = new SweepGradient(0, 0, colors, positions);
    private final RectF oval = new RectF(-1, -1, 1, 1);
    private final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private LinearGradient rimGradient = new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
            Color.rgb(0x95, 0x95, 0x95),
            Color.rgb(0xb0, 0xb5, 0xb0),
            Shader.TileMode.CLAMP);
    private Paint rimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint  rimCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int maxValue = 20;
    private float value = 0;
    private String text = "kW";
    private int colorBackground = Color.DKGRAY;
    private int textColor = Color.WHITE;
    private int markRange = 2;
    private int markRangeText = 1;
    private int markRangeLong = 10;
    private boolean isInteger = false;

    public AnalogView(Context context) {
        super(context);

    }
    public AnalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            try {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogView);
                CharSequence chars = a.getText(R.styleable.AnalogView_android_text);
                text = chars != null ? chars.toString() : "km/h";

                maxValue = a.getInt(R.styleable.AnalogView_maxValue, 1700);
                value = a.getInt(R.styleable.AnalogView_value, 800);
                markRange = a.getInt(R.styleable.AnalogView_markRange, 20);
                markRangeText = a.getInt(R.styleable.AnalogView_markRangeText, 200);
                markRangeLong = a.getInt(R.styleable.AnalogView_markRangeLong, 100);
                colorBackground = a.getColor(R.styleable.AnalogView_colorBackground, Color.DKGRAY);
                textColor = a.getColor(R.styleable.AnalogView_textColor, Color.WHITE);
                isInteger = a.getBoolean(R.styleable.AnalogView_isInteger, false);
                a.recycle();
            } catch (Exception ignored) {

            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();


        rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        rimPaint.setShader(rimGradient);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(0.07f);


        rimCirclePaint.setAntiAlias(true);
        rimCirclePaint.setStyle(Paint.Style.STROKE);
        rimCirclePaint.setColor(Color.argb(0x5f, 0x33, 0x36, 0x33));
        rimCirclePaint.setStrokeWidth(0.07f);

        float width = getWidth();
        float height = getHeight();

        // Посчитаем масштабы для правильного отображения вне зависимости от ориентации
        float scaleX = 0.5f * width;
        float scaleY = -0.5f * height;

        // Используем минимальное значение из scaleX и scaleY, чтобы сохранить круг
        float minScale = Math.min(scaleX, scaleY);

        // Определяем центр круга
        float centerX = 0.5f * width;
        float centerY = 0.5f * height;

        canvas.translate(centerX, centerY); // Переносим центр круга
        canvas.scale(minScale, minScale);

        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(0);

        // Устанавливаем градиентный шейдер для круга
        circlePaint.setShader(gradient);
        circlePaint.setXfermode(porterDuffXfermode);

        // Рисуем верхние 270 градусов с градиентом
        canvas.drawArc(oval, -45, 270, true, circlePaint);

        // Возвращаем обычный режим рисования
        circlePaint.setXfermode(null);

        circlePaint.setColor(colorBackground);
        paint.setColor(colorBackground);

        canvas.drawArc(oval, -135, 90, true, paint);

        paint.setColor(colorBackground);
        canvas.drawCircle(0, 0, 0.75f, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.02f);
        canvas.drawCircle(0,0,0.965f,rimPaint);
        canvas.drawCircle(0,0,0.935f,rimCirclePaint);


        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0.01f);

        float scale = 0.85f;
        float longScale = 0.9f;
        float textPadding = 0.8f;

        double step = 1.5 * Math.PI / maxValue; // Изменено значение шага
        for (int i = 0; i <= maxValue; i += markRange) {
            float x1 = (float) Math.cos(Math.PI * 1.25 - step * i); // Изменено на полный круг
            float y1 = (float) Math.sin(Math.PI * 1.25 - step * i); // Изменено на полный круг
            float x2;
            float y2;
            if (i % markRangeLong == 0) {
                x2 = x1 * scale * longScale;
                y2 = y1 * scale * longScale;
            } else {
                x2 = x1 * scale;
                y2 = y1 * scale;
            }
            canvas.drawLine(x1 * 0.92f, y1 * 0.92f, x2, y2, paint);
        }


        canvas.restore();

        canvas.save();

        canvas.translate(width / 2,  0);
        canvas.scale(0.5f,0.5f);


        paint.setTextSize(height / 10);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);

        float factor = height * scale * longScale * textPadding;

        for (int i = 0; i <= maxValue; i += markRangeText) {
            float x = (float) Math.cos(Math.PI * 1.25 - step*i) * factor;
            float y = (float) Math.sin(Math.PI * 1.25 - step*i) * factor;
            String text = Integer.toString(i);
            int textLen = Math.round(paint.measureText(text));
            canvas.drawText(Integer.toString(i), x  - (float) textLen / 2, height - y, paint);
        }
        paint.setTextSize(height/3);

        String valueText = isInteger ? String.valueOf((int) Math.round(value * 10) / 10) : String.valueOf((float) Math.round(value * 10) / 10);
        canvas.drawText(valueText, -paint.measureText(valueText) /2 , height + height * 0.65f, paint);
        paint.setTextSize(height/5);
        canvas.drawText(String.valueOf(text), -paint.measureText(String.valueOf(text)) /2 , height + height * 0.85f, paint);

        canvas.restore();

        canvas.save();

        canvas.translate(width / 2, height / 2);
        float min = Math.min(width, height);
        canvas.scale(0.5f * min, -0.5f * min);
        canvas.rotate(135 - (float) 270 * (value / (float) maxValue));

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0.02f);
        canvas.drawLine(0.00f, 0, 0, 0.88f, paint);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(0.03f);
        canvas.drawLine(0.02f, 0, 0, 0.88f, paint);
        canvas.drawLine(-0.02f, 0, 0, 0.88f, paint);
        rimCirclePaint.setStrokeWidth(0.03f);
        canvas.drawLine(0.04f, 0f, 0.03f,0.86f, rimCirclePaint);

        paint.setStyle(Paint.Style.FILL);
        //paint.setColor(0xff88ff99);
        canvas.drawCircle(0f, 0f, .1f, paint);

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int desiredWidth = chosenWidth + getPaddingLeft() + getPaddingRight();
        int desiredHeight = chosenHeight + getPaddingTop() + getPaddingBottom();

        // Учитываем ограничения, заданные родительским контейнером
        int measuredWidth = resolveSize(desiredWidth, widthMeasureSpec);
        int measuredHeight = resolveSize(desiredHeight, heightMeasureSpec);

        int chosenDimension = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        } else if (mode == MeasureSpec.AT_MOST) {
            return size / 2;
        } else {
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (value > maxValue) {
            value = maxValue;
        }
        invalidate();
    }

    private void setValue(float value) {
        this.value = Math.min(value, maxValue);
        invalidate();
        //setValueAnimated(value);
    }

    ObjectAnimator objectAnimator;
    public void setValueAnimated(float value) {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofFloat(this, "value", this.value, value);
        //objectAnimator.setDuration(100 + Math.abs(this.value - value) * 5L);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                float newValue = getTouchValue(event.getX(), event.getY());
//                setValueAnimated(newValue);
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                return true;
//            case MotionEvent.ACTION_UP:
//                return true;
//            default:
//                return super.onTouchEvent(event);
//        }
//
//    }

    private float getTouchValue(float x, float y) {
        if (x != 0 && y != 0) {
            float startX = (float) getWidth() / 2;
            float startY = getHeight();

            float dirX = startX - x;
            float dirY = startY - y;

            float angle = (float) Math.acos(dirX / Math.sqrt(dirX * dirX + dirY * dirY));

            return (float) Math.round((maxValue * (angle / (float) Math.PI)) * 10) /10;
        } else {
            return value;
        }
    }
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parentState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parentState);
        savedState.value = value;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setValue(savedState.value);
    }

    private static class SavedState extends BaseSavedState {

        float value;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}