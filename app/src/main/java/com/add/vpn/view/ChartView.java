package com.add.vpn.view;

import android.animation.FloatEvaluator;
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
    private final PointF prevPoint = new PointF();
    private final ArrayList<PointF> points = new ArrayList<>();
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    LinkedList<Float> dataValue;
    LinkedList<String> time;
    FloatEvaluator floatEvaluator = new FloatEvaluator();
    private LinkedList<Float> tempReportValue = new LinkedList<Float>() {{
        add(1f);
        add(3f);
        add(5f);
        add(2f);
        add(3f);
    }};
    private LinkedList<String> tempReportDate = new LinkedList<String>() {{
        add("1");
        add("3");
        add("5");
        add("7");
        add("9");
    }};
    private Integer valueMarker;
    private String timeUnit = "час";
    private String valueUnit = "kW";
    private String description = "Power Constant";
    private boolean isAvg = true;
    private boolean isAutoScale = true;

    public int getMaxSize() {
        return maxSize;
    }

    private int maxSize = 72;
//    private final Matrix matrix;
//    private final ScaleGestureDetector scaleGestureDetector;
//    private final GestureDetector gestureDetector;
//
//    private float scaleFactor = 1.0f;
//    private float focusX = 0f;
//    private float focusY = 0f;
//    private float lastX;
//    private float lastY;

    public ChartView(Context context) {
        super(context);
        //scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        matrix = new Matrix();
//        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
//        matrix = new Matrix();
//        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//        gestureDetector = new GestureDetector(context, new GestureListener());
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        boolean scaleHandled = scaleGestureDetector.onTouchEvent(event);
//        boolean scrollHandled = gestureDetector.onTouchEvent(event);
//        return scaleHandled || scrollHandled || super.onTouchEvent(event);
//        getParent().requestDisallowInterceptTouchEvent(true);
//    }

    public void setValueMarker(Integer valueMarker) {
        this.valueMarker = valueMarker;
    }

    public void setValueUnit(String valueUnit) {
        this.valueUnit = valueUnit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartView);
            try {CharSequence chars = a.getText(R.styleable.ChartView_timeUnit);
                timeUnit = chars != null ? chars.toString() : "Hour";
                chars = a.getText(R.styleable.ChartView_valueUnit);
                valueUnit = chars != null ? chars.toString() : "kW";
                chars = a.getText(R.styleable.ChartView_valueMarker);
                valueMarker = chars != null ? Integer.valueOf((String) chars) : null;
                chars = a.getText(R.styleable.ChartView_description);
                description = chars!= null ? chars.toString() : "Power Constant";
                isAvg = a.getBoolean(R.styleable.ChartView_isAvg,true);
                isAutoScale = a.getBoolean(R.styleable.ChartView_isAutoScale, true);
                //a.recycle();
            } finally {
                a.recycle();
            }
        }
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public LinkedList<Float> getDataValue() {
        return dataValue;
    }

    public LinkedList<String> getTime() {
        return time;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        paint.setColor(Color.WHITE);

        points.clear();
        trendLine.reset();
        float textPointX = 0;

        int height = getHeight();
        int width = getWidth();
        if (height >= width) {
            height = width / 2;
        }
        maxSize = 50 * (width / height);
        if (maxSize < 50) maxSize = 50;
        if (isAvg){
            dataValue = pruneList(tempReportValue, maxSize);
            time = pruneList(tempReportDate, maxSize);
        }else {
            dataValue = tempReportValue;
            time = tempReportDate;
            maxData(maxSize);
        }

        int offset = 0;
        int radius = (height / 140) + ((width / height)/4);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        float maxValue = 0;
        float minValue = 0;

        if (dataValue != null && !dataValue.isEmpty()){
             maxValue = Collections.max(dataValue);
             minValue = Collections.max(dataValue);
            for (Float aFloat : dataValue) {
                if (aFloat >= 0 && aFloat < minValue) minValue = aFloat;
            }

        }else {
            return;
        }

        paint.setTextSize((float) (height / 20) + ((float) width /height)); // Установите желаемый размер шрифта
        int rounded = Math.round(maxValue);
        float startPointX = paint.measureText(String.valueOf(rounded) + radius); // точка начала относительно значения
        float startPointUnitX = paint.measureText(String.valueOf(valueUnit) + radius);
        if (startPointX < startPointUnitX) startPointX = startPointUnitX;
        float endPointX = paint.measureText(String.valueOf(valueUnit) + 2*radius);

        float startPointY = paint.getFontMetrics().descent - paint.getFontMetrics().ascent + paint.getFontMetrics().bottom - paint.getFontMetrics().top;

        float descriptionX = startPointX + 2*radius;
        paint.setTextSize((float) height/8);
        paint.setColor(Color.GREEN);
        paint.setAlpha(90);
        canvas.drawText(description,descriptionX,startPointY - 3*radius,paint);
        paint.setAlpha(100); // рисуем название графика

        float timeScale = ((width - startPointX - endPointX) / dataValue.size());
        float deltaValue = (maxValue - minValue);
        if (maxValue - minValue == 0) deltaValue = 1;
       if (isAutoScale){
//        if (maxValue * 0.15 > deltaValue){
//            deltaValue *= 3;
//        }
//        if (deltaValue != 1){
//            deltaValue = deltaValue / ((deltaValue/maxValue));
//        }
           deltaValue *= floatEvaluator.evaluate(deltaValue / maxValue, 1f, 1f);
       }else {
           if (maxValue * 0.2f > deltaValue){
               deltaValue = maxValue * 0.2f;
           }
           //deltaValue = maxValue;
           //minValue /= 2;
           //minValue = minValue/floatEvaluator.evaluate(deltaValue / maxValue, 3, 1);
       }

        float valueScale = ((height - startPointX - startPointY - radius) / deltaValue);

        paint.setColor(Color.BLUE);

        //рисуем квадрат графика
        paintLine.setColor(Color.argb(0xaa, 0xaa, 0xaa, 0xaa));
        paintLine.setStrokeWidth((float) height / 300);
        paintLine.setStyle(Paint.Style.STROKE);
        float round = 5 * radius;
        float right = width - endPointX / 2 - startPointX / 2 + 3 * radius;
        canvas.drawRoundRect(startPointX - radius, (float) radius / 2, right, height - startPointX + 1.5f * radius, round, round, paintLine);

        for (int i = 0; i <= dataValue.size()-1; i++) {
            Float value = dataValue.get(i);
            float x = startPointX + timeScale * offset;
            float y = height - startPointX - valueScale * (value - minValue);

            // добавляем точки для последующей отрисовки
            if (value >= 0){  // если значение выходит за график то мы его не рисуем
                points.add(new PointF(x, y)); // Записываем точки для отрисовки точек
            }

            // Строим линию графика
            if (trendLine.isEmpty()) {
                trendLine.moveTo(x, y);
            } else if(value >= 0) {
                float control1X = (x + prevPoint.x) / 2;
                float control1Y = prevPoint.y;
                float control2X = (x + prevPoint.x) / 2;
                trendLine.cubicTo(control1X, control1Y, control2X, y, x, y);
            }

            // Рисуем текст (время) и вертикальные полосы графика
            try {
                String timeText = String.valueOf(time.get(i));
                paint.setTextSize((float) (height / 20) + ((float) width /height)); // Установите желаемый размер шрифта
                float space = paint.measureText("000");
                float textWith = paint.measureText(timeText);

                float textX = x - textWith / 2;
                float textY = height - (startPointX / 4);

                boolean isTimeEquals = true;
                if (i != 0){
                    isTimeEquals = !time.get(i).equals(time.get(i - 1));
                }
                if ((textPointX == 0 || x - textPointX > 1.5f * space) && x  < width - endPointX - radius - textWith && isTimeEquals) {
                    canvas.drawText(timeText, textX, textY, paint);
                    if (textPointX != 0 || x > startPointX + round) // первая черта не рисуется и не рисуется черта в закруглении рамки
                        canvas.drawLine(x, height - startPointX + 1.5f * radius, x, (float) radius / 2, paintLine);
                    textPointX =  x;
                }
            }catch (Exception ignored){
            }
            offset++;
            if (value >= 0) prevPoint.set(x, y);
        }

        // рисуем значения графика и линии на оси Y
        for (float i = deltaValue; i > 0.1f; i -= (deltaValue / 4)) {
            String valueText = String.valueOf(Math.round(i + minValue));
            float valueHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            //float valueWidth = paint.measureText(valueText);

            float valueY = height - startPointX - valueScale * (i);
            float valueX = valueHeight / 6;
            canvas.drawText(valueText, (float) radius /4, valueY + (valueHeight/4), paint);
            canvas.drawLine(startPointX - radius, valueY, right, valueY, paintLine);
            if (i == deltaValue){
                canvas.drawText(String.valueOf(Math.round(minValue)), valueX, height - startPointX + (valueHeight/4), paint);
            }
        }
        //рисуем маркер значение - Зеленую линию
        if (valueMarker != null && maxValue > valueMarker && valueMarker > minValue) {
            paintLine.setColor(Color.argb(250, 0, 255, 0));
            paintLine.setStrokeWidth((float) height / 50);
            canvas.drawLine(startPointX - radius, height - startPointX - valueScale * (valueMarker - minValue), right, height - startPointX - valueScale * (valueMarker - minValue), paintLine);
        }

        // единицы измерения величины и времени красный текст
        paint.setTextSize((float) height / 20 + ((float) width /height));
        paint.setColor(Color.RED);

        float textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        float timeUnitWidth = paint.measureText(timeUnit);
        canvas.drawText(valueUnit, textHeight / 6, textHeight * 0.8f, paint);
        canvas.drawText(timeUnit, (float) (width - 1.05 * timeUnitWidth), height - (startPointX / 4), paint);

        // рисуем точки на графике с тенью чтоб (точки позже перерисуются, но тень не наложится на линию тренда)
        paint.setShadowLayer((float) radius / 2, 2 * radius, 2 * radius, Color.GRAY);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, radius, paint);
        }
        // Рисуем линии тренда
        paint.setShadowLayer(radius, 2 * radius, 2 * radius, Color.GRAY);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius);
        canvas.drawPath(trendLine, paint);

        //Рисуем точки без тени. Тень осталась от предыдущей отрисовки точек
        paint.clearShadowLayer();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, radius, paint);
        }
        canvas.restore();

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
        //maxSize = 24 * (measuredWidth / measuredHeight);
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
        tempReportValue = dataValue;
        tempReportDate = time;
        invalidate();
    }
    private void maxData(int maxSize){
        if (dataValue.size() > maxSize){
            int deltaSize = dataValue.size() - maxSize;
            for (int i = 0; i < deltaSize; i++) {
                this.dataValue.removeFirst();
                this.time.removeFirst();
            }
        }
    }

    public <T> LinkedList<T> pruneList(LinkedList<T> floatValueList, int maxItems) {
        if (floatValueList.size() <= maxItems || floatValueList.isEmpty()) {
            return floatValueList; // Если список уже не больше maxItems, не требуется прореживание
        }
        int interval = floatValueList.size() / maxItems;

        LinkedList<T> prunedList = new LinkedList<>();
        int avg = 0;
        int count = 0;
        for (int i = 0; i < floatValueList.size(); i++) {
            if (floatValueList.get(i) instanceof Float && isAvg) {
                Number number = (Number) floatValueList.get(i);
                avg += (int) number.floatValue();
                count++;
                if (i % interval == 0) {
                    float averageValue = count > 0 ? (float) avg / count : 0.0f;
                    prunedList.add((T) (Number) averageValue);
                    avg = 0;
                    count = 0;
                }
            } else {
                if (i % interval == 0) {
                    prunedList.add(floatValueList.get(i));
                }
            }
        }
        return prunedList;
    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
//            matrix.setScale(scaleFactor, scaleFactor, focusX, focusY);
//            invalidate();
//            return false;
//        }
//    }
//
//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            float targetScale = (scaleFactor == MAX_SCALE) ? MIN_SCALE : MAX_SCALE;
//            focusX = e.getX();
//            focusY = e.getY();
//            matrix.setScale(targetScale, targetScale, focusX, focusY);
//            scaleFactor = targetScale;
//            invalidate();
//            return true;
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            lastX = e.getX();
//            lastY = e.getY();
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            // Рассчитайте смещение
//            float dx = e2.getX() - lastX;
//            float dy = e2.getY() - lastY;
//
//            // Примените смещение к матрице
//            matrix.postTranslate(dx, dy);
//            invalidate();
//
//            // Сохраните текущие координаты
//            lastX = e2.getX();
//            lastY = e2.getY();
//
//            return true;
//        }
//
//    }
}
