package com.example.androidinventorymanagement.Fragements;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefSearchParty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SharedQuoteFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    DatabaseReference databaseReferenceQuotations;
    SharedQuoteAdapter sharedQuoteAdapter;
    ImageView sharedQuotionsback, filterIcon;
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
        filterIcon = sharedQuote.findViewById(R.id.viewPartyTransactionsDateFilter);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

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

                processesSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

                processesSearch(s.toString());
            }
        });

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog with the current date as default value
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // Show a Toast message with the selected date
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault());
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                String selectedDate = sdf.format(calendar.getTime());

                                // Show a Toast message with the formatted date
                                processesSearch(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
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
                .setQuery(databaseReferenceQuotations.orderByChild("dateTime").startAt(query).endAt(query+ "\uf8ff"),QuotationModel.class).build();

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
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                        return true;
                    }
                }
                return false;
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}