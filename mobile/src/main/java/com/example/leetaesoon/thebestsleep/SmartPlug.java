package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SmartPlug extends Activity {
    String uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_plug);

        MainPageTask mainPageTask = new MainPageTask();
        mainPageTask.execute();//메인에서 네트워크 연결시 오류가 난다. 따라서 백그라운드로 웹페이지에서 uuid를 가져왔다.

    }

    private class MainPageTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPostExecute(Void result)
        { //doInBackground 작업이 끝나고 난뒤의 작업
            Toast.makeText(SmartPlug.this,"uuid : "+uuid,Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params)
        { //백그라운드 작업이 진행되는 곳.
             try {
                 Document doc = null;
                 doc = Jsoup.connect("https://www.uuidgenerator.net/version4").get();
                 Elements mes = doc.select("h2");
                 for(Element me : mes)
                 {
                     uuid = me.text();
                     Log.d("uuid",uuid+"is Connected Svlistener");
                     break;
                 }
             }

             catch (IOException e) {
                 e.printStackTrace();
             }
             return null;
        }
    }

}
