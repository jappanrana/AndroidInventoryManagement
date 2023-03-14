package com.example.androidinventorymanagement.Fragements;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefEditParty;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPartyFragment extends Fragment {

    TextInputEditText editPartyName,editPartyNumber,editPartyGST,editPartyAddress;
    CardView editPartyDelete,editPartySave;

    ImageView editPartyBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_party, container, false);

        editPartyName = view.findViewById(R.id.editPartyName);
        editPartyNumber = view.findViewById(R.id.editPartyNumber);
        editPartyGST = view.findViewById(R.id.editPartyGST);
        editPartyAddress = view.findViewById(R.id.editPartyAddress);
        editPartyDelete = view.findViewById(R.id.editPartyDelete);
        editPartySave = view.findViewById(R.id.editPartySave);
        editPartyBack = view.findViewById(R.id.editPartyBack);
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$";


        DatabaseReference databaseReferenceParty = FirebaseDatabase.getInstance().getReference("party");

        party currentEditParty = getSharedPrefEditParty(getContext());

        editPartyName.setText(currentEditParty.getName());
        editPartyNumber.setText(currentEditParty.getNumber());
        editPartyGST.setText(currentEditParty.getGSTno());
        editPartyAddress.setText(currentEditParty.getAddress());

        SharedPreferenceMethods.setSharedPrefBackState(getContext(), Constances.BACK_EDIT_PARTY);



        TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayout3);
        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Check if the text matches the regular expression pattern
                    Pattern pattern = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$");
                    Matcher matcher = pattern.matcher(s);
                    boolean isMatch = matcher.matches();
                    // Change the box stroke color based on the result
                    if (isMatch) {
                        textInputLayout.setBoxStrokeColor(Color.GREEN);
                    } else {
                        textInputLayout.setBoxStrokeColor(Color.RED);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Do nothing
                }
            });
        }

        editPartyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
            }
        });

        editPartyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReferenceParty.child(currentEditParty.getKey()).removeValue();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
            }
        });

        editPartySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editPartyName.getText().toString().equals("")){
                    Toast.makeText(getContext(), "No Name Found", Toast.LENGTH_SHORT).show();
                }else if(editPartyNumber.getText().toString().length() != 10){
                    Toast.makeText(getContext(), "Wrong Number", Toast.LENGTH_SHORT).show();
                }else {
                    party newParty = new party(editPartyName.getText().toString().toLowerCase(Locale.ROOT),editPartyNumber.getText().toString(),editPartyGST.getText().toString(),editPartyAddress.getText().toString(),currentEditParty.getKey());
                    databaseReferenceParty.child(newParty.getKey()).setValue(newParty);
                    HomeFragment homeFragment = new HomeFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                }
            }
        });

        return view;
    }
}