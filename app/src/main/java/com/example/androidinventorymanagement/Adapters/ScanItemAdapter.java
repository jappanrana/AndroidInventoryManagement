package com.example.androidinventorymanagement.Adapters;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Models.QuoteModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;

import java.util.ArrayList;

public class ScanItemAdapter extends RecyclerView.Adapter<ScanItemAdapter.MyViewHolder> {

    ArrayList<QuoteModel> dataholder;

    public ScanItemAdapter(ArrayList<QuoteModel> dataholder) {
        this.dataholder = dataholder;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_item_view_holder,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.scanItemName.setText(dataholder.get(holder.getAbsoluteAdapterPosition()).getName());
        holder.scanItemQty.setText(String.valueOf(dataholder.get(holder.getAbsoluteAdapterPosition()).getQty()));
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView scanItemName;
        EditText scanItemQty;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            scanItemName = itemView.findViewById(R.id.scanProductNameViewHolder);
            scanItemQty = itemView.findViewById(R.id.scanProductQtyViewHolder);

            scanItemQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        String qty = scanItemQty.getText().toString();
                        String name = scanItemName.getText().toString();
                        DbManager dbManager = new DbManager(itemView.getContext());
                        dbManager.UpdateQty(qty,name);
                        InputMethodManager imm = (InputMethodManager)itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(scanItemName.getWindowToken(), 0);
                    }
                    return false;
                }
            });
        }
    }

    public void updateList(ArrayList<QuoteModel> newList){
        this.dataholder = newList;
        this.notifyDataSetChanged();
    }
}
