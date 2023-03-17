package com.example.androidinventorymanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Models.ProductsModel;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class ProductsAdapter extends FirebaseRecyclerAdapter<ProductsModel,ProductsAdapter.MyViewHolder> {
    String name;
    String code;
    String mrp;
    String key;
    String userRole;
    Context context;
    LinearLayout selectMenuOption;
    Boolean isSelectable = false;

    public Boolean getSelectable() {
        return isSelectable;
    }

    public void setSelectable(Boolean selectable) {
        isSelectable = selectable;
    }

    private onCardClickListner cardClickListner;
    private onCardLongClickListner cardLongClickListner;

    public ProductsAdapter(@NonNull FirebaseRecyclerOptions<ProductsModel> options, Context context, LinearLayout selectMenuOption) {
        super(options);
        this.context = context;
        this.selectMenuOption = selectMenuOption;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ProductsModel model) {

        userRole = SharedPreferenceMethods.getSharedPrefUserRole(context);

        name = model.getName();
        code = model.getCode();
        mrp = model.getMrp();
        key = model.getKey();

        if (name.equals("")) {
            holder.productName.setText("N/A");
        }
        else {
            String finalName = model.getName().substring(0, 1).toUpperCase() + model.getName().substring(1).toLowerCase();
            holder.productName.setText(finalName);
        }
        if (code.equals("")) {
            holder.productCode.setText("N/A:");
        }
        else {
            holder.productCode.setText(model.getCode() +":");
        }
        if (mrp.equals("")) {
            holder.productMrp.setText("N/A");
        }
        else {
//            if (userRole.equals("agent")) {
//                holder.productMrp.setText(model.getGstAmt());
//            }
            if (userRole.equals("admin")) {
                holder.productMrp.setText(model.getMrp());
            }else{
                holder.rupeesSymbol.setVisibility(View.GONE);
                holder.productMrp.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productName,productCode,productMrp, rupeesSymbol;
        ConstraintLayout productViewLayout;
        ImageView checkImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.scanProductNameViewHolder);
            productCode = itemView.findViewById(R.id.listProductCode);
            rupeesSymbol = itemView.findViewById(R.id.scanProductQtyViewHolder);
            productMrp = itemView.findViewById(R.id.listProductMrp);
//            listProductMrpLayout = itemView.findViewById(R.id.listProductMrpLayout);
            checkImage = itemView.findViewById(R.id.checkImg);
            productViewLayout = itemView.findViewById(R.id.productViewLayout);

            productViewLayout.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cardClickListner != null) {
                    cardClickListner.onCardClick(getSnapshots().getSnapshot(position),position,checkImage,productViewLayout);
                }
            });

            productViewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && cardClickListner != null) {
                        cardLongClickListner.onLongCardClick(getSnapshots().getSnapshot(position), position, checkImage, productViewLayout);
                    }
                    return true;
                }
            });
        }
    }

    public interface onCardClickListner{
        void onCardClick(DataSnapshot snapshot, int position, ImageView ImageView, ConstraintLayout linearLayout);
    }
    public interface onCardLongClickListner{
        void onLongCardClick(DataSnapshot snapshot, int position, ImageView ImageView, ConstraintLayout linearLayout);
    }
    public void setOnClickListner(onCardClickListner cardClickListner){this.cardClickListner = cardClickListner;}
    public void setOnLongClickListner(onCardLongClickListner cardLongClickListner){this.cardLongClickListner = cardLongClickListner;}
}
