package com.example.androidinventorymanagement.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Adapters.SqlQuoteAdapter;
import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.Models.ProductsModel;
import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class QuoteItemsActivity extends AppCompatActivity
{
    Context mContext;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceQuote;
    DatabaseReference databaseReferenceQuotations;
    RecyclerView recyclerView;
    TextView subTotalAmt,totalGstAmt,grandTotal,discountAmt,delete,editedTextView;
    ImageView quoteBackBtn, minusDiscount;
    LinearLayout quoteLayout;
    TextView customerName,customerNo,date;
    String getCustomerName,getCustomerNo;
    SqlQuoteAdapter sqlQuoteAdapter;
    LinearLayout discountLayout,addQuoteImg;
    boolean sharedQuote = false;
    boolean edited = false;
    QuoteModel quoteModel;
    ArrayList<QuoteModel> dataholder = new ArrayList<>();
    ArrayList<QuoteModel> shareddataholder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_items);

        mContext = QuoteItemsActivity.this;
        recyclerView = findViewById(R.id.quoteRecyclerview);
        subTotalAmt = findViewById(R.id.subTotalAmt);
        totalGstAmt = findViewById(R.id.totalGstAmt);
        grandTotal = findViewById(R.id.grandTotal);
        discountAmt = findViewById(R.id.disAmt);
        quoteBackBtn = findViewById(R.id.quoteBackButton);
        delete = findViewById(R.id.deleted);
        quoteLayout = findViewById(R.id.quoteDetailsLayout);
        customerNo = findViewById(R.id.customerNoQuote);
        customerName = findViewById(R.id.customerNameQuote);
        date = findViewById(R.id.dateOnQuote);
        addQuoteImg = findViewById(R.id.addQuote);
        editedTextView = findViewById(R.id.editTextView);
        discountLayout = findViewById(R.id.discountQuoteLayout);

        Intent intent = getIntent();
        getCustomerName = SharedPreferenceMethods.getSharedPrefCustomerName(mContext);
        getCustomerNo = SharedPreferenceMethods.getSharedPrefCustomerNumber(mContext);
        String key = SharedPreferenceMethods.getSharedPrefCustomerKey(mContext);
        sharedQuote = SharedPreferenceMethods.getSharedPrefSharedQuote(mContext);
        edited = SharedPreferenceMethods.getSharedPrefEditable(mContext);

        customerName.setText(getCustomerName);
        customerNo.setText(getCustomerNo);

        if (sharedQuote)
        {
            editedTextView.setVisibility(View.VISIBLE);
        }

        //default discount and editable
        SharedPreferenceMethods.setSharedPrefDisAvailable(mContext,"no");
        SharedPreferenceMethods.setSharedPrefEditable(mContext,false);

        if (sharedQuote) {
    //        Toast.makeText(this, "in Shared", Toast.LENGTH_SHORT).show();

            databaseReferenceQuotations = FirebaseDatabase.getInstance().getReference("quotations").child(key).child("items");
            databaseReferenceQuotations.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceQuote.removeEventListener(this);


                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Getting the data from snapshot
                         quoteModel  = postSnapshot.getValue(QuoteModel.class);
                        shareddataholder.add(quoteModel);

                        processInsert(quoteModel.getName(),quoteModel.getCode(),
                                    quoteModel.getMrp(), quoteModel.getGstAmt());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            sqlQuoteAdapter = new SqlQuoteAdapter(shareddataholder,subTotalAmt,totalGstAmt,grandTotal,discountAmt);
        }else
        {
     //       Toast.makeText(this, "in sql", Toast.LENGTH_SHORT).show();

            sqlQuoteAdapter = new SqlQuoteAdapter(dataholder,subTotalAmt,totalGstAmt,grandTotal,discountAmt);
        }


        recyclerView.setAdapter(sqlQuoteAdapter);
        addQuoteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(QuoteItemsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setCancelable(false);
                dialog.setContentView(R.layout.add_quote);
                dialog.show();
                dialog.setCancelable(true);

                TextView scanToAddTextView,addManuallyTextView,addDiscountRupeesTextView,addDiscountPercentTextView;

                scanToAddTextView = dialog.findViewById(R.id.scanToAdd);
                addManuallyTextView = dialog.findViewById(R.id.addManually);
                addDiscountRupeesTextView = dialog.findViewById(R.id.adddiscountRuppes);
                addDiscountPercentTextView = dialog.findViewById(R.id.adddiscountPercent);


                scanToAddTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuoteItemsActivity.this, QuoteScannerActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                addManuallyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuoteItemsActivity.this,ManuallyAddQuoteActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });

