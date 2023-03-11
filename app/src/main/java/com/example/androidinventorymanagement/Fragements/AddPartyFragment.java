package com.example.androidinventorymanagement.Fragements;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class AddPartyFragment extends Fragment {

    TextInputEditText addPartyName,addPartyNumber,addPartyGST,addPartyAddress;
    CardView addPartySave,addPartySaveNew;
    ImageView addPartyBack;
    TextView validateGST;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_party, container, false);

        addPartyName = view.findViewById(R.id.addPartyName);
        addPartyNumber = view.findViewById(R.id.addPartyNumber);
        addPartyGST = view.findViewById(R.id.addPartyGST);
        addPartyAddress = view.findViewById(R.id.addPartyAddress);
        addPartySave = view.findViewById(R.id.addPartySave);
        addPartySaveNew = view.findViewById(R.id.addPartySaveNew);
        addPartyBack = view.findViewById(R.id.addPartyBack);
        validateGST = view.findViewById(R.id.validateGST);

        addPartyGST.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the entered text is a GST number
                String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$";
                if (s.toString().matches(regex)) {
                    validateGST.setText("Valid GST number");
                } else {
                    validateGST.setText("Invalid GST number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SharedPreferenceMethods.setSharedPrefBackState(getContext(), Constances.BACK_ADD_PARTY);
        DatabaseReference databaseReferenceParty = FirebaseDatabase.getInstance().getReference("party");

        String Key = databaseReferenceParty.push().getKey();

        addPartyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
            }
        });

        addPartySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPartyName.getText().toString().equals("")){
                    Toast.makeText(getContext(), "No Name Found", Toast.LENGTH_SHORT).show();
                }else if(addPartyNumber.getText().toString().length() != 10){
                    Toast.makeText(getContext(), "Wrong Number", Toast.LENGTH_SHORT).show();
                }else {
                    party newParty = new party(addPartyName.getText().toString().toLowerCase(Locale.ROOT), addPartyNumber.getText().toString(), addPartyGST.getText().toString(), addPartyAddress.getText().toString(), Key);
                    databaseReferenceParty.child(Key).setValue(newParty);
                    HomeFragment homeFragment = new HomeFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                }
            }
        });

        addPartySaveNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPartyName.getText().toString().equals("")){
                    Toast.makeText(getContext(), "No Name Found", Toast.LENGTH_SHORT).show();
                }else if(addPartyNumber.getText().toString().length() != 10){
                    Toast.makeText(getContext(), "Wrong Number", Toast.LENGTH_SHORT).show();
                }else {
                    party newParty = new party(addPartyName.getText().toString().toLowerCase(Locale.ROOT), addPartyNumber.getText().toString(), addPartyGST.getText().toString(), addPartyAddress.getText().toString(), Key);
                    databaseReferenceParty.child(Key).setValue(newParty);
                    AddPartyFragment addPartyFragment = new AddPartyFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame, addPartyFragment).commit();
                }
            }
        });
        return view;
    }
}