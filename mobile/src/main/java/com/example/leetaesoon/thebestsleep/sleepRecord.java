package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class sleepRecord extends Activity {
    DBHandler dbHandler;
    LineChart lineChart;
    LineData lineData;
    List<Entry> entries;
    List<Entry> e;
    SimpleDateFormat simpleDateFormat;
    ArrayList<Gyro> gyrolist;
    ArrayList<Acceleration> acclist;
    ArrayList<HeartRate> heartlist;

    float x_axis;
    int list_index = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);
        simpleDateFormat = new SimpleDateFormat("HHmmss");
        x_axis = 0;
        lineChart = (LineChart)findViewById(R.id.chart1);

        entries = new ArrayList<>();
        dbHandler = MainActivity.dbHandler;
        init();
        //readFile();
        drawGraph();
        Log.v("testing","aaaaaaaaaaaaaaaaaaaaaa");
    }
    public void init(){
        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);//DBHander 생성
        gyrolist = new ArrayList<>();//db에 있는 Gyro 정보를 모두 가져오기 위한 Gyro List
        acclist = new ArrayList<>();
        heartlist = new ArrayList<>();
        float x = 0,y = 0,z = 0;
        double sum = 0;
        int h_rate = 0;
        if(dbHandler.getAccelerationDB() != null)//gyroDB에 저장된 것이 있을 때
        {
            acclist.addAll(dbHandler.getAccelerationDB());
            Log.d("Ex_DB", " db 존재");
            for(Acceleration ac : acclist)//list에 있는 정보를 하나씩 log로 볼 수 있음.
            {
                x = (float)ac.getAccelerationX();
                y = (float)ac.getAccelerationY();
                z = (float)ac.getAccelerationZ();
                sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                entries.add(new Entry(Float.parseFloat(ac.getAccelerationTime()),(float) sum));
            }
        }
        else{
            Log.v("No_db","db생성하기위해 읽어오기");
            readFile();
        }
        //여기까지 DB.
    }
    public void drawGraph() {
//        if(list_index == entries.size()-1){
//            Toast.makeText(getApplicationContext(),"데이터가 없습니다",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (list_index < entries.size() - 5000){
//            e = new ArrayList<>(entries.subList(list_index, list_index + 5000));
//            Log.v("list_index",""+list_index+" && "+list_index+5000 );
//            list_index += 5000;
//        }
//        else {
//            e = new ArrayList<>(entries.subList(list_index, entries.size() - 1));
//            list_index = entries.size() -1;
//        }
        LineDataSet lineDataSet = new LineDataSet(entries, "Number");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(0);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.RED);
        xAxis.enableGridDashedLine(8, 48, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.RED);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);

        lineChart.invalidate();
    }
    public void readFile(){
        float x=0,y=0,z=0;
        double sum = 0;
        int rate=0;
        float dateText=0;
        Date d_t;
        Scanner scan = new Scanner(
                getResources().openRawResource(R.raw.sensor)
        );
        while(scan.hasNextLine()){
            String str1 = scan.nextLine();
            String[] s = str1.split(" ");


            if(s.length > 1) {
                String t = s[0].replace(":","");
                Log.v("tt:",t);
                dateText = Float.parseFloat(t.toString());
                switch (s[1]) {
                    case "Accelerometer":
                        x = Float.parseFloat(s[3]);
                        y = Float.parseFloat(s[4]);
                        z = Float.parseFloat(s[5]);
                        sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        Acceleration acc = new Acceleration(t,x,y,z);
                        dbHandler.addAcceleration(acc);
                        //entries.add(new Entry(dateText,(float) sum));
                        break;
                    case "Gyroscope" :
                        x = Float.parseFloat(s[3]);
                        y = Float.parseFloat(s[4]);
                        z = Float.parseFloat(s[5]);
                        sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        Gyro gyro = new Gyro(t,x,y,z);
                        dbHandler.addGyro(gyro);
                        break;
                    case "HeartRate" :
                        rate = Integer.parseInt(s[3]);
                        HeartRate heartRate = new HeartRate(rate,t);
                        dbHandler.addHeartRate(heartRate);
                        break;
                }
            }
        }
        scan.close();
    }

    public void refresh(View view) {
        drawGraph();
    }
}
