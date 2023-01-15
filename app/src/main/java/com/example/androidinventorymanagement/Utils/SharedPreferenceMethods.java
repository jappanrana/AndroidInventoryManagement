package com.example.androidinventorymanagement.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceMethods {

    public static void setSharedPrefRegister(Context context, Boolean value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putBoolean("UserRegistered",value).apply();
    }

    public static Boolean getSharedPrefRegister(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        Boolean UserRegistered = sharedPreferenceObject.getBoolean("UserRegistered",false);
        return UserRegistered;
    }

    public static void setSharedPrefUserRole(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("UserRole",value).apply();
    }

    public static String getSharedPrefUserRole(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String UserRole = sharedPreferenceObject.getString("UserRole","");
        return UserRole;
    }

    public static void setSharedPrefNavigation(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("Navigation",value).apply();
    }

    public static String getSharedPrefNavigation(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String UserRole = sharedPreferenceObject.getString("Navigation",Constances.NAVIGATION_HOME);
        return UserRole;
    }

}
