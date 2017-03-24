package main.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import framework.phvtFragment.BaseFragment;
import main.R;
import main.ReloApp;
import main.util.Constant;

/**
 * Created by HuyTran on 3/21/17.
 */

public class CouponListFragment extends BaseFragment {

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_coupon_list;
    }

    @Override
    protected void getMandatoryViews(View root, Bundle savedInstanceState) {
        ButterKnife.bind(this, root);
        Toolbar toolbar = ButterKnife.findById(root, R.id.toolbar);
        TextView title = ButterKnife.findById(toolbar, R.id.toolbar_title);
        title.setText(R.string.title_coupon_list);
    }

    @Override
    protected void registerEventHandlers() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //Test GA
        Activity act = getActivity();
        if (act != null) {
            ReloApp app = (ReloApp) act.getApplication();
            app.trackingAnalytics(Constant.GA_POPULAR_COUPON_SCREEN);
        }
    }
}