package com.add.vpn.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import java.util.*;

public class ChartView extends View {
    private Map<Float,Float> reportList;
    private final Path trendLine = new Path();
    private final Paint paint =  new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF prevPoint = new PointF();
    private int textPointX = 0;
    private int valuePointY = 0;
    private final ArrayList<PointF> points = new ArrayList<>();
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient linearGradient = new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
            Color.rgb(0x95, 0x95, 0x95),
            Color.rgb(0xb0, 0xb5, 0xb0),
            Shader.TileMode.CLAMP);

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);

        points.clear();
        trendLine.reset();
        textPointX = 0;

        int height = getHeight();
        int width = getWidth();
        if (height >= width) {
            height = width / 2;
        }

        canvas.drawRect(0, 0, width, height, paint);


        int offset = 1;
        int radius = height / 80;
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        reportList = new TreeMap<>();
        for (int i = 0; i < 25; i++) {
            reportList.put((float) i, (float) (100 * ( i * Math.random())));
        }

        float maxTime = Collections.max(reportList.keySet());
        float maxValue = Collections.max(reportList.values());

        float startPoint = (height - (height * 0.87f));
        float timeScale = ((width - 3 * startPoint)/ maxTime);
        float valueScale = ((height - 2 * startPoint) / maxValue);

        //рисуем оси графика
        paintLine.setShader(linearGradient);
        paintLine.setStrokeWidth((float) height /100);
        paintLine.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startPoint + timeScale - 1.5f*radius,height - startPoint + 1.5f*radius, startPoint + timeScale - 1.5f*radius, 0, paintLine);
        canvas.drawLine(startPoint + timeScale - 1.5f*radius,height - startPoint + 1.5f*radius, width - startPoint/3, height - startPoint + 1.5f*radius, paintLine);
        canvas.drawLine(width - startPoint/3, height - startPoint + 1.5f*radius,width - startPoint/3, 0,paintLine);

        for (Map.Entry<Float, Float> entry : reportList.entrySet()) {
            Float time = entry.getKey();
            Float value = entry.getValue();
            float x = startPoint + timeScale * offset;
            float y = height - startPoint - valueScale * value;

            // Рисуем кружок
            //canvas.drawCircle(x, y, radius, paint);
            points.add(new PointF(x,y)); // Записываем точки для отрисовки точек

            // Строим линию
            if (trendLine.isEmpty()) {
                trendLine.moveTo(x, y);
            } else {
                float control1X = (x + prevPoint.x) / 2;
                float control1Y = prevPoint.y;
                float control2X = (x + prevPoint.x) / 2;
                float control2Y = y;
                trendLine.cubicTo(control1X, control1Y, control2X, control2Y, x, y);

            }

            // Рисуем текст (время)
            String timeText = String.valueOf(time);
            paint.setTextSize((float) height /20); // Установите желаемый размер шрифта
            float textWidth = paint.measureText(timeText);

            //float textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            float textX = x - textWidth;
            float textY = height  - (startPoint /4);
            if (textPointX == 0 || x - textPointX > 2 * textWidth){
                canvas.drawText(timeText, textX, textY , paint);
                if(textPointX != 0)canvas.drawLine(textX + textWidth/2,height - startPoint + 1.5f*radius, textX + textWidth/2, 0,paintLine);
                textPointX = (int) x;
            }


            offset++;
            prevPoint.set(x, y);
        }
        // рисуем график по оси Y
        for (float i = maxValue; i > 0.1; i-= (maxValue/4)) {
            String valueText = String.valueOf(((float) (Math.round(i * 10))/10));
            float valueHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            float valueWidth = paint.measureText(valueText);

            float valueY = height - startPoint - valueScale * i;
            float valueX = valueHeight/6;
            canvas.drawText(valueText,valueX,valueY,paint);
            canvas.drawLine(startPoint + timeScale - 1.5f*radius,valueY,width - startPoint/3,valueY,paintLine);
        }

        // Рисуем линии тренда
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        canvas.drawPath(trendLine, paint);

        //Рисуем точки
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y,radius,paint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void background(Canvas canvas){

    }
}
