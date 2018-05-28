package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class PlugList extends Activity {
    ListView listView;
    List<PlugItem> plugItems;
    PlugAdapter plugAdapter;
    TextView user_id;
    DBHandler dbHandler;
    String user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_list);
        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        user_email = intent.getStringExtra("email");
        user_id = (TextView)findViewById(R.id.user_id);
        user_id.setText(user_email +" 님");

        listView = (ListView)findViewById(R.id.listview);
        plugItems = new ArrayList<>();

        GetDeviceInfo getDeviceInfo = new GetDeviceInfo();
        try {
            getDeviceInfo.execute().get();//device 정보들을 가져온다.
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        plugAdapter = new PlugAdapter(this, R.layout.pluglistlayout, plugItems);
        listView.setAdapter(plugAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PlugItem p = (PlugItem) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(),"Click" + p.getdeviceId(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void KasaLogout(View view) {
        dbHandler.deletePlugUser(user_email);
        //device 설정에 대한 정보는 남겨두자.(

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",false);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {//뒤로가기 눌렀을 때.(메인화면으로 가야함.)
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",true);
        setResult(RESULT_OK,returnIntent);
        super.onBackPressed();
    }



    //device 정보 받아오기

    private class GetDeviceInfo extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPostExecute(Void result) { //doInBackground 작업이 끝나고 난뒤의 작업

        }

        @Override
        protected Void doInBackground(Void... params) { //백그라운드 작업이 진행되는 곳.
            getDeviceList();//사용자가 입력한 계정에 등록된 장치의 URL, ID, Alias(사용자가 정한 디바이스의 이름)를 가져온다.
            return null;
        }

        private void getDeviceList() {//url, id, alias값을 받아오는 곳. ,token 필요.
            try {
                URL url = new URL("https://wap.tplinkcloud.com?token="+dbHandler.getPlugUserDB().get(0).getUserToken());
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("method", "getDeviceList");

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//POST message
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);


                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes());
                outputStream.flush();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)//값을 잘 받았을 때.
                {
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        byteArrayOutputStream.write(byteBuffer, 0, nLength);
                    }
                    byteData = byteArrayOutputStream.toByteArray();
                    String response = new String(byteData);

                    JSONObject responseJSON1 = new JSONObject(response);

                    if(responseJSON1.get("error_code").equals(0) )//메세지를 잘 받았을 때.
                    {
                        JSONObject responseJSON2 = (JSONObject)responseJSON1.get("result");
                        JSONArray jsonArray = (JSONArray) responseJSON2.get("deviceList");
                        for(int i=0;i<jsonArray.length();i++)// device가 여러개 있을 때 모두 불러오기 위함.
                        {
                            JSONObject responseJSON3 = jsonArray.getJSONObject(i);
                            PlugItem p = new PlugItem(responseJSON3.getString("deviceId"),responseJSON3.getString("appServerUrl"),responseJSON3.getString("alias"),dbHandler.getPlugUserDB().get(0).getUserId());
                            plugItems.add(p);
                            Log.d("uuid","status : "+ responseJSON3.toString());
                        }

//                        Log.d("uuid","url : "+plugItems.get(0).geturl()+ "\nid : "+plugItems.get(0).getdeviceId()+"\nalias : "+plugList.get(0).getalias()+"\nstatus : "+plugList.get(0).getstatus());
                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
