package com.example.androidinventorymanagement.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.HomeActivity;
import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SqlQuoteAdapter extends RecyclerView.Adapter<SqlQuoteAdapter.myviewholder>
{
    ArrayList<QuoteModel> dataholder;
    TextView subTotalAmt, totalGstAmt,grandTotal,discount;
    int totalOfSubTotal = 0;
    int totalOfGst = 0;
    int totalOfDiscount = 0;
    SharedPreferences userDetail;
    private boolean isVisible = false;
    boolean editable = true;


    public SqlQuoteAdapter(ArrayList<QuoteModel> dataholder, TextView subTotalAmt, TextView totalGstAmt, TextView grandTotal, TextView discountAmt) {
        this.dataholder = dataholder;
        this.subTotalAmt = subTotalAmt;
        this.totalGstAmt = totalGstAmt;
        this.grandTotal = grandTotal;
        this.discount = discountAmt;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_layout,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        userDetail = holder.itemView.getContext().getSharedPreferences("userDetail",0);
        holder.CartQuantityText.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,999f)});
        editable = SharedPreferenceMethods.getSharedPrefEditable(holder.itemView.getContext());
        if (editable)
        {
            holder.CartQuantityText.setEnabled(true);
            holder.cartItemName.setEnabled(true);
        }else
        {
            holder.CartQuantityText.setEnabled(false);
            holder.cartItemName.setEnabled(false);

        }
        String qtyValue;
        int qtyint = Math.round(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty());
        float qtyfloat = dataholder.get(holder.getAbsoluteAdapterPosition()).getQty();
        float qtyCondition = qtyint - qtyfloat;
        if (qtyCondition == 0.0)
        {
            qtyValue = String.valueOf(qtyint);
        }else{
            qtyValue = String.valueOf(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty());
        }
        holder.CartQuantityText.setText(qtyValue);

        String totalValue;
        String total = String.valueOf(Integer.parseInt(dataholder.get(holder.getAbsoluteAdapterPosition()).getMrp())*Float.parseFloat(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty().toString()));
        int totalint = Math.round(Float.parseFloat(total));
        float totalfloat = Float.parseFloat(total);
        float ans = totalint - totalfloat;
        if (ans == 0.0)
        {
            totalValue = String.valueOf(totalint);
        }else{
            double dtotal = Double.parseDouble(total);
            Double rounded= new BigDecimal(dtotal).setScale(2, RoundingMode.HALF_UP).doubleValue();
            totalValue = String.valueOf(rounded);

        }

        holder.itemTotal.setText(totalValue);
        holder.cartItemName.setText(dataholder.get(holder.getAbsoluteAdapterPosition()).getName());
        holder.cartItemGst.setText(dataholder.get(holder.getAbsoluteAdapterPosition()).getGstAmt()+"%");
        holder.cartItemRate.setText(dataholder.get(holder.getAbsoluteAdapterPosition()).getMrp());

        if(isVisible)
        {
            holder.minusQuoteImg.setVisibility(View.GONE);
        }else
        {
            holder.minusQuoteImg.setVisibility(View.VISIBLE);
            holder.minusQuoteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme);
                    builder.setTitle("Remove Item")
                            .setMessage("Do you want to remove this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = holder.cartItemName.getText().toString();
                                    DbManager dbManager = new DbManager(holder.itemView.getContext());
                                    dbManager.deleteItem(name);
                                    totalOfSubTotal = 0;
                                    totalOfGst = 0;
                                    dataholder.remove(holder.getAbsoluteAdapterPosition());
                                    if(dataholder.size() == 0){
                                        subTotalAmt.setText(String.valueOf(0));
                                        totalGstAmt.setText(String.valueOf(0));
                                        grandTotal.setText(String.valueOf(0));
                                    }
                                    notifyDataSetChanged();
                                }

                            })
                            .setNegativeButton("No", null);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                    Button btnPositive = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                    Button btnNegative = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                    layoutParams.weight = 10;
                    btnPositive.setLayoutParams(layoutParams);
                    btnNegative.setLayoutParams(layoutParams);

