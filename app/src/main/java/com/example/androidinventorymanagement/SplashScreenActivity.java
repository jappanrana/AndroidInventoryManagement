package com.example.androidinventorymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.androidinventorymanagement.Utils.CommonMethods;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.example.androidinventorymanagement.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashScreenStatusBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mContext = SplashScreenActivity.this;

        Boolean UserRegistered = SharedPreferenceMethods.getSharedPrefRegister(mContext);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(CommonMethods.isNetworkConnected(mContext)){
                    if(UserRegistered){
                        Intent homeActivity = new Intent(mContext,HomeActivity.class);
                        homeActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeActivity);
                        finish();
                    }else{
                        Intent loginActivity = new Intent(mContext, LoginActivity.class);
                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginActivity);
                        finish();
                    }
                }else{
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please Check Your Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Log.e("tem",dialog+""+id);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }, 3000);
    }
}