//                Discount in Money
                addDiscountRupeesTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final Dialog disDialog = new Dialog(QuoteItemsActivity.this);
                        disDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        disDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        disDialog.setCancelable(false);
                        disDialog.setContentView(R.layout.discount_quotexml);
                        disDialog.show();
                        disDialog.setCancelable(true);

                        EditText inRs,inPer;
                        MaterialButton applyDis;

                        inPer = disDialog.findViewById(R.id.percentQuoteDis);
                        inRs = disDialog.findViewById(R.id.mrpQuoteDis);
                        applyDis = disDialog.findViewById(R.id.disRSApplyBtn);

                        inRs.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(s.toString().trim().length()>0 ){
                                    applyDis.setBackgroundColor(Color.parseColor("#04B8E2"));
                                    applyDis.setEnabled(true);
                                }else{
                                    applyDis.setBackgroundColor(Color.parseColor("#ABE7F5"));
                                    applyDis.setEnabled(false);
                                }
                            }
                        });

                        applyDis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String inRsStrng = inRs.getText().toString();
                                if (Integer.parseInt(inRsStrng)>Integer.parseInt(grandTotal.getText().toString()))
                                {
                                    Toast.makeText(QuoteItemsActivity.this, "Discount can't be more than the Grand Price", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    discountAmt.setText(inRsStrng);
                                    SharedPreferenceMethods.setSharedPrefDisAvailable(mContext,inRsStrng);
                                    discountLayout.setVisibility(View.VISIBLE);
                                    sqlQuoteAdapter.notifyDataSetChanged();
                                    disDialog.dismiss();
                                }
                            }
                        });
                    }
                });

//                Discount in Percentage
                addDiscountPercentTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final Dialog disDialogPer = new Dialog(QuoteItemsActivity.this);
                        disDialogPer.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        disDialogPer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        disDialogPer.setCancelable(false);
                        disDialogPer.setContentView(R.layout.discount_percent_quote);
                        disDialogPer.show();
                        disDialogPer.setCancelable(true);

                        EditText inRs,inPer;
                        MaterialButton applyDis;



                        inPer = disDialogPer.findViewById(R.id.percentQuoteDis);
                        inRs = disDialogPer.findViewById(R.id.mrpQuoteDis);
                        applyDis = disDialogPer.findViewById(R.id.disPercentageApplyBtn);

                        inPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(s.toString().trim().length()>0 ){
                                    applyDis.setBackgroundColor(Color.parseColor("#04B8E2"));
                                    applyDis.setEnabled(true);
                                }else{
                                    applyDis.setBackgroundColor(Color.parseColor("#ABE7F5"));
                                    applyDis.setEnabled(false);
                                }
                            }
                        });

                        applyDis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String inPerStrng = inPer.getText().toString();
                                if (Integer.parseInt(inPerStrng)>100)
                                {
                                    Toast.makeText(QuoteItemsActivity.this, "Discount can't be more than 100%", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    DbManager dbManager = new DbManager(QuoteItemsActivity.this);
                                    String res2 = String.valueOf(dbManager.readallOnlyDiscountValues());
                                    double amount = Double.parseDouble(res2);
                                    double res = (amount / 100.0f) * Integer.parseInt(inPerStrng);
                                    Log.e("pertage", String.valueOf(res));
                                    Double d = new Double(res);
                                    int i = d.intValue();
                                    discountAmt.setText(String.valueOf(i));
                                    SharedPreferenceMethods.setSharedPrefDisAvailable(mContext,discountAmt.getText().toString());
                                    discountLayout.setVisibility(View.VISIBLE);
                                    dbManager.readallOnlyDiscountValues();
                                    sqlQuoteAdapter.notifyDataSetChanged();
                                    disDialogPer.dismiss();
                                }
                            }
                        });
                    }
                });



                //finish();
            }
        });

        minusDiscount = findViewById(R.id.minusDiscountImage);

        minusDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountAmt.setText(String.valueOf(0));
                SharedPreferenceMethods.setSharedPrefDisAvailable(mContext,"no");
                discountLayout.setVisibility(View.GONE);
                sqlQuoteAdapter.notifyDataSetChanged();
            }
        });


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        date.setText(formattedDate);
        Date dt = new Date();
        //allQuoteItemList = (List<ProductsModel>) getIntent().getParcelableExtra("mylist");
        ArrayList<ProductsModel> myList = (ArrayList<ProductsModel>) getIntent().getSerializableExtra("mylist");

        Log.e("list", String.valueOf(myList));

