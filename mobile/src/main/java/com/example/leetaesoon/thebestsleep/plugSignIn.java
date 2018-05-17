package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class plugSignIn extends Activity{
    EditText mEmail;
    EditText mPassword;
    String user_email;
    String user_pass;
    String errormsg=null;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_sign_in);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        dbHandler = MainActivity.dbHandler;
        //만약 DB에 id, password 정보가 있으면 login 과정 없이 device목록을 찾아 device DB에 없는 장치가 있으면 DB에 추가.

    }

    public void KasaLogin(View view) {
        user_email = mEmail.getText().toString();
        user_pass = mPassword.getText().toString();
        user_email.trim();
        user_pass.trim();

        mEmail.setError(null);
        mPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(user_email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }
        else if (!isEmailValid(user_email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        else if (TextUtils.isEmpty(user_pass)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        }
        else{
            LoginKasa loginKasa = new LoginKasa();
            try {
                loginKasa.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(errormsg !=null)//Kasa 정보와 일치하지 않을 때.
            {
//                errormsg.indexOf()
            }




            ///
            Intent i = new Intent(plugSignIn.this, PlugList.class);
            i.putExtra("email",user_email);
//            i.putExtra("password",user_pass);
            mEmail.setText("");
            mPassword.setText("");
            startActivity(i);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        boolean temp = email.contains(".") && email.contains("@");
        return temp;
    }



//
    private class LoginKasa extends AsyncTask<Void,Void,Void>
    {
        String uuid;
        String token;
        @Override
        protected void onPostExecute(Void result)
        { //doInBackground 작업이 끝나고 난뒤의 작업

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

//                getDeviceList();//사용자가 입력한 계정에 등록된 장치의 URL, ID, Alias(사용자가 정한 디바이스의 이름)를 가져온다.
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private int getToken()  {
            try {
                URL url = new URL("https://wap.tplinkcloud.com");

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.accumulate("appType", "Kasa_Android");
                jsonObject1.accumulate("cloudPassword", user_pass); //사용자가 입력한 값으로 받자.
                jsonObject1.accumulate("cloudUserName", user_email);//사용자가 입력한 값으로 받아오기.
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
                        errormsg = responseJSON.get("msg").toString();
                    }

                    else{
                        JSONObject responseJSON2 = new JSONObject(responseJSON.get("result").toString());
                        token = (String)responseJSON2.get("token");
                        Log.d("uuid","token : "+token);
                        //여기서 userInfo Table에 추가하자.
                        KasaInfo kasaInfo = new KasaInfo(user_email,token);
                        dbHandler.addPlugUser(kasaInfo);
                    }
                    return (int)responseJSON.get("error_code");
                }

            }
            catch (MalformedURLException e) { e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
            catch (JSONException e) {e.printStackTrace();}

            return -1;
        }

//        private void getDeviceList() {//url, id, alias값을 받아오는 곳. ,token 필요.
//            try {
//                URL url = new URL("https://wap.tplinkcloud.com?token="+token);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("method", "getDeviceList");
//
//                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");//POST message
//                connection.setRequestProperty("Content-type", "application/json");
//                connection.setDoOutput(true);
//                connection.setDoInput(true);
//
//
//                OutputStream outputStream = connection.getOutputStream();
//                outputStream.write(jsonObject.toString().getBytes());
//                outputStream.flush();
//
//                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)//값을 잘 받았을 때.
//                {
//                    InputStream inputStream = connection.getInputStream();
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    byte[] byteBuffer = new byte[1024];
//                    byte[] byteData = null;
//                    int nLength = 0;
//                    while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
//                        byteArrayOutputStream.write(byteBuffer, 0, nLength);
//                    }
//                    byteData = byteArrayOutputStream.toByteArray();
//                    String response = new String(byteData);
//
//                    JSONObject responseJSON1 = new JSONObject(response);
//
//                    if(responseJSON1.get("error_code").equals(0) )//메세지를 잘 받았을 때.
//                    {
//                        JSONObject responseJSON2 = (JSONObject)responseJSON1.get("result");
//                        JSONArray jsonArray = (JSONArray) responseJSON2.get("deviceList");
//                        for(int i=0;i<jsonArray.length();i++)// device가 여러개 있을 때 모두 불러오기 위함.
//                        {
//                            JSONObject responseJSON3 = jsonArray.getJSONObject(i);
//                            SmartPlug.plug p = new SmartPlug.plug(responseJSON3.getString("appServerUrl"),responseJSON3.getString("deviceId"),responseJSON3.getString("alias"),responseJSON3.getString("status"));
//                            plugList.add(p);
//                            Log.d("uuid","status : "+ responseJSON3.toString());
//                        }
//
//                        Log.d("uuid","url : "+plugList.get(0).geturl()+ "\nid : "+plugList.get(0).getid()+"\nalias : "+plugList.get(0).getalias()+"\nstatus : "+plugList.get(0).getstatus());
//                    }
//                }
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

    }

}
