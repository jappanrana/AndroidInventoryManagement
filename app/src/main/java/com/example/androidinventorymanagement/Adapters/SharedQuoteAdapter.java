package com.example.androidinventorymanagement.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.MainActivity;
import com.example.androidinventorymanagement.Models.QuotationModel;
import com.example.androidinventorymanagement.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SharedQuoteAdapter extends FirebaseRecyclerAdapter<QuotationModel,SharedQuoteAdapter.MyViewHolder> {
    SharedPreferences userDetail;

    public SharedQuoteAdapter(@NonNull FirebaseRecyclerOptions<QuotationModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull QuotationModel model) {

        userDetail = holder.itemView.getContext().getSharedPreferences("userDetail",0);

        holder.customeraName.setText(model.getCustomerName());
        String total = String.valueOf(model.getTotal());
        holder.customerTotal.setText("₹ "+total);
        String date = model.getDateTime().substring(0, model.getDateTime().indexOf("_"));
        holder.date.setText(date);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_quote_details_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customeraName,customerTotal,date;
        LinearLayout sharedQuoteViewLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            customeraName = itemView.findViewById(R.id.quoteCustomerName);
            customerTotal = itemView.findViewById(R.id.quoteCustomerNo);
            date = itemView.findViewById(R.id.quoteDate);
            sharedQuoteViewLayout = itemView.findViewById(R.id.sharedQuoteViewLayout);

            sharedQuoteViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userInpNumber = getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).child("customerNumber").getValue().toString();
                    Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                    userDetail.edit().putString("key",getSnapshots().getSnapshot(getPosition()).getKey()).apply();
                    userDetail.edit().putString("customerName",customeraName.getText().toString()).apply();
                    userDetail.edit().putString("customerNo",userInpNumber).apply();
                    itemView.getContext().startActivity(intent);
                    ((Activity)itemView.getContext()).finish();
                }
            });
        }
    }
}