//        testQuoteAdapter = new TestQuoteAdapter(myList,QuoteItemsActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceQuote = firebaseDatabase.getReference("Quote");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<QuoteModel> options = new FirebaseRecyclerOptions.Builder<QuoteModel>()
                .setQuery(databaseReferenceQuote,QuoteModel.class).build();

//        adapter = new QuoteAdapter(options,subTotalAmt,totalGstAmt,grandTotal,sharedPDF,quoteLayout);
//        recyclerView.setAdapter(testQuoteAdapter);
//        adapter.startListening();

        Cursor cursor = new DbManager(this).readalldata();

        while (cursor.moveToNext())
        {
            QuoteModel model = new QuoteModel(cursor.getString(2),cursor.getFloat(4),cursor.getString(3),
                    cursor.getString(5),cursor.getString(3),cursor.getString(6));
            dataholder.add(model);
            Set<QuoteModel> set = new HashSet<>(dataholder);
            dataholder.clear();
            dataholder.addAll(set);
        }

        Set<QuoteModel> set=new HashSet<>(dataholder);
        List<QuoteModel> allQuoteItemList = new ArrayList<>();

        allQuoteItemList.addAll(set);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuoteItemsActivity.this, "done", Toast.LENGTH_SHORT).show();
                DbManager dbManager = new DbManager(QuoteItemsActivity.this);
                dbManager.deleteAll();
            }
        });

        quoteBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                userDetail.edit().putBoolean("sharedQuote",false).apply();
//                userDetail.edit().putBoolean("edited",false).apply();

            }
        });

        findViewById(R.id.finalQuotePDF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(QuoteItemsActivity.this,FinalQuoteActivity.class);
                SharedPreferenceMethods.setSharedPrefEditable(mContext,false);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setTitle("Unsaved Changes")
                .setMessage("Do you want to cancel the quotation?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbManager dbManager = new DbManager(QuoteItemsActivity.this);
                        dbManager.deleteAll();
                        SharedPreferenceMethods.setSharedPrefNavigation(mContext, Constances.NAVIGATION_PROFILE);

                        if (edited)
                        {
                            Intent intent = new Intent(QuoteItemsActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            SharedPreferenceMethods.setSharedPrefSharedQuote(mContext,false);
                            Intent intent = new Intent(QuoteItemsActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                })
                .setNegativeButton("No", null);

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
    }
    private void processInsert(String name, String code, String mrp, String agtMrp)
    {
        String res = new DbManager(this).createQuote(code,name,agtMrp, Float.valueOf("1"),mrp,"scan");
        if (!res.equals("0"))
        {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        View view =  getWindow().getDecorView().getRootView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.clearFocus();

    }
}