//                    new AlertDialog.Builder(holder.itemView.getContext(), R.style.AlertDialogTheme)
//                            .setMessage("Do you want to remove this item?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String name = holder.cartItemName.getText().toString();
//                                    DbManager dbManager = new DbManager(holder.itemView.getContext());
//                                    dbManager.deleteItem(name);
//                                    totalOfSubTotal = 0;
//                                    totalOfGst = 0;
//                                    dataholder.remove(holder.getAbsoluteAdapterPosition());
//                                    if(dataholder.size() == 0){
//                                        subTotalAmt.setText(String.valueOf(0));
//                                        totalGstAmt.setText(String.valueOf(0));
//                                        grandTotal.setText(String.valueOf(0));
//                                    }
//                                    notifyDataSetChanged();
//                                }
//
//                            })
//                            .setNegativeButton("No", null)
//                            .show();

                }
            });
        }
        if(holder.getAbsoluteAdapterPosition() == 0){
            totalOfGst = 0;
            totalOfSubTotal = 0;
        }

        totalOfSubTotal = (int) (totalOfSubTotal + (Double.parseDouble(String.valueOf(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty()))*Integer.parseInt( dataholder.get(holder.getAbsoluteAdapterPosition()).getMrp())));
        double amount = Double.parseDouble(String.valueOf(Double.parseDouble(String.valueOf(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty()))*Integer.parseInt(dataholder.get(holder.getAbsoluteAdapterPosition()).getMrp())));
        double res = (amount / 100.0f) * Integer.parseInt(dataholder.get(holder.getAbsoluteAdapterPosition()).getGstAmt());
        Log.e("per",String.valueOf(res));
        Double d = new Double(res);
        int i = d.intValue();
        totalOfGst = totalOfGst + i;

        subTotalAmt.setText(String.valueOf(totalOfSubTotal));
        totalGstAmt.setText(String.valueOf(totalOfGst));
        grandTotal.setText(String.valueOf(totalOfSubTotal+totalOfGst-Integer.parseInt(discount.getText().toString())));

    }
    public void setVisibility(){
        isVisible = true;
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView cartItemName,cartItemRate,cartItemGst,itemTotal;
        EditText CartQuantityText;
        CardView cardView;
        ImageView minusQuoteImg;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemRate = itemView.findViewById(R.id.cartItemRate);
            cartItemGst = itemView.findViewById(R.id.cartItemGst);
            CartQuantityText = itemView.findViewById(R.id.CartQuantityText);
            cardView = itemView.findViewById(R.id.cardViewQuoteItems);
            itemTotal = itemView.findViewById(R.id.cartItemTotal);
            minusQuoteImg = itemView.findViewById(R.id.minusQuoteItem);

            cartItemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferenceMethods.setSharedPrefNavigation(itemView.getContext(), Constances.NAVIGATION_EDITPROD_QUOTE);
                    userDetail.edit().putString("productNameEdit",cartItemName.getText().toString()).apply();
                    userDetail.edit().putString("productGstEdit",cartItemGst.getText().toString()).apply();
                    userDetail.edit().putString("productRateEdit",cartItemRate.getText().toString()).apply();
                    userDetail.edit().putString("productQtyEdit",CartQuantityText.getText().toString()).apply();
                    Intent intent = new Intent(itemView.getContext(), HomeActivity.class);
                    itemView.getContext().startActivity(intent);

                }
            });

            CartQuantityText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        CartQuantityText.clearFocus();
                        Toast.makeText(itemView.getContext(), "back key", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        CartQuantityText.clearFocus();
                        Toast.makeText(itemView.getContext(), "back key", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });

            CartQuantityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_BACK)
                    {
                        Toast.makeText(itemView.getContext(), "back key", Toast.LENGTH_SHORT).show();

                        if (CartQuantityText.getText().toString().equals(""))
                        {
                            CartQuantityText.setText("1");
                        }
                        int position = getBindingAdapterPosition();
                        dataholder.get(position).setQty(Float.valueOf(CartQuantityText.getText().toString()));
                        totalOfSubTotal = 0;
                        totalOfGst = 0;
                        DbManager dbManager = new DbManager(itemView.getContext());
                        dbManager.UpdateQty(CartQuantityText.getText().toString(),cartItemName.getText().toString());
                        CartQuantityText.clearFocus();
                        InputMethodManager imm = (InputMethodManager)itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(CartQuantityText.getWindowToken(), 0);
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });

        }
    }


}
