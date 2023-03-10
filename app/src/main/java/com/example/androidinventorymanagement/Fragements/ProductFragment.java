package com.example.androidinventorymanagement.Fragements;

import static com.example.androidinventorymanagement.Utils.CommonMethods.CheckNumbers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidinventorymanagement.Adapters.ProductsAdapter;
import com.example.androidinventorymanagement.ExportScreens.PDFExportActivity;
import com.example.androidinventorymanagement.ExportScreens.PDFExportAllActivity;
import com.example.androidinventorymanagement.Models.ExportModel;
import com.example.androidinventorymanagement.Models.ProductsModel;
import com.example.androidinventorymanagement.Navigation.ProfileFragment;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.SqlDB.DbManager;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class ProductFragment extends Fragment {

    Context mContext;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceProducts;
    ProductsAdapter adapter;
    ImageView homeFragmentAdd, homeFragmentExport, homeLoadingAnimationImage;
    LinearLayout homeFragmentSelectMenu;
    EditText homeFragmentSearchView;
    TextView homeFragmentEmpty;
    RecyclerView homeFragmentRecyclerView;
    ConstraintLayout homeFragmentLoader;
    AnimatedVectorDrawable avd;
    ArrayList<ExportModel> SelectedList = new ArrayList<>();
    String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View productView = inflater.inflate(R.layout.fragment_product, container, false);

        mContext = getContext();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        userRole = SharedPreferenceMethods.getSharedPrefUserRole(mContext);

        SharedPreferenceMethods.setSharedPrefNavigation(mContext,Constances.NAVIGATION_PRODUCT);

//        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(false);

        homeFragmentAdd = productView.findViewById(R.id.homeFragmentAdd);
        homeFragmentExport = productView.findViewById(R.id.homeFragmentExport);
        homeFragmentSelectMenu = productView.findViewById(R.id.homeFragmentSelectMenu);
        homeFragmentSearchView = productView.findViewById(R.id.homeFragmentSearchView);
        homeFragmentEmpty = productView.findViewById(R.id.homeFragmentEmpty);
        homeFragmentRecyclerView = productView.findViewById(R.id.homeFragmentRecyclerView);
        homeLoadingAnimationImage = productView.findViewById(R.id.homeLoadingAnimationImage);
        homeFragmentLoader = productView.findViewById(R.id.homeFragmentLoader);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProducts = firebaseDatabase.getReference("Products");

        Drawable drawable = homeLoadingAnimationImage.getDrawable();

        if (drawable instanceof AnimatedVectorDrawable) {
            avd = (AnimatedVectorDrawable) drawable;
            avd.start();

            ((AnimatedVectorDrawable) drawable).registerAnimationCallback(new Animatable2.AnimationCallback() {

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    avd.start();

                }
            });
        }

        //Delete Database for quote
        DbManager dbManager = new DbManager(getContext());
        dbManager.deleteAll();

        homeFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<ProductsModel> options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(databaseReferenceProducts.orderByKey(), ProductsModel.class).build();

        adapter = new ProductsAdapter(options, getContext(), homeFragmentSelectMenu);

        adapter.notifyDataSetChanged();
        homeFragmentRecyclerView.setItemViewCacheSize(90);
        homeFragmentRecyclerView.setItemAnimator(null);
        databaseReferenceProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReferenceProducts.removeEventListener(this);
                if(snapshot.exists()){
                    homeFragmentRecyclerView.setAdapter(adapter);
                    homeFragmentLoader.setVisibility(View.GONE);
                } else
                    homeFragmentEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.startListening();
