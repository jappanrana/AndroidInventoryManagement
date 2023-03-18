package com.example.androidinventorymanagement.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Activities.QuoteItemsActivity;
import com.example.androidinventorymanagement.Activities.QuoteScannerActivity;
import com.example.androidinventorymanagement.Models.QuotationModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class SharedQuoteAdapter extends FirebaseRecyclerAdapter<QuotationModel,SharedQuoteAdapter.MyViewHolder> {
    Context context;
    public SharedQuoteAdapter(@NonNull FirebaseRecyclerOptions<QuotationModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull QuotationModel model) {

//        holder.customeraName.setText(model.getCustomerName());
        String total = String.valueOf(model.getTotal());
        holder.customerTotal.setText("₹ "+total);
        String date = model.getDateTime().substring(0, model.getDateTime().indexOf("_"));
        holder.date.setText(date);
        holder.partySharedQuoteType.setText(model.getType());

        holder.moreOptionViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(holder.moreOptionViewTransaction.getContext(),holder.moreOptionViewTransaction);
                menu.getMenuInflater().inflate(R.menu.transaction_options, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_delete:{
                                FirebaseDatabase.getInstance().getReference().child("quotations").child(model.getKey()).removeValue();
                                notifyDataSetChanged();
                                return true;
                            }
                            case R.id.menu_c2q:{
                                FirebaseDatabase.getInstance().getReference().child("quotations").child(model.getKey()).child("type").setValue("Quotation");
                                notifyDataSetChanged();
                                return true;
                            }
                            case R.id.menu_c2s:{
                                FirebaseDatabase.getInstance().getReference().child("quotations").child(model.getKey()).child("type").setValue("Sales");
                                notifyDataSetChanged();
                                return true;
                            }
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_quote_details_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customeraName,customerTotal,date,partySharedQuoteType;
        LinearLayout sharedQuoteViewLayout;

        ImageView moreOptionViewTransaction;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            customeraName = itemView.findViewById(R.id.partySaleView);
            customerTotal = itemView.findViewById(R.id.quoteCustomerNo);
            date = itemView.findViewById(R.id.quoteDate);
            sharedQuoteViewLayout = itemView.findViewById(R.id.sharedQuoteViewLayout);
            partySharedQuoteType = itemView.findViewById(R.id.partySharedQuoteType);
            moreOptionViewTransaction = itemView.findViewById(R.id.moreOptionViewTransaction);

            sharedQuoteViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userInpNumber = getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).child("customerNumber").getValue().toString();
                    SharedPreferenceMethods.setSharedPrefSharedQuote(itemView.getContext(),true);
                    SharedPreferenceMethods.setSharedPrefGenerateType(itemView.getContext(), getSnapshots().getSnapshot(getPosition()).child("type").getValue().toString());
                    Intent intent = new Intent(itemView.getContext(), QuoteItemsActivity.class);
                    SharedPreferenceMethods.setSharedPrefCustomerName(context,getSnapshots().getSnapshot(getPosition()).child("customerName").getValue().toString());
                    SharedPreferenceMethods.setSharedPrefCustomerNumber(context,userInpNumber);
                    SharedPreferenceMethods.setSharedPrefCustomerKey(context,getSnapshots().getSnapshot(getPosition()).getKey());
                    itemView.getContext().startActivity(intent);
                    ((Activity)itemView.getContext()).finish();
                }
            });
        }
    }
}
