package com.example.androidinventorymanagement.Adapters;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.setSharedPrefEditParty;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Fragements.EditPartyFragment;
import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PartyAdapter extends FirebaseRecyclerAdapter<party,PartyAdapter.MyViewHolder> {
    Partylistner listner;
    public PartyAdapter(@NonNull FirebaseRecyclerOptions<party> options, Partylistner listner) {
        super(options);
        this.listner = listner;
    }

    @Override
    protected void onBindViewHolder(@NonNull PartyAdapter.MyViewHolder holder, int position, @NonNull party model) {

        holder.partyItemName.setText(model.getName());
        holder.partyItemNumber.setText(model.getNumber());
        holder.partyItemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.clicked(getSnapshots().get(position));
            }
        });

    }

    @NonNull
    @Override
    public PartyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.party_item_holder,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView partyItemName,partyItemNumber;
        ConstraintLayout partyItemParent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            partyItemParent = itemView.findViewById(R.id.partyItemParent);
            partyItemName = itemView.findViewById(R.id.partyItemName);
            partyItemNumber = itemView.findViewById(R.id.partyItemNumber);
        }
    }

    public interface Partylistner{
        public void clicked(party pty);
    }
}
