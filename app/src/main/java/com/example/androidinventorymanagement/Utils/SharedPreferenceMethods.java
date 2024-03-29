package com.example.androidinventorymanagement.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidinventorymanagement.Models.ProductsModel;
import com.example.androidinventorymanagement.Models.party;
import com.google.gson.Gson;

public class SharedPreferenceMethods {

    public static void setSharedPrefEditParty(Context context, party value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String valueString = new Gson().toJson(value);
        sharedPreferenceObject.edit().putString("EditParty",valueString).apply();
    }

    public static party getSharedPrefEditParty(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        party value = new Gson().fromJson(sharedPreferenceObject.getString("EditParty",""),party.class);
        return value;
    }

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

    public static void setSharedPrefShowProduct(Context context, ProductsModel showProduct){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String value = new Gson().toJson(showProduct);
        sharedPreferenceObject.edit().putString("showProduct",value).commit();
    }

    public static ProductsModel getSharedPrefShowProduct(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        ProductsModel showProduct = new Gson().fromJson(sharedPreferenceObject.getString("showProduct",""),ProductsModel.class);
        return showProduct;
    }

    public static void setSharedPrefSharedQuote(Context context, Boolean value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putBoolean("SharedQuote",value).apply();
    }

    public static Boolean getSharedPrefSharedQuote(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        Boolean SharedQuote = sharedPreferenceObject.getBoolean("SharedQuote",false);
        return SharedQuote;
    }

    public static void setSharedPrefCustomerName(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("CustomerName",value).apply();
    }

    public static String getSharedPrefCustomerName(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String customerName = sharedPreferenceObject.getString("CustomerName","");
        return customerName;
    }

    public static void setSharedPrefCustomerNumber(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("CustomerNumber",value).apply();
    }

    public static String getSharedPrefCustomerNumber(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String customerNumber = sharedPreferenceObject.getString("CustomerNumber","");
        return customerNumber;
    }

    public static void setSharedPrefCustomerKey(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("CustomerKey",value).apply();
    }

    public static String getSharedPrefCustomerKey(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String customerKey = sharedPreferenceObject.getString("CustomerKey","");
        return customerKey;
    }

    public static void setSharedPrefDisAvailable(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("DiscountAvailable",value).apply();
    }

    public static String getSharedPrefDisAvailable(Context context){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String customerKey = sharedPreferenceObject.getString("DiscountAvailable","no");
        return customerKey;
    }

    public static void setSharedPrefEditable(Context context, Boolean value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putBoolean("Editable",value).apply();
    }

    public static Boolean getSharedPrefEditable(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        Boolean Editable = sharedPreferenceObject.getBoolean("Editable",false);
        return Editable;
    }

    public static void setSharedPrefBackState(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("Back",value).apply();
    }

    public static String getSharedPrefBackState(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String Back = sharedPreferenceObject.getString("Back",Constances.BACK_HOME);
        return Back;
    }

    public static void setSharedPrefGenerateType(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("billtype",value).apply();
    }

    public static String getSharedPrefGenerateType(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String Value = sharedPreferenceObject.getString("billtype","Quotation");
        return Value;
    }

    public static void setSharedPrefSearchParty(Context context, String value){
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        sharedPreferenceObject.edit().putString("partynamesearch",value).apply();
    }

    public static String getSharedPrefSearchParty(Context context) {
        SharedPreferences sharedPreferenceObject = context.getSharedPreferences("sharedPrefStorage",0);
        String Value = sharedPreferenceObject.getString("partynamesearch","");
        return Value;
    }
}
