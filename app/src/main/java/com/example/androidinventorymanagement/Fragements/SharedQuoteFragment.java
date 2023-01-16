package com.example.androidinventorymanagement.Fragements;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.androidinventorymanagement.Adapters.SharedQuoteAdapter;
import com.example.androidinventorymanagement.Models.QuotationModel;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SharedQuoteFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    DatabaseReference databaseReferenceQuotations;
    SharedQuoteAdapter sharedQuoteAdapter;
    ImageView sharedQuotionsback;
    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sharedQuote = inflater.inflate(R.layout.fragment_shared_quote, container, false);

        recyclerView = sharedQuote.findViewById(R.id.sharedRecyclerview);
        sharedQuotionsback = sharedQuote.findViewById(R.id.sharedQuotionsback);
        searchView = sharedQuote.findViewById(R.id.quotationsSearchView);

        mContext = getContext();

        databaseReferenceQuotations = FirebaseDatabase.getInstance().getReference("quotations");

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        FirebaseRecyclerOptions<QuotationModel> options = new FirebaseRecyclerOptions.Builder<QuotationModel>()
                .setQuery(databaseReferenceQuotations,QuotationModel.class).build();

        sharedQuoteAdapter = new SharedQuoteAdapter(options);
        recyclerView.setAdapter(sharedQuoteAdapter);
        sharedQuoteAdapter.startListening();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processesSearch(query.toLowerCase(Locale.ROOT));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processesSearch(newText.toLowerCase(Locale.ROOT));
                return false;
            }
        });

        sharedQuotionsback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
            }
        });

        return sharedQuote;
    }

    private void processesSearch(String query) {

        FirebaseRecyclerOptions<QuotationModel> options = new FirebaseRecyclerOptions.Builder<QuotationModel>()
                .setQuery(databaseReferenceQuotations.orderByChild("name").startAt(query).endAt(query+ "\uf8ff"),QuotationModel.class).build();

        sharedQuoteAdapter = new SharedQuoteAdapter(options);
        recyclerView.setAdapter(sharedQuoteAdapter);
        sharedQuoteAdapter.startListening();

    }
}