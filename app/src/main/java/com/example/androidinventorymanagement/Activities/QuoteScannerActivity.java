package com.example.androidinventorymanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;

public class QuoteScannerActivity extends AppCompatActivity {

    Context mContext;
    MaterialButton proceedBtn;
    private CodeScanner mCodeScanner;
    EditText quantityScanner;
    ArrayList<String> youNameArray = new ArrayList<>();
    RecyclerView recyclerView;
//    ScanItemAdapter scanItemAdapter;
    String UserRole;
    MediaPlayer music;
    ArrayList<QuoteModel> dataholder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_scanner);

        mContext = QuoteScannerActivity.this;
        proceedBtn = findViewById(R.id.proceedBtn);
        quantityScanner = findViewById(R.id.quantityScanner);
        recyclerView = findViewById(R.id.scanProductRecyclerView);
        UserRole = SharedPreferenceMethods.getSharedPrefUserRole(mContext);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        music = MediaPlayer.create(QuoteScannerActivity.this, R.raw.beep_beep);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.startPreview();
                        DatabaseReference RedLineRouteReference = FirebaseDatabase.getInstance().getReference().child("Products").child(result.getText());
                        RedLineRouteReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                RedLineRouteReference.removeEventListener(this);
                                if(dataSnapshot.exists())
                                {
                                    mCodeScanner.stopPreview();
//                                    scanItemAdapter.notifyDataSetChanged();
                                    Toast.makeText(QuoteScannerActivity.this, "Inside", Toast.LENGTH_SHORT).show();
                                    //quantityScanner.setVisibility(View.VISIBLE);
                                    //scanProductNameLayout.setVisibility(View.VISIBLE);
                                    //scanProductName.setText(dataSnapshot.child("name").getValue().toString());
                                    String data = dataSnapshot.getValue().toString();
                                    youNameArray.add(data);

//                                    processInsert(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("code").getValue().toString(),
//                                            dataSnapshot.child("mrp").getValue().toString(), dataSnapshot.child("gstAmt").getValue().toString());
                                    proceedBtn.setBackgroundColor(Color.parseColor("#04B8E2"));
                                    proceedBtn.setEnabled(true);
                                    quantityScanner.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            if (actionId == EditorInfo.IME_ACTION_DONE)
                                            {
                                                String qty = quantityScanner.getText().toString();
                                                String name = dataSnapshot.child("name").getValue().toString();
                                                DbManager dbManager = new DbManager(QuoteScannerActivity.this);
                                                dbManager.UpdateQty(qty,name);
                                                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(quantityScanner.getWindowToken(), 0);
                                            }
                                            return false;
                                        }
                                    });

                                }else {
                                    Toast.makeText(QuoteScannerActivity.this, "Product Does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCodeScanner.startPreview();
    }
}