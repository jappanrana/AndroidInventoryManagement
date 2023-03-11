package com.example.androidinventorymanagement.login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Context mContext;
    EditText loginNo,password;
    MaterialButton loginBtn;
    DatabaseReference databaseReferenceLogin;
    FirebaseDatabase firebaseDatabase;

    ArrayList<String> permissionsList;
    String[] permissionsStrBelow11 = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    String[] permissionsStrAbove11 = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CONTACTS,
    };
    int permissionsCount = 0;

    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                            {
                                for (int i = 0; i < list.size(); i++)
                                {
                                    if (shouldShowRequestPermissionRationale(permissionsStrBelow11[i])) {
                                        permissionsList.add(permissionsStrBelow11[i]);
                                    } else if (!hasPermission(mContext, permissionsStrBelow11[i])) {
                                        permissionsCount++;
                                    }
                                }

                            }

                            else
                            {

                                for (int i = 0; i < list.size(); i++)
                                {
                                    if (shouldShowRequestPermissionRationale(permissionsStrAbove11[i])) {
                                        permissionsList.add(permissionsStrAbove11[i]);
                                    } else if (!hasPermission(mContext, permissionsStrAbove11[i])) {
                                        permissionsCount++;
                                    }
                                }
                            }

                            if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                            } else {
                                //All permissions granted. Do your stuff 🤞
                                checkStoragePermission();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;

        permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissionsList.addAll(Arrays.asList(permissionsStrBelow11));
        }
        else {
            permissionsList.addAll(Arrays.asList(permissionsStrAbove11));
        }

        askForPermissions(permissionsList);
        checkPermission();

        loginNo = findViewById(R.id.loginMobileNO);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceLogin = firebaseDatabase.getReference("login");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPhoneNo = loginNo.getText().toString();
                String mPassword = password.getText().toString();

                databaseReferenceLogin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReferenceLogin.removeEventListener(this);
                        if (snapshot.child(mPhoneNo).exists())
                        {
                            // Toast.makeText(LoginActivity.this, snapshot.child(mPhoneNo).child("password").getValue().toString(), Toast.LENGTH_SHORT).show();
                            if (snapshot.child(mPhoneNo).child("password").getValue().toString().equals(mPassword))
                            {
                                String status = snapshot.child(mPhoneNo).child("status").getValue().toString();
                                SharedPreferenceMethods.setSharedPrefUserRole(mContext,status);
                                SharedPreferenceMethods.setSharedPrefRegister(mContext,true);
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(mContext, "wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(mContext, "no user Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        loginNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)    {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str1) {
                if(str1.toString().trim().length()>0 && !password.getText().toString().equals("")){
                    loginBtn.setBackgroundColor(Color.parseColor("#0477E2"));
                    loginBtn.setEnabled(true);
                }else{
                    loginBtn.setBackgroundColor(Color.parseColor("#CED8E1"));
                    loginBtn.setEnabled(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)    {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                if(str.toString().trim().length()>0 && !loginNo.getText().toString().equals("")){
                    loginBtn.setBackgroundColor(Color.parseColor("#0477E2"));
                    loginBtn.setEnabled(true);

                }else{
                    loginBtn.setBackgroundColor(Color.parseColor("#CED8E1"));
                    loginBtn.setEnabled(false);
                }
            }
        });

    }

    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read ==PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView= inflater.inflate(R.layout.layout_permission_required, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    public void checkStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (!Environment.isExternalStorageManager()) {
                try {

                    Intent perIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    perIntent.addCategory("android.intent.category.DEFAULT");
                    perIntent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                startActivityForResult(perIntent,2296);
                    storageActivityResultLauncher.launch(perIntent);
                    Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("req", e.toString());
                    Intent perIntent = new Intent();
                    perIntent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(perIntent,2296);

                    storageActivityResultLauncher.launch(perIntent);
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();


                }
            }
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        if (Environment.isExternalStorageManager()){
                            Toast.makeText(mContext, "M.G Permission Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "M.G Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}