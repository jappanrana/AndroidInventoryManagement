package com.example.androidinventorymanagement.Navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.androidinventorymanagement.Activities.QuoteScannerActivity;
import com.example.androidinventorymanagement.Fragements.ProductFragment;
import com.example.androidinventorymanagement.Fragements.SharedQuoteFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.example.androidinventorymanagement.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    LinearLayout createGstQuote;
    LinearLayout logoutProfile,sharedQuote,productPage;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        createGstQuote = view.findViewById(R.id.gstQuote);
        logoutProfile = view.findViewById(R.id.logoutProfile);
        sharedQuote = view.findViewById(R.id.sharedQuote);
        productPage = view.findViewById(R.id.productPage);

        mContext = getContext();

        SharedPreferenceMethods.setSharedPrefNavigation(mContext,Constances.NAVIGATION_PROFILE);

        sharedQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceMethods.setSharedPrefSharedQuote(mContext, true);
                SharedQuoteFragment sharedQuoteFragment = new SharedQuoteFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, sharedQuoteFragment).commit();
            }
        });
        productPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment productFragment = new ProductFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, productFragment).commit();
            }
        });

        logoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                builder
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferenceMethods.setSharedPrefRegister(mContext,false);
                                SharedPreferenceMethods.setSharedPrefNavigation(mContext, Constances.NAVIGATION_HOME);
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton("No", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                Button btnPositive = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });

        createGstQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog userSheetDialog = new BottomSheetDialog(getContext(),R.style.CartDialog);
                userSheetDialog.setContentView(R.layout.customer_details_layout);
                userSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                userSheetDialog.getBehavior().setHideable(true);
                userSheetDialog.getBehavior().setSkipCollapsed(true);
                userSheetDialog.setCanceledOnTouchOutside(true);

                userSheetDialog.show();

                TextInputEditText customerName = userSheetDialog.findViewById(R.id.customerNameSheetEditText);
                TextInputEditText customerNo = userSheetDialog.findViewById(R.id.customerNoSheetEditText);
                CardView proceedBtn = userSheetDialog.findViewById(R.id.proceedButton);

                proceedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!customerName.getText().toString().equals("") && !customerNo.getText().toString().equals("") && customerNo.getText().toString().length() == 10)
                        {
                            String userInpNumber = customerNo.getText().toString();
                            String FinalNumber = "+91 "+userInpNumber.substring(0,5)+" "+userInpNumber.substring(5);
                            SharedPreferenceMethods.setSharedPrefCustomerName(mContext,customerName.getText().toString());
                            SharedPreferenceMethods.setSharedPrefCustomerNumber(mContext,FinalNumber);

                            SharedPreferenceMethods.setSharedPrefSharedQuote(mContext,false);

                            userSheetDialog.dismiss();
                            Intent intent1 = new Intent(getActivity(), QuoteScannerActivity.class);
                            getActivity().startActivity(intent1);
                        }
                        else
                            Toast.makeText(getContext(), "Please Provide Details", Toast.LENGTH_SHORT).show();
                    }
                });

                customerName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().trim().length()>0 && !customerNo.getText().toString().equals("")){
                            proceedBtn.setCardBackgroundColor(Color.parseColor("#04B8E2"));
                            proceedBtn.setEnabled(true);
                        }else{
                            proceedBtn.setCardBackgroundColor(Color.parseColor("#ABE7F5"));
                            proceedBtn.setEnabled(false);
                        }
                    }
                });

                customerNo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().trim().length()>0 && !customerName.getText().toString().equals("")){
                            proceedBtn.setCardBackgroundColor(Color.parseColor("#04B8E2"));
                            proceedBtn.setEnabled(true);
                        }else{
                            proceedBtn.setCardBackgroundColor(Color.parseColor("#ABE7F5"));
                            proceedBtn.setEnabled(false);
                        }
                    }
                });

            }
        });

        return view;
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