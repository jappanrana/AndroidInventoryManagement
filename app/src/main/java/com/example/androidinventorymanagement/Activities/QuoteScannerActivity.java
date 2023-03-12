package com.example.androidinventorymanagement.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.androidinventorymanagement.Adapters.ScanItemAdapter;
import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.example.androidinventorymanagement.Utils.KeyboardUtils;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QuoteScannerActivity extends AppCompatActivity {

    Context mContext;
    MaterialButton proceedBtn;
    private CodeScanner mCodeScanner;
    ArrayList<String> youNameArray = new ArrayList<>();
    RecyclerView recyclerView;
    ScanItemAdapter scanItemAdapter;
    String UserRole;
    MediaPlayer music;
    ArrayList<QuoteModel> dataholder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_scanner);

        mContext = QuoteScannerActivity.this;
        proceedBtn = findViewById(R.id.proceedBtn);
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
                        mCodeScanner.stopPreview();
                        music.start();
                        DatabaseReference RedLineRouteReference = FirebaseDatabase.getInstance().getReference().child("Products").child(result.getText());
                        RedLineRouteReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                RedLineRouteReference.removeEventListener(this);
                                if(dataSnapshot.exists()) {
                                    recyclerView.scrollToPosition(scanItemAdapter.getItemCount()-1);
                                    scanItemAdapter.notifyDataSetChanged();
                                    String data = dataSnapshot.getValue().toString();

                                    PopupWindow popup = new PopupWindow(QuoteScannerActivity.this);

                                    View view = LayoutInflater.from(QuoteScannerActivity.this).inflate(R.layout.scanned_item__popup, null);

                                    // set the dimensions of the popup window
                                    popup.setContentView(view);
                                    popup.setFocusable(true);
                                    popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                                    popup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

                                    // find the EditText and Button views in the layout
                                    TextView nameText = view.findViewById(R.id.scannedItemName);
                                    TextView unitText = view.findViewById(R.id.unitTextPopup);
                                    EditText Qty = view.findViewById(R.id.scannedItemQty);
                                    Qty.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                                    Qty.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                    Button submitButton = view.findViewById(R.id.scannedItemSubmit);
                                    Qty.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            // Check if the entered text is a decimal or a whole number
                                            if (s.toString().contains(".")) {
                                                unitText.setText("KG");
                                            } else {
                                                unitText.setText("Units");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    Qty.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,999f)});

                                    nameText.setText(dataSnapshot.child("name").getValue().toString());

                                    // set an onClickListener for the submit button
                                    submitButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // do something with the user's input
                                            String userInput = Qty.getText().toString();
                                            processInsert(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("code").getValue().toString(),
                                                    dataSnapshot.child("mrp").getValue().toString(), dataSnapshot.child("gstAmt").getValue().toString(),userInput);
                                            proceedBtn.setBackgroundColor(Color.parseColor("#04B8E2"));
                                            proceedBtn.setEnabled(true);
                                            recyclerView.setAdapter(scanItemAdapter);
                                            youNameArray.add(data);
                                            // dismiss the popup window
                                            popup.dismiss();
                                        }
                                    });

                                    popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            mCodeScanner.startPreview();
                                        }
                                    });

                                    // show the popup window
                                    popup.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
                                }
                                else {
                                    Toast.makeText(QuoteScannerActivity.this, "Product Does not exist", Toast.LENGTH_SHORT).show();
                                    mCodeScanner.startPreview();
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

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> set=new HashSet<>(youNameArray);
                List<String> allQuoteItemList = new ArrayList<>();

                allQuoteItemList.addAll(set);
                proceedBtn.setEnabled(false);
                Intent intent = new Intent(QuoteScannerActivity.this, QuoteItemsActivity.class);
                intent.putExtra("mylist", (Serializable) allQuoteItemList);
                startActivity(intent);
            }
        });

        ScanItemAdapter.scanItemListner listner = new ScanItemAdapter.scanItemListner() {
            @Override
            public void click(QuoteModel item) {
                PopupWindow popup = new PopupWindow(QuoteScannerActivity.this);

                View view = LayoutInflater.from(QuoteScannerActivity.this).inflate(R.layout.scanned_item__popup, null);

                // set the dimensions of the popup window
                popup.setContentView(view);
                popup.setFocusable(true);
                popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                popup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

                // find the EditText and Button views in the layout
                TextView nameText = view.findViewById(R.id.scannedItemName);
                EditText Qty = view.findViewById(R.id.scannedItemQty);
                Qty.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,999f)});
                Button submitButton = view.findViewById(R.id.scannedItemSubmit);

                nameText.setText(item.getName());
                Qty.setText(item.getQty().toString());

                // set an onClickListener for the submit button
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do something with the user's input
                        String userInput = Qty.getText().toString();
                        processInsert(item.getName(), item.getCode(),
                                item.getMrp(), item.getGstAmt(), userInput);
                        proceedBtn.setBackgroundColor(Color.parseColor("#04B8E2"));
                        proceedBtn.setEnabled(true);
                        recyclerView.setAdapter(scanItemAdapter);
//                        youNameArray.add(item);
                        // dismiss the popup window
                        popup.dismiss();
                    }
                });

                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCodeScanner.startPreview();
                    }
                });

                // show the popup window
                popup.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
            }
        };
        scanItemAdapter = new ScanItemAdapter(dataholder,listner);
    }

    private void processInsert(String name, String code, String mrp, String agtMrp, String qtyReceived)
    {

        String res = new DbManager(QuoteScannerActivity.this).createQuote(code,name,agtMrp, Float.valueOf(qtyReceived),mrp,"scan");
        if (!res.equals("0"))
        {
            Toast.makeText(QuoteScannerActivity.this, res, Toast.LENGTH_SHORT).show();
            dataholder.add(new QuoteModel(name,Float.parseFloat(String.valueOf(qtyReceived)),code,mrp,agtMrp,"scan"));
        }
        else {
            Cursor cursor = new DbManager(QuoteScannerActivity.this).getQty(name);
            while (cursor.moveToNext()) {
//                Float qty = Float.valueOf(cursor.getString(4));
//                qty = qty + 1;
                DbManager dbManager = new DbManager(QuoteScannerActivity.this);
                dbManager.UpdateQty(String.valueOf(qtyReceived),name);
            }
            int i = 0;
            while (i<dataholder.size()){
                QuoteModel temp = dataholder.get(i);
                if(Objects.equals(temp.getName(), name)){
                    temp.setQty(temp.getQty()+1);
                    dataholder.remove(i);
                    dataholder.add(i,temp);
                    scanItemAdapter.updateList(dataholder);
                }
                i++;
            }
        }
        scanItemAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCodeScanner.startPreview();
                mCodeScanner.setTouchFocusEnabled(true);
            }
        }, 300);
        proceedBtn.setEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCodeScanner.startPreview();
    }


}