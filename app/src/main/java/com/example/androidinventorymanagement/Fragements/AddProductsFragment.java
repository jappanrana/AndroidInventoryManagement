package com.example.androidinventorymanagement.Fragements;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class AddProductsFragment extends Fragment {

    EditText prodName,prodCode,prodMrp,prodGst;
    MaterialButton submitBtn,addMoreBtn;
    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    ImageView backBtn;
    Context mContext;
    ScrollView productScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View addProductsView = inflater.inflate(R.layout.fragment_add_products, container, false);
        
        mContext = getContext();

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        prodName = addProductsView.findViewById(R.id.nameProductName);
        prodCode = addProductsView.findViewById(R.id.codeProductCode);
        prodMrp = addProductsView.findViewById(R.id.mrpAddProduct);
        prodGst = addProductsView.findViewById(R.id.gstAddProduct);
        submitBtn = addProductsView.findViewById(R.id.submitBtn);
        addMoreBtn = addProductsView.findViewById(R.id.addMoreBtn);
        productScrollView = addProductsView.findViewById(R.id.addProductScrollView);
        backBtn = addProductsView.findViewById(R.id.addProductBackButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = firebaseDatabase.getReference("Products");

        prodGst.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,28.0f)});
        prodMrp.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,1000000f)});

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
            }
        });

        databaseReferenceProduct.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReferenceProduct.orderByKey().limitToLast(1).removeEventListener(this);
                String lastChildKey = String.valueOf(0);
                if(snapshot.exists()){
                    for(DataSnapshot data : snapshot.getChildren()) {
                        lastChildKey = data.getKey();
                    }
                }
                lastChildKey = String.valueOf(Integer.parseInt(lastChildKey)+1);
                prodCode.setText(lastChildKey);
                prodCode.setFocusable(false);
                prodCode.setFocusableInTouchMode(false);
                prodCode.setClickable(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdGst = prodGst.getText().toString();

                if(!mProdName.equals("") && !MProdMrp.equals("")) {
                    if(MProdGst.equals("")){
                        MProdGst = String.valueOf(0);
                    }
                    String key = String.valueOf(prodCode.getText());
                    databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                    databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                    databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                    databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
                    databaseReferenceProduct.child(key).child("key").setValue(key).toString();
                    Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

                    if (imm.isAcceptingText()) { // verify if the soft keyboard is open
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    HomeFragment homeFragment = new HomeFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                }else{
                    Toast.makeText(mContext,"Enter Valid Name And Rate",Toast.LENGTH_SHORT).show();
                }
            }
        });

        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdGst = prodGst.getText().toString();

                if(!mProdName.equals("") && !MProdMrp.equals("")) {
                    if(MProdGst.equals("")){
                        MProdGst = String.valueOf(0);
                    }
                    String key = String.valueOf(prodCode.getText());
                    databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                    databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                    databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                    databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
                    databaseReferenceProduct.child(key).child("key").setValue(key).toString();
                    Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();

//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//
//                    if (imm.isAcceptingText()) { // verify if the soft keyboard is open
//                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                    }
                    prodName.setText(null);
                    prodCode.setText(null);
                    prodMrp.setText(null);
                    prodGst.setText(null);
                    prodCode.setText(String.valueOf(Integer.parseInt(mProdCode)+1));
                    prodName.requestFocus();
                }else{
                    Toast.makeText(mContext,"Enter Valid Name And Rate",Toast.LENGTH_SHORT).show();
                }
            }
        });

//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
//                String mProdCode = prodCode.getText().toString();
//                String MProdMrp = prodMrp.getText().toString();
//                final String[] MProdGst = {prodGst.getText().toString()};
//
//                databaseReferenceProduct.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReferenceProduct.removeEventListener(this);
//
//                        if (!mProdName.equals("") && !mProdCode.equals("") && !MProdMrp.equals("")){
//                            if(MProdGst[0].equals("")){
//                                MProdGst[0] = String.valueOf(0);
//                            }
//                            databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
//                                    databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).removeEventListener(this);
//                                    if (!snapshot2.exists())
//                                    {
//                                        if(snapshot.exists()) {
//                                            String lastChildKey = String.valueOf(1);
//                                            for(DataSnapshot data : snapshot.getChildren()) {
//                                                lastChildKey = data.getKey();
//                                            }
//                                            int lastChildKeyI = Integer.valueOf(lastChildKey)+1;
//                                            String key = String.valueOf(lastChildKeyI);
//                                            databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
//                                            databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
//                                            databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
//                                            databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst[0]).toString();
//                                            databaseReferenceProduct.child(key).child("key").setValue(key).toString();
//                                            Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                            String key = String.valueOf(1);
//                                            databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
//                                            databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
//                                            databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
//                                            databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst[0]).toString();
//                                            databaseReferenceProduct.child(key).child("key").setValue(key).toString();
//                                            Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();
//                                        }
//                                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//
//                                        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
//                                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                                        }
//                                        HomeFragment homeFragment = new HomeFragment();
//                                        getParentFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
//                                    }else
//                                        Toast.makeText(mContext, "Code Already Used", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//
//                        }else
//                        {
//                            Toast.makeText(mContext, "Please entry valid values", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//        });
//
//        addMoreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String mProdName = prodName.getText().toString().toLowerCase(Locale.ROOT);
//                String mProdCode = prodCode.getText().toString();
//                String MProdMrp = prodMrp.getText().toString();
//                String MProdGst = prodGst.getText().toString();
//
//                databaseReferenceProduct.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReferenceProduct.removeEventListener(this);
//
//                        if (!mProdName.equals("") && !mProdCode.equals("") && !MProdMrp.equals("") && !MProdGst.equals("")){
//                            databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
//                                    databaseReferenceProduct.orderByChild("code").equalTo(mProdCode).removeEventListener(this);
//                                    if (!snapshot2.exists())
//                                    {
//                                        if(snapshot.exists()) {
//                                            String lastChildKey = String.valueOf(1);
//                                            for(DataSnapshot data : snapshot.getChildren()) {
//                                                lastChildKey = data.getKey();
//                                            }
//                                            int lastChildKeyI = Integer.valueOf(lastChildKey)+1;
//                                            String key = String.valueOf(lastChildKeyI);
//                                            databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
//                                            databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
//                                            databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
//                                            databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
//                                            databaseReferenceProduct.child(key).child("key").setValue(key).toString();
//                                            Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                            String key = String.valueOf(1);
//                                            databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
//                                            databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
//                                            databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
//                                            databaseReferenceProduct.child(key).child("gstAmt").setValue(MProdGst).toString();
//                                            databaseReferenceProduct.child(key).child("key").setValue(key).toString();
//                                            Toast.makeText(mContext, "Successfully Added product", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    }
//                                    else
//                                        Toast.makeText(mContext, "Code Already Used", Toast.LENGTH_SHORT).show();
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//
//                        }else
//                            Toast.makeText(mContext, "Please entry valid values", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                prodName.setText(null);
//                prodCode.setText(null);
//                prodMrp.setText(null);
//                prodGst.setText(null);
//                prodCode.setFocusable(true);
//                prodCode.requestFocus();
//            }
//        });

        return addProductsView;
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