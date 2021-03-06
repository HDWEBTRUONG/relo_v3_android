package net.fukuri.memberapp.memberapp.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import framework.phvtUtils.AppLog;

/**
 * My WEBVIEW
 * <p/>
 * Handle Proicessing for webview widget controller in App
 * Speciality in ShouldOverride Url through Open WebBrowser and
 * update something from Schema URL callBack
 * <p/>
 * lastupate : Phatvt
 * at : 2016 - 09 - 12
 */

public class MyWebViewClient extends WebViewClient {

    public static final String IS_UPDATE_OBJECT_ID = "IS_UPDATE_OBJECT_ID";
    //handle Activity
    private Activity activity;

    //Main user info in App
//    private UserInfo mUserInfo;


    //Initilized Class
    public MyWebViewClient(Activity activity) {
        this.activity = activity;
    }

    // OVERRIDE METHOD

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        AppLog.log("PHVT", "Current url loading : " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        AppLog.log("PHVT", "Current url loading completed !  : " + url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        // TO DO Auto-generated method stub
        super.onLoadResource(view, url);
    }

    /**
     * Processing for URL open by Extenal Browser on Device
     *
     * @param view
     * @param url
     * @return Extended Browser
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        AppLog.log("PHVT", "Full should open by Extend Browser : " + url);

        // Processing for : Openning  product detail
        if (url.contains("detail.html")) {
            openWebView(url, "", true);
            return true;
        } else

            // Processing for SCHEMAL URL CALLBACK
/*            if (url.startsWith(Constant.SCHEME_ANPANMANFANCLUB)) {

                // Callback: Update ObjectId
                if (url.startsWith(Constant.HOST_ANPANMANFANCLUB_UPDATE_OBJECT)) {
                    updateObjectID(getFullObjectIDBySchemal(url));
                }

                // Callback: Open Extend Browser on Device through url string
                else if (url.startsWith(Constant.HOST_ANPANMANFANCLUB_OPEN_BROWSER)) {

                    // BLOCK Split URL and Params
                    String url_open_browser = "";
                    url_open_browser = getFullUrlBySchemal(url);
                    AppLog.log("PHVT", " Open URL after processing : " + url_open_browser);

                    // OPEN WEB BROWSER
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url_open_browser));
                    activity.startActivity(i);

                }
                return true;
            }*/
        //test debug scheme update object
//        else if(url.contains("http://stg-fcapp.anpanman.jp/account/F-2-1.html")){
//            updateObjectID("v0WytbqKna1uKuUR");
//                }


        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        Common.onSslError(activity, view, handler, error);
    }

    //Open Fragment Dialog load URL
    public void openWebView(String url, String title, boolean isDetails) {
//        DialogFragment fragment = WebViewFragment.newInstance(url, title, isDetails);
//        fragment.show(activity.getFragmentManager(), WebViewFragment.class.getName());
    }

    /**
     * get Full URL by Schemal Links
     *
     * @param schemaUrl
     * @return
     */
    public String getFullUrlBySchemal(String schemaUrl) {
        StringBuilder res = new StringBuilder();
        final String PREFIX_URL_SYMBOL = "url=";

        try {
            if (schemaUrl.indexOf('?') > 1) {
                String[] allStrings = schemaUrl.split("\\?");
                int n = allStrings.length;
                if (n >= 2) {
                    for (int i = 1; i < n; i++) {
                        res.append(allStrings[i]);
                        if (i + 1 < n)
                            res.append("?");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res.toString().substring(PREFIX_URL_SYMBOL.length());
    }


    /**
     * get Object ID by Schemal URL
     *
     * @param schemaUrl
     * @return
     */
    public String getFullObjectIDBySchemal(String schemaUrl) {
        StringBuilder res = new StringBuilder();
        final String PREFIX_ID_SYMBOL = "id=";

        try {
            if (schemaUrl.indexOf('?') > 1) {
                String[] allStrings = schemaUrl.split("\\?");
                int n = allStrings.length;
                if (n >= 2) {
                    res.append(allStrings[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res.toString().substring(PREFIX_ID_SYMBOL.length());
    }


    /*
    *
    * FUNC:   Update USER_ID into SHARE PREFERENCES
    *
    */
    /*public void updateObjectID(String object_id) {

        //Call API to get user info in general
        RestfulUtil.getUserInfo(this.activity, object_id, new RestfulService.Callback() {
            @Override
            public void onDownloadSuccessfully(Object data, int requestCode, int responseCode) {

                // update User info into Application
                mUserInfo = (UserInfo) data;
                ((AnpanmanApp) (activity.getApplication())).updateUserInfo(mUserInfo);

                // save it to SHARE PREFERENCES
                SharedPreferencesUtil.putString(activity, Constant.PREF_USER_INFO, mUserInfo.toJson());

                //update info from main Activity
                ((MainActivity)activity)._updateUserInfoListener.update(mUserInfo);
            }

            @Override
            public void onDownloadFailed(Exception e, int requestCode, int responseCode) {

            }
        });


    }*/
}