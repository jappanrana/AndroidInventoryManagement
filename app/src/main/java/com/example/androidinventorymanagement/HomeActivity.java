package com.example.androidinventorymanagement;

import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefBackState;
import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.getSharedPrefNavigation;
import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.setSharedPrefBackState;
import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.setSharedPrefNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidinventorymanagement.Activities.ScannerActivity;
import com.example.androidinventorymanagement.Fragements.EditQuoteFragment;
import com.example.androidinventorymanagement.Fragements.SharedQuoteFragment;
import com.example.androidinventorymanagement.Navigation.HomeFragment;
import com.example.androidinventorymanagement.Navigation.ProfileFragment;
import com.example.androidinventorymanagement.Utils.Constances;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    Context mContext;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = HomeActivity.this;

        setSharedPrefBackState(mContext,Constances.BACK_HOME);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        String navigation = getSharedPrefNavigation(mContext);

        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.scanner).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(false);

        if (navigation.equals(Constances.NAVIGATION_PROFILE)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileFragment()).commit();
            bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
        }
        else if (navigation.equals(Constances.NAVIGATION_EDITPROD_QUOTE)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new EditQuoteFragment()).commit();
        }
        else if (navigation.equals(Constances.NAVIGATION_SHARED_QUOTE)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SharedQuoteFragment()).commit();
            bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
        }

        setSharedPrefNavigation(mContext, Constances.NAVIGATION_HOME);

        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.findViewById(bottomNavigationView.getMenu().getItem(i).getItemId()).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            });

        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
                        return true;

                    case R.id.scanner:
                        Intent intent = new Intent(HomeActivity.this, ScannerActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.profile:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new ProfileFragment()).commit();
//                        return true;
                        Toast.makeText(mContext, "Access Denied", Toast.LENGTH_SHORT).show();
                        return false;

                }
                return false;
            }
        });

        bottomNavigationView.setItemIconTintList(null);
    }

    @Override
    public void onBackPressed()
    {
        String back = getSharedPrefBackState(mContext);
        if (Constances.BACK_ADD_PRODUCT.equals(back)){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
            setSharedPrefBackState(mContext,Constances.BACK_HOME);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Exit App")
                    .setMessage("Are you sure you want to leave the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
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
        }
    }
}