package com.android.mobileplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.mobileplayer.base.BasePager;

public class ReplaceFragment extends Fragment {

    public ReplaceFragment() {
    }

    private BasePager currPager;

    public ReplaceFragment(BasePager pager) {

        this.currPager=pager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootview;
    }
}