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

    LineChart lineChart;
    LineData lineData;
    List<Entry> entries;
    List<Entry> e;
    ArrayList<String> labels;
    SimpleDateFormat simpleDateFormat;
    float x_axis;
    int list_index = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);
        simpleDateFormat = new SimpleDateFormat("HHmmss");
        x_axis = 0;
        lineChart = (LineChart)findViewById(R.id.chart1);
        Random random = new Random();
        entries = new ArrayList<>();
        readFile();
        drawGraph();
        Log.v("testing","aaaaaaaaaaaaaaaaaaaaaa");
    }
    public void drawGraph() {
        if(list_index == entries.size()-1){
            Toast.makeText(getApplicationContext(),"데이터가 없습니다",Toast.LENGTH_SHORT).show();
            return;
        }
        if (list_index < entries.size() - 5000){
            e = new ArrayList<>(entries.subList(list_index, list_index + 5000));
            list_index += 5000;
        }
        else {
            e = new ArrayList<>(entries.subList(list_index, entries.size() - 1));
            list_index = entries.size() -1;
        }
        labels = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(e, "Number");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
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

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(true);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
    }
    public void readFile(){
        float x=0,y=0,z=0;
        double sum = 0;
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
                Log.v("s_length",""+s.length);
                switch (s[1]) {
                    case "Accelerometer":
                        x = Float.parseFloat(s[3]);
                        y = Float.parseFloat(s[4]);
                        z = Float.parseFloat(s[5]);
                        sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        entries.add(new Entry(dateText,(float) sum));
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
