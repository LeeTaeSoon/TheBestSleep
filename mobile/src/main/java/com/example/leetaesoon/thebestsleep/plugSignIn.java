package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class plugSignIn extends Activity{
    EditText mEmail;
    EditText mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_sign_in);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
    }

    public void KasaLogin(View view) {
        String user_email = mEmail.getText().toString();
        String user_pass = mPassword.getText().toString();
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
        else if()//카사에 보냈을 때, SmartPlug.java 에서 가져와야한다.
        {
            focusView.requestFocus();
        }
        else{
            Intent i = new Intent(plugSignIn.this, PlugList.class);
            i.putExtra("email",user_email);
            i.putExtra("password",user_pass);
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




}
