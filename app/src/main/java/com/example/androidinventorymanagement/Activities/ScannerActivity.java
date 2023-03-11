package com.example.androidinventorymanagement.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Capture;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {

    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    String sourceString, userRole;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mContext = ScannerActivity.this;
        userRole = SharedPreferenceMethods.getSharedPrefUserRole(mContext);

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("For Flash Use Volume Key up");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(ScannerActivity.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.initiateScan();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult.getContents()!=null){

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceProduct = firebaseDatabase.getReference("Products");

            databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceProduct.removeEventListener(this);

                    if(snapshot.child(intentResult.getContents().toString()).exists()) {
                        String prodName = snapshot.child(intentResult.getContents().toString()).child("name").getValue().toString();
                        String prodCode = snapshot.child(intentResult.getContents().toString()).child("code").getValue().toString();
                        String prodMrp = snapshot.child(intentResult.getContents().toString()).child("mrp").getValue().toString();
                        String prodGst = snapshot.child(intentResult.getContents().toString()).child("gstAmt").getValue().toString();

                        if (userRole.equals("admin")) {
                            sourceString = "Name" + " : " + "<b>" + prodName + "</b>" + "<br/>" +
                                    "Code" + " : " + "<b>" + prodCode + "</b>" + "<br/>" +
                                    "Sale Price" + " : " + "<b>" + prodMrp + "</b>" + "<br/>" +
                                    "GST" + " : " + "<b>" + prodGst + "</b>" + "<br/>";
                        }
                        if (userRole.equals("manager")) {
                            sourceString = "Name" + " : " + "<b>" + prodName + "</b>" + "<br/>" +
                                    "Code" + " : " + "<b>" + prodCode + "</b>" + "<br/>" +
                                    "Sale Price" + " : " + "<b>" + prodMrp + "</b>" + "<br/>" +
                                    "GST" + " : " + "<b>" + prodGst + "</b>" + "<br/>";
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this, R.style.AlertDialogTheme);
                        builder.setTitle("Product Details");
                        builder.setMessage(Html.fromHtml(sourceString));


                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                IntentIntegrator intentIntegrator = new IntentIntegrator(ScannerActivity.this);
                                intentIntegrator.setPrompt("For Flash Use Volume Key up");
                                intentIntegrator.setBeepEnabled(true);
                                intentIntegrator.setOrientationLocked(true);
                                intentIntegrator.setCaptureActivity(Capture.class);
                                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                intentIntegrator.initiateScan();
                            }
                        });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                IntentIntegrator intentIntegrator = new IntentIntegrator(ScannerActivity.this);
                                intentIntegrator.setPrompt("For Flash Use Volume Key up");
                                intentIntegrator.setBeepEnabled(true);
                                intentIntegrator.setOrientationLocked(true);
                                intentIntegrator.setCaptureActivity(Capture.class);
                                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                                intentIntegrator.initiateScan();
                            }
                        });
                        builder.show();
                    }
                    else{
                        IntentIntegrator intentIntegrator = new IntentIntegrator(ScannerActivity.this);
                        intentIntegrator.setPrompt("For Flash Use Volume Key up");
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setOrientationLocked(true);
                        intentIntegrator.setCaptureActivity(Capture.class);
                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.initiateScan();
                        Toast.makeText(ScannerActivity.this, "Product Does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            onBackPressed();
//            Toast.makeText(this, "Oops didn't find any barcode", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScannerActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}