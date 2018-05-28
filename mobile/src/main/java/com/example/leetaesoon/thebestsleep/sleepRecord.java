package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class sleepRecord extends Activity {
    final static String TAG = "sleepRecord";

    DBHandler dbHandler;

    LineChart lineChart;
    LineData lineData;

    List<Entry> accelerometerEntries;
    List<Entry> heartRateEntries;

    ArrayList<Acceleration> accList;
    ArrayList<HeartRate> heartList;

    long timeBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);
        lineChart = (LineChart)findViewById(R.id.chart1);

        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);
        heartRateEntries = new ArrayList<>();
        accelerometerEntries = new ArrayList<>();

        init();
        drawGraph();

        Log.v(TAG,"finish onCreate");
    }

    public void init() {
        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);//DBHander 생성
        heartList = new ArrayList<>();
        accList = new ArrayList<>();

        long accelerometerBase = Long.MAX_VALUE;
        long heartRateBase = Long.MAX_VALUE;

        // TODO : DB 는 기본적으로 존재, 데이터가 없으면 안그려지도록, 12 ~ 12 시 데이터만 유지, 파일 읽는 과정이 아예 없어야함

        ArrayList<Acceleration> accelerations = dbHandler.getAccelerationDBMinMax();
        if (accelerations != null) Log.d(TAG, "exist Accelerometer DB");
        else {
            Log.v(TAG,"가속도 db 생성하기위해 읽어오기");
            readFile();
            accelerations = dbHandler.getAccelerationDBMinMax();
        }
        accList.addAll(accelerations);

        ArrayList<HeartRate> heartRates = dbHandler.getHeartRateDB();
        if (heartRates != null) Log.d(TAG, "exist heart rate DB");
        else {
            Log.v("sleepRecord","심박수 db 생성하기위해 읽어오기");
            readFile();
        }
        heartList.addAll(heartRates);

        accelerometerBase = accList.get(0).getAccelerationTime();
        heartRateBase = heartList.get(0).getHeartRatetime();
        timeBase = Math.min(accelerometerBase, heartRateBase);

        long pre = 0;
        if(accelerations != null) {
            for(Acceleration ac : accList) {
                long time = ac.getAccelerationTime();
                long diff = (time - pre) / 60000L;
                //Log.d(TAG, "pre : " + pre + ", time : " + time);
                //Log.d(TAG, "diff : " + diff + " min");
                if (diff > 30) pre = time;           // first time
                else if (diff > 5) {
                    if (isSleep(time)) turnOffDevices(time);
                    pre = time;
                }
                accelerometerEntries.add(new Entry(ac.getAccelerationTime() - timeBase, (float) ac.getAccelerationSCALAR()));
            }
        }

        if(heartRates != null) {
            for(HeartRate hr : heartList) {
                int rate = hr.getHeartRateRate();
                heartRateEntries.add(new Entry(hr.getHeartRatetime() - timeBase, rate));
            }
        }
    }

    public void drawGraph() {
        LineDataSet lineDataSet = new LineDataSet(accelerometerEntries, "Accelerometer");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineDataSet heartRateLineDataSet = new LineDataSet(heartRateEntries, "Heart Rate");
        heartRateLineDataSet.setLineWidth(2);
        heartRateLineDataSet.setCircleColor(Color.RED);
        heartRateLineDataSet.setCircleColorHole(Color.RED);
        heartRateLineDataSet.setColor(Color.RED);
        heartRateLineDataSet.setDrawHorizontalHighlightIndicator(false);
        heartRateLineDataSet.setDrawHighlightIndicators(false);
        heartRateLineDataSet.setDrawValues(false);

        lineDataSet.setAxisDependency(lineChart.getAxisLeft().getAxisDependency());
        heartRateLineDataSet.setAxisDependency(lineChart.getAxisRight().getAxisDependency());

        lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        lineData.addDataSet(heartRateLineDataSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DayAxisValueFormatter(lineChart));
        xAxis.setTextColor(Color.RED);
        xAxis.enableGridDashedLine(8, 48, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.parseColor("#FFA1B4DC"));

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setTextColor(Color.RED);
        yRAxis.setAxisMinimum(-10);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(true);
        lineChart.setDescription(description);
        //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);

        lineChart.invalidate();
    }

    public void readFile() {
        float x=0,y=0,z=0;
        Scanner scan = new Scanner(
                getResources().openRawResource(R.raw.sensor)
        );

        ArrayList<HeartRate> heartRates = new ArrayList<HeartRate>();
        ArrayList<Acceleration> accelerations = new ArrayList<Acceleration>();

        while(scan.hasNextLine()){
            String str1 = scan.nextLine();
            String[] s = str1.split(" ");

            if(s.length > 1) {
                long t = Long.parseLong(s[0]);

                switch (s[1]) {
                    case "HeartRate":
                        int data = Integer.parseInt(s[3]);
                        HeartRate heartRate = new HeartRate(data, t);
                        heartRates.add(heartRate);
                        break;

                    case "Accelerometer":
                        x = Float.parseFloat(s[3]);
                        y = Float.parseFloat(s[4]);
                        z = Float.parseFloat(s[5]);
                        double sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        Acceleration acc = new Acceleration(t,x,y,z,sum);
                        accelerations.add(acc);
                        break;
                }
            }
        }

        Log.d(TAG, "Finish read flies and start save in db");
        dbHandler.addAllHeartRate(heartRates);
        dbHandler.addAllAcceleration(accelerations);
        scan.close();
    }

    private boolean isSleep(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "called isSleep");
        long windowStart = time - 1800000;      // 30 min before
        Log.d(TAG, "start time : " + simpleDateFormat.format(windowStart) + ", end : " + simpleDateFormat.format(time));
        if (getAvgAcc(windowStart, time) < 0.002 && getAvgHeartRate(windowStart, time) < 62) return true;
        else return false;
    }

    private double getAvgAcc(long start, long end) {
        double d = dbHandler.getAccelerationDBAvg(start, end);
        Log.d(TAG, "avg acc : " + d);
        return d;
    }

    private int getAvgHeartRate(long start, long end) {
        int r = dbHandler.getHeartRateDBAvg(start, end);
        Log.d(TAG, "avg heart : " + r);
        return r;
    }

    private void turnOffDevices(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "turn off at " + simpleDateFormat.format(time));
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile("sleep time.txt", simpleDateFormat.format(time) + "\n");
    }

    public void refresh(View view) {
        drawGraph();
    }

    class DayAxisValueFormatter implements IAxisValueFormatter
    {
        private BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            return simpleDateFormat.format((long) value + timeBase);
        }
    }
}
