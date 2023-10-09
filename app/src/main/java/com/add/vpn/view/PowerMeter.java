package com.add.vpn.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.*;

import android.os.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Nullable;

public class PowerMeter extends View {
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
    private Paint rimPaint = new Paint();
    private Paint  rimCirclePaint = new Paint();

    private int maxValue = 1700;
    private int value = 1700;
    private String text = "kW";
    private int colorBackground = Color.DKGRAY;
    private int textColor = Color.WHITE;
    private int markRange = 200;

    public PowerMeter(Context context) {
        super(context);
    }
    public PowerMeter(Context context, AttributeSet attrs) {
        super(context, attrs);

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

        circlePaint.setStyle(Paint.Style.FILL);


        // Устанавливаем градиентный шейдер для круга
        circlePaint.setShader(gradient);
        paint.setXfermode(porterDuffXfermode);

        // Рисуем верхние 270 градусов с градиентом
        canvas.drawArc(oval, -45, 270, true, circlePaint);

        // Возвращаем обычный режим рисования
        paint.setXfermode(null);

        paint.setColor(colorBackground);

        canvas.drawArc(oval, -135, 90, true, paint);


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
        for (int i = 0; i <= maxValue; i += markRange/10) {
            float x1 = (float) Math.cos(Math.PI * 1.25 - step * i); // Изменено на полный круг
            float y1 = (float) Math.sin(Math.PI * 1.25 - step * i); // Изменено на полный круг
            float x2;
            float y2;
            if (i % (markRange/2) == 0) {
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

        for (int i = 0; i <= maxValue; i += markRange) {
            float x = (float) Math.cos(Math.PI * 1.25 - step*i) * factor;
            float y = (float) Math.sin(Math.PI * 1.25 - step*i) * factor;
            String text = Integer.toString(i);
            int textLen = Math.round(paint.measureText(text));
            canvas.drawText(Integer.toString(i), x  - (float) textLen / 2, height - y, paint);
        }
        paint.setTextSize(height/3);
        canvas.drawText(String.valueOf(value), -paint.measureText(String.valueOf(value)) /2 , height + height * 0.65f, paint);
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

    private void setValue(int value) {
        this.value = Math.min(value, maxValue);
        invalidate();
        //setValueAnimated(value);
    }

    ObjectAnimator objectAnimator;
    public void setValueAnimated(int value) {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        objectAnimator = ObjectAnimator.ofInt(this, "value", this.value, value);
        objectAnimator.setDuration(100 + Math.abs(this.value - value) * 5L);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int newValue = getTouchValue(event.getX(), event.getY());
                setValueAnimated(newValue);
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            default:
                return super.onTouchEvent(event);
        }

    }

    private int getTouchValue(float x, float y) {
        if (x != 0 && y != 0) {
            float startX = (float) getWidth() / 2;
            float startY = getHeight();

            float dirX = startX - x;
            float dirY = startY - y;

            float angle = (float) Math.acos(dirX / Math.sqrt(dirX * dirX + dirY * dirY));

            return Math.round(maxValue * (angle / (float) Math.PI));
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

        int value;

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
            out.writeInt(value);
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