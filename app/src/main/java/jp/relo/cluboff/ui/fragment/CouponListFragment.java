package jp.relo.cluboff.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import framework.phvtFragment.BaseFragment;
import jp.relo.cluboff.R;
import jp.relo.cluboff.ReloApp;
import jp.relo.cluboff.database.MyDatabaseHelper;
import jp.relo.cluboff.model.CouponDTO;
import jp.relo.cluboff.ui.BaseFragmentBottombar;
import jp.relo.cluboff.ui.activity.WebviewActivity;
import jp.relo.cluboff.ui.adapter.CouponListAdapter;
import jp.relo.cluboff.util.Constant;

/**
 * Created by HuyTran on 3/21/17.
 */

public class CouponListFragment extends BaseFragment implements View.OnClickListener,CouponListAdapter.iClickButton{

    LinearLayout lnCatalory;
    ListView lvCategoryMenu;
    MaterialSpinner spinner;
    MyDatabaseHelper myDatabaseHelper;
    CouponListAdapter adapter;
    ArrayList<CouponDTO> listCoupon=new ArrayList<>();
    public static String WILL_NET_SERVER="1";
    public switchFragment iSwitchFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabaseHelper=new MyDatabaseHelper(getActivity());
    }
    public void setiSwitchFragment(switchFragment iSwitchFragment){
      this.iSwitchFragment = iSwitchFragment;
    }

    private void init(View view) {
        lnCatalory = (LinearLayout) view.findViewById(R.id.lnCatalory);
        lvCategoryMenu = (ListView) view.findViewById(R.id.list_category_listview);
        spinner = (MaterialSpinner) view.findViewById(R.id.spinnerCategory);
        spinner.setItems("カテゴリを選ぶ 1", "カテゴリを選ぶ 2", "カテゴリを選ぶ 3", "カテゴリを選ぶ 4", "カテゴリを選ぶ 5");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Toast.makeText(getActivity(), "Clicked " + item, Toast.LENGTH_SHORT).show();
            }
        });
        lnCatalory.setOnClickListener(this);
    }

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_coupon_list;
    }
    @Override
    protected void getMandatoryViews(View root, Bundle savedInstanceState) {
        init(root);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listCoupon = getListData();
        setAdapter();
    }


    private ArrayList getListData() {
        ArrayList<CouponDTO> results = new ArrayList<CouponDTO>();
        results = myDatabaseHelper.getCouponWithDate();
        // Add some more dummy data for testing
        return results;
    }
    private void setAdapter(){
        if(adapter==null){
            adapter =new CouponListAdapter(getContext(), listCoupon,this);
        }else{
            adapter.notifyDataSetChanged();
        }
        lvCategoryMenu.setAdapter(adapter);
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
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lnCatalory){
            //clickCategoryMenu();
            spinner.expand();
        }
    }
    @Override
    public void callback(CouponDTO data) {
        Toast.makeText(getActivity(),"Click: "+data.getCoupon_name(),Toast.LENGTH_SHORT).show();
        String url="";
        if(data.getCoupon_type().equals(WILL_NET_SERVER)){
            url =data.getLink_path();
        }else{
            //ToDo add url
            url = "https://www.google.com.vn/?gfe_rd=cr&ei=MgdaWZiME6Kl8weVlaiYBQ#q=android";//""+ data.getLink_path();
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_LOGIN_URL, url);
        bundle.putInt(Constant.KEY_CHECK_WEBVIEW,Constant.DETAIL_COUPON);
        iSwitchFragment.callSwitchFragment(bundle);
    }

    @Override
    public void like(int id) {
        myDatabaseHelper.likeCoupon(id);
        for(CouponDTO item : listCoupon){
            if(item.getID() == id){
                item.setLiked(1);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    interface switchFragment{
        void callSwitchFragment(Bundle bundle);
    }
}