//        homeFragmentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                processesSearch(query.toLowerCase(Locale.ROOT),homeFragmentSelectMenu);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                processesSearch(newText.toLowerCase(Locale.ROOT),homeFragmentSelectMenu);
//                return false;
//            }
//        });

        homeFragmentSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                processesSearch(s.toString().toLowerCase(Locale.ROOT),homeFragmentSelectMenu);
            }

            @Override
            public void afterTextChanged(Editable s) {
                processesSearch(s.toString().toLowerCase(Locale.ROOT),homeFragmentSelectMenu);
            }
        });

        homeFragmentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRole.equals(Constances.USER_ROLE_ADMIN)){
                    AddProductsFragment addProductsFragment = new AddProductsFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame, addProductsFragment).commit();
                }else{
                    Toast.makeText(mContext,"Access Only to Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.setOnLongClickListner(new ProductsAdapter.onCardLongClickListner() {
            @Override
            public void onLongCardClick(DataSnapshot snapshot, int position, ImageView checkImage, ConstraintLayout productViewLayout) {
                ExportModel clickedItem = new ExportModel(snapshot.getKey().toString(),snapshot.child("name").getValue().toString(),snapshot.child("code").getValue().toString(),snapshot.child("mrp").getValue().toString());
                if(checkImage.getVisibility() == View.GONE){
                    checkImage.setVisibility(View.VISIBLE);
                    productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                    SelectedList.add(clickedItem);
                    adapter.setSelectable(true);
                }
            }
        });

        ProductsAdapter.onCardClickListner cardClickListner = new ProductsAdapter.onCardClickListner() {
            @Override
            public void onCardClick(DataSnapshot snapshot, int position, ImageView checkImage, ConstraintLayout productViewLayout) {
                if(userRole.equals(Constances.USER_ROLE_ADMIN)){
                    ExportModel clickedItem = new ExportModel(snapshot.getKey().toString(),snapshot.child("name").getValue().toString(),snapshot.child("code").getValue().toString(),snapshot.child("mrp").getValue().toString());
                    if(adapter.getSelectable()){
                        if (checkImage.getVisibility() == View.VISIBLE){
                            checkImage.setVisibility(View.GONE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < SelectedList.size(); i++) {
                                ExportModel temp = SelectedList.get(i);
                                if(temp.getKey() == clickedItem.getKey()){
                                    SelectedList.remove(i);
                                }
                            }
                            SelectedList.remove(clickedItem);
                            if(SelectedList.isEmpty()){
                                adapter.setSelectable(false);
                            }
                        }else{
                            checkImage.setVisibility(View.VISIBLE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                            if(!SelectedList.contains(clickedItem)) {
                                SelectedList.add(clickedItem);
                            }
                        }
                    }else{
                        ProductsModel showProduct = new ProductsModel(
                                snapshot.child("name").getValue().toString(),
                                snapshot.child("code").getValue().toString(),
                                snapshot.child("mrp").getValue().toString(),
                                snapshot.child("gstAmt").getValue().toString(),
                                snapshot.child("key").getValue().toString(),
                                snapshot.child("measurement").getValue().toString()
                        );
                        SharedPreferenceMethods.setSharedPrefShowProduct(mContext,showProduct);
                        ShowProductFragment showProductFragment = new ShowProductFragment();
                        getParentFragmentManager().beginTransaction().replace(R.id.frame, showProductFragment).commit();
                    }
                }
            }
        };
        homeFragmentExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals(Constances.USER_ROLE_ADMIN)){
                    Intent intent = new Intent(getContext(), PDFExportActivity.class);
                    if(!SelectedList.isEmpty()){
                        Toast.makeText(getContext(), "export", Toast.LENGTH_SHORT).show();
                        Bundle args = new Bundle();
                        args.putSerializable("SelectedList",(Serializable) SelectedList);
                        intent.putExtra("SelectedListIntent",args);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), "No product to export", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext,"Access Only to Admin", Toast.LENGTH_SHORT).show();
                }

            }
        });
        homeFragmentSelectMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRole.equals(Constances.USER_ROLE_ADMIN)){
                    PopupMenu popupMenu = new PopupMenu(getContext(), homeFragmentSelectMenu);
                    popupMenu.getMenuInflater().inflate(R.menu.select_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_export_all:
                                    Intent intent = new Intent(getContext(), PDFExportAllActivity.class);
                                    startActivity(intent);
                                    return true;
                                case R.id.menu_delete:
                                    if(!SelectedList.isEmpty()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                                        builder.setTitle("Delete Product")
                                                .setMessage("Do you want to delete these products?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        for (int i = 0; i < SelectedList.size(); i++) {
                                                            ExportModel temp = SelectedList.get(i);
                                                            String key = temp.getKey();
                                                            databaseReferenceProducts.child(key).removeValue();
                                                        }
                                                        adapter.setSelectable(false);
                                                    }

                                                })
                                                .setNegativeButton("No", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();

                                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                                        Button btnPositive = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                                        Button btnNegative = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                                        layoutParams.weight = 10;
                                        btnPositive.setLayoutParams(layoutParams);
                                        btnNegative.setLayoutParams(layoutParams);

                                    }else{
                                        Toast.makeText(getContext(), "No product to delete", Toast.LENGTH_SHORT).show();
                                    }
                                    return true;
                                default:
                                    Toast.makeText(getContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }else{
                    Toast.makeText(mContext,"Access Only to Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setOnClickListner(cardClickListner);

        return productView;
    }

    private void processesSearch(String query, LinearLayout selectMenuOption) {

        FirebaseRecyclerOptions<ProductsModel> options;
        if(query.isEmpty()){
            options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                    .setQuery(databaseReferenceProducts.orderByKey(),ProductsModel.class).build();
        }else if(CheckNumbers(query.toString())){
            options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                    .setQuery(databaseReferenceProducts.orderByChild("code").startAt(query).endAt(query+ "\uf8ff"),ProductsModel.class).build();
        }else{
            options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                    .setQuery(databaseReferenceProducts.orderByChild("name").startAt(query).endAt(query+ "\uf8ff"),ProductsModel.class).build();
        }

        adapter = new ProductsAdapter(options, getContext(), selectMenuOption);
        adapter.startListening();
        homeFragmentRecyclerView.setAdapter(adapter);

        ProductsAdapter.onCardClickListner cardClickListner = new ProductsAdapter.onCardClickListner() {
            @Override
            public void onCardClick(DataSnapshot snapshot, int position, ImageView checkImage, ConstraintLayout productViewLayout) {
                if(userRole.equals(Constances.USER_ROLE_ADMIN)) {
                    ExportModel clickedItem = new ExportModel(snapshot.getKey().toString(), snapshot.child("name").getValue().toString(), snapshot.child("code").getValue().toString(), snapshot.child("mrp").getValue().toString());

                    if (adapter.getSelectable()) {
                        if (checkImage.getVisibility() == View.VISIBLE) {
                            checkImage.setVisibility(View.GONE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                            for (int i = 0; i < SelectedList.size(); i++) {
                                ExportModel temp = SelectedList.get(i);
                                if (temp.getKey() == clickedItem.getKey()) {
                                    SelectedList.remove(i);
                                }
                            }
                            SelectedList.remove(clickedItem);
                            if (SelectedList.isEmpty()) {
                                adapter.setSelectable(false);
                            }
                        } else {
                            checkImage.setVisibility(View.VISIBLE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                            if (!SelectedList.contains(clickedItem)) {
                                SelectedList.add(clickedItem);
                            }
                        }
                    } else {
                        ProductsModel showProduct = new ProductsModel(
                                snapshot.child("name").getValue().toString(),
                                snapshot.child("code").getValue().toString(),
                                snapshot.child("mrp").getValue().toString(),
                                snapshot.child("gstAmt").getValue().toString(),
                                snapshot.child("key").getValue().toString(),
                                snapshot.child("measurement").getValue().toString()
                        );
                        SharedPreferenceMethods.setSharedPrefShowProduct(mContext,showProduct);
                        ShowProductFragment showProductFragment = new ShowProductFragment();
                        getParentFragmentManager().beginTransaction().replace(R.id.frame, showProductFragment).commit();
                    }
                }
            }
        };

        adapter.setOnClickListner(cardClickListner);

        adapter.setOnLongClickListner(new ProductsAdapter.onCardLongClickListner() {
            @Override
            public void onLongCardClick(DataSnapshot snapshot, int position, ImageView checkImage,ConstraintLayout productViewLayout) {
                if(userRole.equals(Constances.USER_ROLE_ADMIN)){
                    ExportModel clickedItem = new ExportModel(snapshot.getKey(),snapshot.child("name").getValue().toString(),snapshot.child("code").getValue().toString(),snapshot.child("mrp").getValue().toString());
                    if(checkImage.getVisibility() == View.GONE){
                        checkImage.setVisibility(View.VISIBLE);
                        productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                        SelectedList.add(clickedItem);
                        adapter.setSelectable(true);
                    }
                }else{
                    Toast.makeText(mContext, "Access Only to Admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                        if(SelectedList.size()!=0){
                            SelectedList = new ArrayList<>();
                            adapter.setSelectable(false);
                            adapter.notifyDataSetChanged();
                        }else{
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileFragment()).commit();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}