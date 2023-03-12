package com.example.androidinventorymanagement.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.button.MaterialButton;

public class ManuallyAddQuoteActivity extends AppCompatActivity {
    EditText prodName,prodCode,prodMrp,prodAgtMrp,discount,quantityQuoteAddProduct;
    MaterialButton submitBtn,addMoreBtn;
    ImageView backBtn;
    Context mContext;

    TextView calculatePriceQuoteAddProduct,calculateQtyQuoteAddProduct,calculateTotalQuoteAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_quote);

        mContext = ManuallyAddQuoteActivity.this;
        prodName = findViewById(R.id.name);
        prodCode = findViewById(R.id.codeQuoteProductCode);
        prodMrp = findViewById(R.id.mrpQuoteAddProduct);
        prodAgtMrp = findViewById(R.id.agentQuoteMrpAddProduct);
        submitBtn = findViewById(R.id.submitQuoteBtn);
        addMoreBtn = findViewById(R.id.addQuoteMoreBtn);
        backBtn = findViewById(R.id.addQuoteProductBackButton);
        discount = findViewById(R.id.discountQuoteAddProduct);
        quantityQuoteAddProduct = findViewById(R.id.quantityQuoteAddProduct);
        calculatePriceQuoteAddProduct = findViewById(R.id.calculatePriceQuoteAddProduct);
        calculateQtyQuoteAddProduct = findViewById(R.id.calculateQtyQuoteAddProduct);
        calculateTotalQuoteAddProduct = findViewById(R.id.calculateTotalQuoteAddProduct);


        prodAgtMrp.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,28f)});
        discount.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,100f)});
        prodMrp.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,1000000f)});

        prodMrp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && !s.toString().equals("")) {
                    String price = s.toString();
                    int priceI = Integer.parseInt(price);
                    String Qty = quantityQuoteAddProduct.getText().toString();
                    int QtyI = Integer.parseInt(Qty);
                    int TotalI = priceI * QtyI;
                    String total = String.valueOf(TotalI);
                    calculatePriceQuoteAddProduct.setText(price);
                    calculateQtyQuoteAddProduct.setText(Qty);
                    calculateTotalQuoteAddProduct.setText(total);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && !s.toString().equals("")) {
                    String price = s.toString();
                    int priceI = Integer.parseInt(price);
                    String Qty = quantityQuoteAddProduct.getText().toString();
                    int QtyI = Integer.parseInt(Qty);
                    int TotalI = priceI * QtyI;
                    String total = String.valueOf(TotalI);
                    calculatePriceQuoteAddProduct.setText(price);
                    calculateQtyQuoteAddProduct.setText(Qty);
                    calculateTotalQuoteAddProduct.setText(total);
                }
            }
        });

        quantityQuoteAddProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && !s.toString().equals("")) {
                    String price = prodMrp.getText().toString();
                    int priceI = Integer.parseInt(price);
                    String Qty = s.toString();
                    int QtyI = Integer.parseInt(Qty);
                    int TotalI = priceI * QtyI;
                    String total = String.valueOf(TotalI);
                    calculatePriceQuoteAddProduct.setText(price);
                    calculateQtyQuoteAddProduct.setText(Qty);
                    calculateTotalQuoteAddProduct.setText(total);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null && !s.toString().equals("")) {
                    String price = prodMrp.getText().toString();
                    int priceI = Integer.parseInt(price);
                    String Qty = s.toString();
                    int QtyI = Integer.parseInt(Qty);
                    int TotalI = priceI * QtyI;
                    String total = String.valueOf(TotalI);
                    calculatePriceQuoteAddProduct.setText(price);
                    calculateQtyQuoteAddProduct.setText(Qty);
                    calculateTotalQuoteAddProduct.setText(total);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManuallyAddQuoteActivity.this,QuoteItemsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString();
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdAgtMrp = prodAgtMrp.getText().toString();
                String mDiscountPrice = discount.getText().toString();
                String qty = quantityQuoteAddProduct.getText().toString();

                if (!mProdName.equals("") && !MProdMrp.equals("") && !MProdAgtMrp.equals("") || !mDiscountPrice.equals("") && !(Integer.parseInt(mDiscountPrice) >100))
                {
                    int discountPrice = 0;
                    int finalDiscountPrice = 0;
                    if (mDiscountPrice.equals(""))
                    {
                        discountPrice = 0;
                    }
                    else
                    {
                        discountPrice = Integer.parseInt(discount.getText().toString());
                    }
                    double amount = Double.parseDouble(MProdMrp);
                    double res = (amount / 100.0f) * discountPrice;
                    Log.e("pertage", String.valueOf(res));
                    Double d = new Double(res);
                    int i = d.intValue();
                    finalDiscountPrice = Integer.parseInt(MProdMrp) - i;
                    processInsert(mProdName,mProdCode,String.valueOf(finalDiscountPrice),MProdAgtMrp,qty);
                    SharedPreferenceMethods.setSharedPrefSharedQuote(mContext, false);
                    Intent intent = new Intent(ManuallyAddQuoteActivity.this,QuoteItemsActivity.class);
                    startActivity(intent);
                    finish();

                }
                else
                    Toast.makeText(ManuallyAddQuoteActivity.this, "Please entry valid values", Toast.LENGTH_SHORT).show();
            }
        });

        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString();
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdAgtMrp = prodAgtMrp.getText().toString();
                String mDiscountPrice = discount.getText().toString();
                String qty = quantityQuoteAddProduct.getText().toString();

                if (!mProdName.equals("") && !MProdMrp.equals("") && !MProdAgtMrp.equals("") || !mDiscountPrice.equals("") && !(Integer.parseInt(mDiscountPrice) >100))
                {
                    int discountPrice = 0;
                    int finalDiscountPrice = 0;
                    if (mDiscountPrice.equals(""))
                    {
                        discountPrice = 0;
                    }
                    else
                    {
                        discountPrice = Integer.parseInt(discount.getText().toString());
                    }
                    double amount = Double.parseDouble(MProdMrp);
                    double res = (amount / 100.0f) * discountPrice;
                    Log.e("pertage", String.valueOf(res));
                    Double d = new Double(res);
                    int i = d.intValue();
                    finalDiscountPrice = Integer.parseInt(MProdMrp) - i;
                    processInsert(mProdName,mProdCode,String.valueOf(finalDiscountPrice),MProdAgtMrp,qty);
                    SharedPreferenceMethods.setSharedPrefSharedQuote(mContext, false);
                    startActivity(new Intent(ManuallyAddQuoteActivity.this,ManuallyAddQuoteActivity.class));
                    finish();

                }
                else
                    Toast.makeText(ManuallyAddQuoteActivity.this, "Please entry valid values", Toast.LENGTH_SHORT).show();

