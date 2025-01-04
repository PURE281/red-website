package com.jbb.library_common;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.jbb.library_common.basemvp.BaseActivity;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.log.LogUtil;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WebActivity extends BaseActivity {

    private AgentWeb mAgentWeb;
    LinearLayout container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void init() {
        final String title2 = getIntent().getStringExtra(KeyContacts.KEY_TITLE);
        String webUrl = getIntent().getStringExtra(KeyContacts.KEY_URL);


        container = findViewById(R.id.container);
//        container.heig

//        setTitle(title);


        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                .useDefaultIndicator()
//                .setAgentWebWebSettings(new CustomSettings())
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
//                        if(title2!=null) title = title2;
//                        mTitle.setText(title);
//                        if(title != null && title.length() > 20){
//                            mTitle.setTextSize(14);
//                        }
                    }
                })
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onLoadResource(WebView view, String url) {
                        LogUtil.d("拦截URL>>>>>>:" + url);

                        super.onLoadResource(view, url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
//                        if (!TextUtils.isEmpty(params)) {
//                            mAgentWeb.getJsAccessEntrace().callJs("javascript:insuranceParameter('" + params + "')");
//                        }
//                        if (!TextUtils.isEmpty(EPayParam)) {
//                            mAgentWeb.getJsAccessEntrace().quickCallJs("javascript:fromPost('" + megaPayUrl + "','" + EPayParam + "')");
//                        }
                    }


                })
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(webUrl + "?token=" + getIntent().getStringExtra("token"));

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new MyJavaScripInterface(this));
//        WebView webView = mAgentWeb.getWebCreator().getWebView();
//        webView.addJavascriptInterface(new AndroidInterface(),"android");
    }

    @Override
    public int getLayoutId() {
        return R.layout.webview_activity;
    }

    @Override
    public String getATitle() {
        return "";
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }


    public class AndroidInterface {

        @JavascriptInterface
        public void login() {
        }

        @JavascriptInterface
        public void close() {
            finish();
        }

        @JavascriptInterface
        public void appPay(String json) {

        }


    }

}
