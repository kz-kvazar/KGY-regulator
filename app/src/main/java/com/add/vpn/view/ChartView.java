package com.add.vpn.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.add.vpn.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class ChartView extends View {
    private final Path trendLine = new Path();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF prevPoint = new PointF();
    private final ArrayList<PointF> points = new ArrayList<>();
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinkedList<Float> reportValue = new LinkedList<Float>() {{
        add(1f);
        add(3f);
        add(5f);
        add(2f);
        add(3f);
    }};
    private LinkedList<String> reportDate = new LinkedList<String>() {{
        add("1");
        add("3");
        add("5");
        add("7");
        add("9");
    }};
    private Integer valueMarker;
    private String timeUnit = "час";
    private String valueUnit = "kW";

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            try {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartView);
                CharSequence chars = a.getText(R.styleable.ChartView_timeUnit);
                timeUnit = chars != null ? chars.toString() : "Hour";
                chars = a.getText(R.styleable.ChartView_valueUnit);
                valueUnit = chars != null ? chars.toString() : "kW";
                chars = a.getText(R.styleable.ChartView_valueMarker);
                valueMarker = chars != null ? Integer.valueOf((String) chars) : null;
                a.recycle();
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);

        points.clear();
        trendLine.reset();
        int textPointX = 0;

        int height = getHeight();
        int width = getWidth();
        if (height >= width) {
            height = width / 2;
        }

        //canvas.drawRect(0, 0, width, height, paint);


        int offset = 0;
        int radius = height / 80;
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        if (reportValue == null || reportValue.isEmpty()) return;

        float maxValue = Collections.max(reportValue);
        //float maxTime = Collections.max(reportDate);

        float startPoint = (height - (height * 0.85f));
        float timeScale = ((width - 1.6f * startPoint) / reportValue.size());
        float valueScale = ((height - 2f * startPoint) / maxValue);

        //рисуем оси графика
        paintLine.setColor(Color.argb(0xaa, 0xaa, 0xaa, 0xaa));
        paintLine.setStrokeWidth((float) height / 300);
        paintLine.setStyle(Paint.Style.STROKE);
        float round = 5 * radius;
        canvas.drawRoundRect(startPoint - radius, (float) radius / 2, width - startPoint / 3, height - startPoint + 1.5f * radius, round, round, paintLine);


        for (int i = 0, reportValueSize = reportValue.size(); i < reportValueSize; i++) {
            Float value = reportValue.get(i);
//            Float time = entry.getKey();
//            Float value = entry.getValue();
            float x;
            if (i == 0){
                x =  startPoint;
            }else {
                x = startPoint + timeScale * offset;
            }
            float y = height - startPoint - valueScale * value;

            // Рисуем кружок
            //canvas.drawCircle(x, y, radius, paint);
            points.add(new PointF(x, y)); // Записываем точки для отрисовки точек

            // Строим линию
            if (trendLine.isEmpty()) {
                trendLine.moveTo(x, y);
            } else {
                float control1X = (x + prevPoint.x) / 2;
                float control1Y = prevPoint.y;
                float control2X = (x + prevPoint.x) / 2;
                trendLine.cubicTo(control1X, control1Y, control2X, y, x, y);
            }

            // Рисуем текст (время)
            String timeText = String.valueOf(reportDate.get(i));
            paint.setTextSize((float) height / 20); // Установите желаемый размер шрифта
            float space = paint.measureText("00");
            float textWith = paint.measureText(timeText);

            //float textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            float textX = x - textWith/2;
            float textY = height - (startPoint / 4);
            if (textPointX == 0 || x - textPointX > 1.5f * space) {
                canvas.drawText(timeText, textX, textY, paint);
                if (textPointX != 0)
                    //canvas.drawLine(textX + space / 2, height - startPoint + 1.5f * radius, textX + space / 2, (float) radius / 2, paintLine);
                    canvas.drawLine(x, height - startPoint + 1.5f * radius, x, (float) radius / 2, paintLine);
                textPointX = (int) x;
            }


            offset++;
            prevPoint.set(x, y);
        }

        // рисуем график на оси Y
        for (float i = maxValue; i > 0.1; i -= (maxValue / 4)) {
            String valueText = String.valueOf(Math.round(i));
            float valueHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            //float valueWidth = paint.measureText(valueText);

            float valueY = height - startPoint - valueScale * i;
            float valueX = valueHeight / 6;
            canvas.drawText(valueText, valueX, valueY + valueHeight / 4, paint);
            canvas.drawLine(startPoint - radius, valueY, width - startPoint / 3, valueY, paintLine);
        }
        //рисуем маркер значение
        if (valueMarker != null && maxValue > valueMarker){
            paintLine.setColor(Color.argb(100,0, 255, 0));
            paintLine.setStrokeWidth((float) height / 50);
            canvas.drawLine(startPoint - radius, height - startPoint - valueScale * valueMarker,width - startPoint / 3,height - startPoint - valueScale * valueMarker,paintLine);
        }
        paint.setTextSize((float) height / 14);
        paint.setColor(Color.RED);

        float textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        //float valueUnitWidth = paint.measureText(valueUnit);
        float timeUnitWidth = paint.measureText(timeUnit);
        canvas.drawText(valueUnit, textHeight / 6, textHeight * 0.8f, paint);
        canvas.drawText(timeUnit, (float) (width - 1.2 * timeUnitWidth), height - (startPoint / 4), paint);

        // Рисуем линии тренда
        paint.setShadowLayer(radius, radius, radius, Color.GRAY);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius);
        canvas.drawPath(trendLine, paint);

        //Рисуем точки
        paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, radius, paint);
        }

    }

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

        if (measuredHeight >= measuredWidth) {
            measuredHeight = measuredWidth / 2;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        } else if (mode == MeasureSpec.AT_MOST) {
            return size;
        } else {
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 600;
    }

    public void setData(LinkedList<Float> dataValue, LinkedList<String> time) {
        reportValue = dataValue;
        reportDate = time;
        invalidate();
    }
}
