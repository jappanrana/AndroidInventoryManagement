package com.example.androidinventorymanagement.Utils;

import static com.example.androidinventorymanagement.Utils.CommonMethods.CheckNumbers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinventorymanagement.Activities.QuoteItemsActivity;
import com.example.androidinventorymanagement.Adapters.PartyAdapter;
import com.example.androidinventorymanagement.Fragements.AddNewPartyQuotationFragment;
import com.example.androidinventorymanagement.Models.party;
import com.example.androidinventorymanagement.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    PartyAdapter partyAdapter;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the style of the dialog to a theme that has no background and no animations
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_details_layout, container, false);
        // Your code to initialize the views and set up the dialog

        mContext = getContext();

        TextInputEditText customerName = view.findViewById(R.id.customerNameSheetEditText);
//                TextInputEditText customerNo = userSheetDialog.findViewById(R.id.customerNoSheetEditText);
        CardView proceedBtn = view.findViewById(R.id.proceedButton);
        RecyclerView parties = view.findViewById(R.id.customerRecycler);

        parties.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference databaseReferenceParty = FirebaseDatabase.getInstance().getReference("party");

        FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                Builder<party>().setQuery(databaseReferenceParty,party.class).build();

        PartyAdapter.Partylistner listner = new PartyAdapter.Partylistner() {
            @Override
            public void clicked(party pty) {
                String userInpNumber = pty.getNumber();
                String FinalNumber = "+91 "+userInpNumber.substring(0,5)+" "+userInpNumber.substring(5);
                SharedPreferenceMethods.setSharedPrefCustomerName(mContext, pty.getName());
                SharedPreferenceMethods.setSharedPrefCustomerNumber(mContext,FinalNumber);

                SharedPreferenceMethods.setSharedPrefSharedQuote(mContext,false);

                dismiss();
//                userSheetDialog.dismiss();
                Intent intent1 = new Intent(getActivity(), QuoteItemsActivity.class);
                getActivity().startActivity(intent1);
            }
        };

        partyAdapter = new PartyAdapter(options,listner);
        parties.setAdapter(partyAdapter);
        partyAdapter.startListening();

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userSheetDialog.dismiss();
                dismiss();
                AddNewPartyQuotationFragment addNewPartyQuotationFragment = new AddNewPartyQuotationFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.frame, addNewPartyQuotationFragment).commit();
            }
        });

        customerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(CheckNumbers(s.toString())){
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("number").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    parties.setAdapter(partyAdapter);
                    partyAdapter.startListening();
                }else{
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("name").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    parties.setAdapter(partyAdapter);
                    partyAdapter.startListening();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(CheckNumbers(s.toString())){
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("number").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    parties.setAdapter(partyAdapter);
                    partyAdapter.startListening();
                }else{
                    FirebaseRecyclerOptions<party> options = new FirebaseRecyclerOptions.
                            Builder<party>().setQuery(databaseReferenceParty.orderByChild("name").startAt(s.toString().toLowerCase(Locale.ROOT)).endAt(s.toString().toLowerCase(Locale.ROOT)+"\uf8ff"),party.class).build();
                    partyAdapter = new PartyAdapter(options,listner);
                    parties.setAdapter(partyAdapter);
                    partyAdapter.startListening();
                }

//                        if(s.toString().trim().length()>0 && !customerNo.getText().toString().equals("")){
//                            proceedBtn.setCardBackgroundColor(Color.parseColor("#04B8E2"));
//                            proceedBtn.setEnabled(true);
//                        }else{
//                            proceedBtn.setCardBackgroundColor(Color.parseColor("#ABE7F5"));
//                            proceedBtn.setEnabled(false);
//                        }
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Set the state of the bottom sheet to expanded
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
        BottomSheetBehavior.from(bottomSheet).setHideable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
            }
        }

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            ViewTreeObserver viewTreeObserver = getView().getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

    }


}
