package com.example.androidinventorymanagement.Fragements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidinventorymanagement.Activities.QuoteItemsActivity;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.CustomRangeInputFilter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class EditQuoteFragment extends Fragment {


    ImageView backBtn;
    SharedPreferences userDetail;
    String productName,productGst,productRate,productQty;
    EditText prodNameEditText,prodGstEditText,prodMrpEditText,prodQtyEditText;
    MaterialButton editSubmitBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_quote, container, false);
        backBtn = view.findViewById(R.id.editQuoteProductBackButton);
        prodNameEditText = view.findViewById(R.id.editName);
        prodGstEditText = view.findViewById(R.id.editGst);
        prodMrpEditText = view.findViewById(R.id.editRate);
        prodQtyEditText = view.findViewById(R.id.editQty);
        editSubmitBtn = view.findViewById(R.id.editSubmitBtn);

        prodQtyEditText.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,999f)});
        prodMrpEditText.setFilters(new InputFilter[]{new CustomRangeInputFilter(0f,100000f)});

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.home).setEnabled(false).setVisible(false);
        bottomNavigationView.getMenu().findItem(R.id.scanner).setEnabled(false).setVisible(false);
        bottomNavigationView.getMenu().findItem(R.id.profile).setEnabled(false).setVisible(false);

        userDetail = getContext().getSharedPreferences("userDetail",0);
        productName = userDetail.getString("productNameEdit","error");
        productGst = userDetail.getString("productGstEdit","error");
        productRate = userDetail.getString("productRateEdit","error");
        productQty = userDetail.getString("productQtyEdit","error");

        prodNameEditText.setText(productName);
        prodGstEditText.setText(productGst);
        prodMrpEditText.setText(productRate);
        prodQtyEditText.setText(productQty);


        editSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mProdName = prodNameEditText.getText().toString();
                String mProdGst = prodGstEditText.getText().toString();
                String MProdMrp = prodMrpEditText.getText().toString();
                String MproductQtyEdit = prodQtyEditText.getText().toString();
                String mProdGstText = mProdGst.replace("%","");

                if (!mProdName.equals("") && !mProdGst.equals("") && !MProdMrp.equals("")  && !MproductQtyEdit.equals(""))
                {
                    processEdit(mProdName,mProdGstText,MProdMrp,productName,MproductQtyEdit);
                }
                else
                {
                    Toast.makeText(getContext(), "Please Enter Values to Edit", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return view;
    }

    private void processEdit(String mProdName, String mProdGst, String mProdMrp, String productName, String mproductQtyEdit) {

        DbManager dbManager = new DbManager(getContext());
        dbManager.UpdateAll(mProdName,mProdGst,mProdMrp,productName,mproductQtyEdit);
        Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
        userDetail.edit().putBoolean("sharedQuote",false).apply();
        Intent intent = new Intent(getContext(), QuoteItemsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        prodGstEditText.clearFocus();
                        prodNameEditText.clearFocus();
                        prodMrpEditText.clearFocus();
                        getActivity().finish();
//                        HomeFragment homeFragment = new HomeFragment();
//                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navBarBottom);
//                        bottomNavigationView.getMenu().findItem(R.id.navigation_order).setChecked(true);
                        // getActivity().finish();
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                        return true;
                    }
                }
                return false;
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }
        prodNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    prodNameEditText.clearFocus();
                    getView().requestFocus();
                }
                return false;
            }
        });
        prodMrpEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    prodNameEditText.clearFocus();
                    getView().requestFocus();
                }
                return false;
            }
        });
        prodGstEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    prodNameEditText.clearFocus();
                    getView().requestFocus();
                }
                return false;
            }
        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }
}