//                String mProdName = prodName.getText().toString();
//                String mProdCode = prodCode.getText().toString();
//                String MProdMrp = prodMrp.getText().toString();
//                String MProdAgtMrp = prodAgtMrp.getText().toString();
//
//                if (!mProdName.equals("") && !mProdCode.equals("") && !MProdMrp.equals("") && !MProdAgtMrp.equals(""))
//                {
//                    processInsert(mProdName,mProdCode,MProdMrp,MProdAgtMrp);
//                }
//                else
//                    Toast.makeText(ManuallyAddQuoteActivity.this, "Please entry valid values", Toast.LENGTH_SHORT).show();
//                prodName.setText(null);
//                prodCode.setText(null);
//                prodMrp.setText(null);
//                prodAgtMrp.setText(null);
//                prodCode.setFocusable(true);
//                prodCode.requestFocus();


            }
        });
    }
    private void processInsert(String name, String code, String mrp, String agtMrp, String Qty)
    {

        String res = new DbManager(this).createQuote(code,name,agtMrp, Float.valueOf(Qty),mrp,"manually");
        if (!res.equals("0"))
        {
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
//            MediaPlayer music = MediaPlayer.create(ManuallyAddQuoteActivity.this, R.raw.beep_beep);
//            music.start();
        }

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(ManuallyAddQuoteActivity.this,QuoteItemsActivity.class);
        startActivity(intent);
        finish();
    }

}