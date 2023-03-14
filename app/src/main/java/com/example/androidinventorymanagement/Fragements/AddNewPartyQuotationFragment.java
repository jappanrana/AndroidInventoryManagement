package com.example.androidinventorymanagement.Fragements;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.androidinventorymanagement.Activities.QuoteItemsActivity;
import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.Navigation.ProfileFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class AddNewPartyQuotationFragment extends Fragment implements EasyPermissions.PermissionCallbacks{

    TextInputEditText addPartyName,addPartyNumber,addPartyGST,addPartyAddress;
    CardView addPartySave,addPartySaveNew;
    ImageView addPartyBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View addNewPartyQuotation = inflater.inflate(R.layout.fragment_add_new_party_quotation, container, false);


        addPartyName = addNewPartyQuotation.findViewById(R.id.addPartyName);
        addPartyNumber = addNewPartyQuotation.findViewById(R.id.addPartyNumber);
        addPartyGST = addNewPartyQuotation.findViewById(R.id.addPartyGST);
        addPartyAddress = addNewPartyQuotation.findViewById(R.id.addPartyAddress);
        addPartySave = addNewPartyQuotation.findViewById(R.id.addPartySave);

        addPartyBack = addNewPartyQuotation.findViewById(R.id.addPartyBack);
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$";

        addPartyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()>0 && !addPartyNumber.getText().toString().equals("")){
                    addPartySave.setBackgroundColor(Color.parseColor("#0477E2"));
                    addPartySave.setEnabled(true);

                }else{
                    addPartySave.setBackgroundColor(Color.parseColor("#CED8E1"));
                    addPartySave.setEnabled(false);
                }
            }
        });
        addPartyNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()>0 && !addPartyName.getText().toString().equals("")){
                    addPartySave.setBackgroundColor(Color.parseColor("#0477E2"));
                    addPartySave.setEnabled(true);

                }else{
                    addPartySave.setBackgroundColor(Color.parseColor("#CED8E1"));
                    addPartySave.setEnabled(false);
                }
            }
        });

        SharedPreferenceMethods.setSharedPrefBackState(getContext(), Constances.BACK_ADD_PARTY);
        DatabaseReference databaseReferenceParty = FirebaseDatabase.getInstance().getReference("party");

        String Key = databaseReferenceParty.push().getKey();

        TextInputLayout textInputLayout = addNewPartyQuotation.findViewById(R.id.textInputLayout3);
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
                    Intent intent = new Intent(getActivity(), QuoteItemsActivity.class);
                    startActivity(intent);
//                    ProfileFragment profileFragment = new ProfileFragment();
//                    getParentFragmentManager().beginTransaction().replace(R.id.frame, profileFragment).commit();
                }
            }
        });



        getPermissions();

        return addNewPartyQuotation;
    }

    public void getPermissions() {
        String[] perms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms = new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_SMS,
            };
        }
        else {
            perms = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions(this, "We need permissions because this and that", 123, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}