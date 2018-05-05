package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SmartPlug extends Activity {
    String uuid;
    String token;
    ArrayList<plug> plugList;
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_plug);

        Init();
    }

    private void Init() {
        plugList = new ArrayList<>();
        switch1 = (Switch)findViewById (R.id.plugSwitch);

        MainPageTask mainPageTask = new MainPageTask();
        mainPageTask.execute();//메인에서 네트워크 연결시 오류가 난다. 따라서 백그라운드로 웹페이지에서 uuid를 가져왔다.

        //db에 정보들이 저장되어 있으면 으로 바꿔야 함.

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)//true
                {
                    plugStateChange(true);
                }
                else
                {
                    plugStateChange(false);
                }
            }
        });


        //없으면 위의 thread 과정 실행.
    }

    private void plugStateChange(final boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(plugList.get(0).geturl()+"/?"+"token="+token);

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.accumulate("deviceId", plugList.get(0).getid());
                    JSONObject content1 = new JSONObject();
                    JSONObject content2 = new JSONObject();
                    JSONObject content3 = new JSONObject();
                    if(b){
                        content1.accumulate("state",1);
                    }
                    else {
                        content1.accumulate("state",0);
                    }
                    content2.accumulate("set_relay_state",content1);
                    content3.accumulate("system",content2);

                    jsonObject1.accumulate("requestData", ""+content3);



                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.accumulate("method", "passthrough");
                    jsonObject2.accumulate("params", jsonObject1);

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");//POST message
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);


                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(jsonObject2.toString().getBytes());
                    outputStream.flush();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)//
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
//                        Log.d("uuid",responseJSON1.getString("msg"));
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public class plug {
        private String m_url;
        private String m_id;
        private String m_alias;
        private String m_status;

        public plug(String url, String id, String alias, String status)
        {
            this.m_url = url;
            this.m_id = id;
            this.m_alias = alias;
            this.m_status = status;
        }
        public String geturl()
        {
            return this.m_url;
        }
        public String getid() {return this.m_id;}
        public String getalias() {return this.m_alias;}
        public String getstatus() {return this.m_status;}
    }


    private class MainPageTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPostExecute(Void result)
        { //doInBackground 작업이 끝나고 난뒤의 작업
//            Toast.makeText(SmartPlug.this,"uuid : "+uuid,Toast.LENGTH_LONG).show();

//            if(plugList.get(0).getstatus().equals("1"))//앱을 켰을 때 장치 상태를 파악해 보여준다.
//            {
//                switch1.setChecked(true);
//                Log.d("uuid","초기값 true");
//            }
//            else
//            {
//                Log.d("uuid","초기값 false");
//                switch1.setChecked(false);
//            }
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


                 getDeviceList();//사용자가 입력한 계정에 등록된 장치의 URL, ID, Alias(사용자가 정한 디바이스의 이름)를 가져온다.
             }

             catch (IOException e) {
                 e.printStackTrace();
             }
             return null;
        }

        private void getDeviceList() {//url, id, alias값을 받아오는 곳. ,token 필요.
            try {
                URL url = new URL("https://wap.tplinkcloud.com?token="+token);
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
                            plug p = new plug(responseJSON3.getString("appServerUrl"),responseJSON3.getString("deviceId"),responseJSON3.getString("alias"),responseJSON3.getString("status"));
                            plugList.add(p);
                            Log.d("uuid","status : "+ responseJSON3.toString());
                        }

                        Log.d("uuid","url : "+plugList.get(0).geturl()+ "\nid : "+plugList.get(0).getid()+"\nalias : "+plugList.get(0).getalias()+"\nstatus : "+plugList.get(0).getstatus());
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

    }

}
