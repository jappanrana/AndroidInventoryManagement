package com.example.androidinventorymanagement.Fragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidinventorymanagement.R;


public class ViewPartyTransactionsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View viewPartyTransaction = inflater.inflate(R.layout.fragment_view_party_transactions, container, false);
        // Inflate the layout for this fragment
        return viewPartyTransaction;
    }
}