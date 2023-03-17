package com.example.androidinventorymanagement.Navigation;

import static com.example.androidinventorymanagement.Utils.CommonMethods.CheckNumbers;
import static com.example.androidinventorymanagement.Utils.SharedPreferenceMethods.setSharedPrefEditParty;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Adapters.PartyAdapter;
import com.example.androidinventorymanagement.Fragements.AddPartyFragment;
import com.example.androidinventorymanagement.Fragements.EditPartyFragment;
import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.R;
import com.example.androidinventorymanagement.Utils.Constances;
import com.example.androidinventorymanagement.Utils.SharedPreferenceMethods;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements EasyPermissions.PermissionCallbacks{

    Context mContext;

    RecyclerView partyRecycler;
    PartyAdapter partyAdapter;
    ConstraintLayout homeAddPartyBtn,homeFragmentLoader;
    ImageView homeLoadingAnimationImage;
    TextInputEditText partySearchHome;
    AnimatedVectorDrawable avd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = getContext();

        SharedPreferenceMethods.setSharedPrefNavigation(mContext,Constances.NAVIGATION_HOME);
        SharedPreferenceMethods.setSharedPrefBackState(mContext,Constances.BACK_HOME);

        partyRecycler = home.findViewById(R.id.partyRecycler);
        homeAddPartyBtn = home.findViewById(R.id.homeAddPartyBtn);
        partySearchHome = home.findViewById(R.id.partySearchHome);
        homeLoadingAnimationImage = home.findViewById(R.id.homeLoadingAnimationImage);
        homeFragmentLoader = home.findViewById(R.id.homeFragmentLoader);

        partyRecycler.setLayoutManager(new LinearLayoutManager(mContext){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                homeFragmentLoader.setVisibility(View.GONE);
            }
        });


        DatabaseReference databaseReferenceParty = FirebaseDatabase.getInstance().getReference("party");

        FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                Builder<party>().setQuery(databaseReferenceParty,party.class).build();

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

        PartyAdapter.Partylistner listner = new PartyAdapter.Partylistner() {
            @Override
            public void clicked(party pty) {
                setSharedPrefEditParty(getContext(),pty);
                EditPartyFragment editPartyFragment = new EditPartyFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, editPartyFragment).commit();
            }
        };

        partyAdapter = new PartyAdapter(options,listner);
        partyRecycler.setAdapter(partyAdapter);
        partyAdapter.startListening();
//        homeFragmentLoader.setVisibility(View.GONE);
//        Jappan Loading Image Stop when Recyclerview Loaded


        partySearchHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CheckNumbers(s.toString())){
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("number").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    partyRecycler.setAdapter(partyAdapter);
                    homeFragmentLoader.setVisibility(View.GONE);
                    partyAdapter.startListening();
                }else{
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("name").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    partyRecycler.setAdapter(partyAdapter);
                    homeFragmentLoader.setVisibility(View.GONE);
                    partyAdapter.startListening();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(CheckNumbers(s.toString())){
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("number").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    partyRecycler.setAdapter(partyAdapter);
                    homeFragmentLoader.setVisibility(View.GONE);
                    partyAdapter.startListening();
                }else{
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("name").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    partyRecycler.setAdapter(partyAdapter);
                    homeFragmentLoader.setVisibility(View.GONE);
                    partyAdapter.startListening();
                }
            }
        });

        homeAddPartyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPartyFragment addPartyFragment = new AddPartyFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, addPartyFragment).commit();
            }
        });

        getPermissions();

        return home;
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