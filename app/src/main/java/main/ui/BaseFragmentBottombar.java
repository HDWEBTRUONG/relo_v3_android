package main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import framework.phvtFragment.BaseFragment;
import main.R;

/**
 * Created by tonkhanh on 5/22/17.
 */

public abstract class BaseFragmentBottombar extends BaseFragment {

    protected LinearLayout lnBottom;
    protected ImageView imvBackBottomBar;
    protected ImageView imvForwardBottomBar;
    protected ImageView imvBrowserBottomBar;
    protected ImageView imvReloadBottomBar;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lnBottom = (LinearLayout) view.findViewById(R.id.lnBottom);
        imvBackBottomBar = (ImageView) view.findViewById(R.id.imvBackBottomBar);
        imvForwardBottomBar = (ImageView) view.findViewById(R.id.imvForwardBottomBar);
        imvBrowserBottomBar = (ImageView) view.findViewById(R.id.imvBrowserBottomBar);
        imvReloadBottomBar = (ImageView) view.findViewById(R.id.imvReloadBottomBar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupBottombar();
    }

    public abstract void setupBottombar();
}