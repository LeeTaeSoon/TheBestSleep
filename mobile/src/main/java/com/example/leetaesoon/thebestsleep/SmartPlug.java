package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SmartPlug extends Activity {
    String uuid;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_plug);

        Init();
    }

    private void Init() {
        //uuid값을 받아오자.
        MainPageTask mainPageTask = new MainPageTask();
        mainPageTask.execute();//메인에서 네트워크 연결시 오류가 난다. 따라서 백그라운드로 웹페이지에서 uuid를 가져왔다.

    }

    private int getToken()  {
        try {
            URL url = new URL("https://wap.tplinkcloud.com");

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.accumulate("appType", "Kasa_Android");
            jsonObject1.accumulate("cloudPassword", "1q2w3e4r"); //사용자가 입력한 값으로 받자.
            jsonObject1.accumulate("cloudUserName", "xxxxx@naver.com");//사용자가 입력한 값으로 받아오기.
            jsonObject1.accumulate("terminalUUID", uuid);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.accumulate("method", "login");
            jsonObject2.accumulate("params", jsonObject1);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");//POST message
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);


            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonObject2.toString().getBytes());
            outputStream.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)//값을 잘 받았을 때.
            {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    byteArrayOutputStream.write(byteBuffer, 0, nLength);
                }
                byteData = byteArrayOutputStream.toByteArray();
                String response = new String(byteData);

                final JSONObject responseJSON = new JSONObject(response);
                Log.d("uuid","json :" +responseJSON.toString());

                if((int)responseJSON.get("error_code") != 0)//비밀번호가 틀렸거나 아이디가 틀림. -> alert로 메세지를 띄우고 스레드 종료.
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SmartPlug.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                }
                            });

                            try {
                                alert.setMessage(responseJSON.get("msg").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            alert.show();
                        }
                    });
                }

                else{
                    JSONObject responseJSON2 = new JSONObject(responseJSON.get("result").toString());
                    token = (String)responseJSON2.get("token");
                    Log.d("uuid","token : "+token);
                }
                return (int)responseJSON.get("error_code");
            }

        }
        catch (MalformedURLException e) { e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace();}

        return -1;
    }



    private class MainPageTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPostExecute(Void result)
        { //doInBackground 작업이 끝나고 난뒤의 작업
//            Toast.makeText(SmartPlug.this,"uuid : "+uuid,Toast.LENGTH_LONG).show();
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

                 if(getToken() !=0) return null;// 아이디, 비밀번호중 하나가 틀렸을 경우 즉, Token을 못받아왔으면 쓰레드를 종료한다.
                 Log.d("uuid","fail");

             }

             catch (IOException e) {
                 e.printStackTrace();
             }
             return null;
        }
    }

}
