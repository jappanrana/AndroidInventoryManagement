package com.example.androidinventorymanagement.Fragements;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefSearchParty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.androidinventorymanagement.Adapters.SharedQuoteAdapter;
import com.example.androidinventorymanagement.Models.QuotationModel;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.Navigation.ProfileFragment;
import com.example.androidinventorymanagement.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SharedQuoteFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    DatabaseReference databaseReferenceQuotations;
    SharedQuoteAdapter sharedQuoteAdapter;
    ImageView sharedQuotionsback;
    TextInputEditText searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sharedQuote = inflater.inflate(R.layout.fragment_shared_quote, container, false);

        recyclerView = sharedQuote.findViewById(R.id.sharedRecyclerview);
        sharedQuotionsback = sharedQuote.findViewById(R.id.viewPartyTransactionsBack);
        searchView = sharedQuote.findViewById(R.id.quotationsSearchView);

        mContext = getContext();

        databaseReferenceQuotations = FirebaseDatabase.getInstance().getReference("quotations");

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        FirebaseRecyclerOptions<QuotationModel> options = new FirebaseRecyclerOptions.Builder<QuotationModel>()
                .setQuery(databaseReferenceQuotations.orderByChild("customerName").startAt(getSharedPrefSearchParty(mContext)).endAt(getSharedPrefSearchParty(mContext)+ "\uf8ff"),QuotationModel.class).build();

        sharedQuoteAdapter = new SharedQuoteAdapter(options,mContext);
        recyclerView.setAdapter(sharedQuoteAdapter);
        sharedQuoteAdapter.startListening();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                processesSearch(s.toString().toLowerCase(Locale.ROOT));
            }

            @Override
            public void afterTextChanged(Editable s) {

                processesSearch(s.toString().toLowerCase(Locale.ROOT));
            }
        });

        sharedQuotionsback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
            }
        });

        return sharedQuote;
    }

    private void processesSearch(String query) {

        FirebaseRecyclerOptions<QuotationModel> options = new FirebaseRecyclerOptions.Builder<QuotationModel>()
                .setQuery(databaseReferenceQuotations.orderByChild("name").startAt(query).endAt(query+ "\uf8ff"),QuotationModel.class).build();

        sharedQuoteAdapter = new SharedQuoteAdapter(options,mContext);
        recyclerView.setAdapter(sharedQuoteAdapter);
        sharedQuoteAdapter.startListening();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();

        requireView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileFragment()).commit();
                        return true;
                    }
                }
                return false;
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}