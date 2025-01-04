package com.jbb.library_common.widght;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jbb.library_common.MyJavaScripInterface;
import com.jbb.library_common.R;
import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.download.AppDownloadService;
import com.jbb.library_common.utils.log.LogUtil;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;

import java.io.File;


public class XWebView extends WebView {

	public static final String USERAGENT_UC = " UCBrowser/11.6.4.950 ";
	public static final String USERAGENT_QQ_BROWSER = " MQQBrowser/8.0 ";
	public static final String USERAGENT_AGENTWEB = " " + AgentWebConfig.AGENTWEB_VERSION + " ";

	private LinearLayout parentLayout;
	private int appTitle;
	LinearLayout titleLayout;

	private Activity context;
	private WebSettings mWebSettings;
	WebViewBackFace backFace;
	private String downUrl;
	public static final int GET_UNKNOWN_APP_SOURCES = 111;
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mUploadCallbackAboveL;
	private TakePhotoUtil photoUtil;
	private File tempFile;
	private int oldContentHeight;

//	private boolean mIsPageLoading= false;

	public File getTempFile() {
		return tempFile;
	}

	public Uri getImageUri(){
		if(photoUtil != null){
			return photoUtil.getImageUri();
		}
		return null;
	}

	public void setParentLayout(LinearLayout parentLayout, int appTitle, LinearLayout titleLayout){
		this.parentLayout = parentLayout;
		this.appTitle = appTitle;
		this.titleLayout = titleLayout;
	}

	public XWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (Activity) context;
		initView();
	}

	public XWebView(Context context) {
		super(context);
		this.context = (Activity) context;
		initView();
	}




	private void initView() {

		settings();
//		mWebSettings = this.getSettings();

		// 设置javaScript可用
//		mWebSettings.setJavaScriptEnabled(true);
////		webSettings.setDefaultTextEncodingName("UTF-8");
//		mWebSettings.setMinimumFontSize(CommUtil.dp2px(14));//设置最小字体大小
////		webSettings.setTextZoom(100);
//		//设置自适应屏幕，两者合用
//		mWebSettings.setUseWideViewPort(true);//Webivew支持<meta>标签的viewport属性
//		mWebSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
//		//启用地理定位
////		webSettings.setGeolocationEnabled(true);
//		mWebSettings.setDomStorageEnabled(true);
////		webSettings.setAppCacheEnabled(true);// 设置App的缓存
////		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存：
//		mWebSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
//		mWebSettings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
//		// 是否允许通过file url加载的Javascript读取本地文件，默认值 false
//		mWebSettings.setAllowFileAccessFromFileURLs(true);
////		webSettings.setBlockNetworkImage(false);//同步请求图片
//
//		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//			mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

		this.addJavascriptInterface(new MyJavaScripInterface(context),"android");
		setLayerType(View.LAYER_TYPE_NONE, null);
		this.setWebViewClient(new MyWebViewClient());

		this.setWebChromeClient(new MyWebChromeClient());
		this.setDownloadListener(new MyDownLoadListeren());
	}


	private void settings() {
		mWebSettings = getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setSupportZoom(true);
		mWebSettings.setBuiltInZoomControls(false);
		mWebSettings.setSavePassword(false);
		if (AgentWebUtils.checkNetwork(context)) {
			//根据cache-control获取数据。
			mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			//没网，则从本地获取，即离线加载
			mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			//适配5.0不允许http和https混合使用情况
			mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mWebSettings.setTextZoom(100);
		mWebSettings.setDatabaseEnabled(true);
		mWebSettings.setAppCacheEnabled(true);
		mWebSettings.setLoadsImagesAutomatically(true);
		mWebSettings.setSupportMultipleWindows(false);
		// 是否阻塞加载网络图片  协议http or https
		mWebSettings.setBlockNetworkImage(false);
		// 允许加载本地文件html  file协议
		mWebSettings.setAllowFileAccess(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
			mWebSettings.setAllowFileAccessFromFileURLs(false);
			// 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
			mWebSettings.setAllowUniversalAccessFromFileURLs(false);
		}
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

			mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		} else {
			mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		}
		mWebSettings.setLoadWithOverviewMode(false);
		mWebSettings.setUseWideViewPort(false);
		mWebSettings.setDomStorageEnabled(true);
		mWebSettings.setNeedInitialFocus(true);
		mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
		mWebSettings.setDefaultFontSize(14);
		mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
		mWebSettings.setGeolocationEnabled(true);
		String dir = AgentWebConfig.getCachePath(context);
//		LogUtils.i(TAG, "dir:" + dir + "   appcache:" + AgentWebConfig.getCachePath(context));
		//设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
		mWebSettings.setGeolocationDatabasePath(dir);
		mWebSettings.setDatabasePath(dir);
		mWebSettings.setAppCachePath(dir);
		//缓存文件最大值
		mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
		mWebSettings.setUserAgentString(mWebSettings
				.getUserAgentString()
				.concat(USERAGENT_AGENTWEB)
				.concat(USERAGENT_UC)
		);
//		LogUtils.i(TAG, "UserAgentString : " + mWebSettings.getUserAgentString());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			// 安卓9.0后不允许多进程使用同一个数据目录，需设置前缀来区分
			// 参阅 https://blog.csdn.net/lvshuchangyin/article/details/89446629
//			Context context = webView.getContext();
//			String processName = ProcessUtils.getCurrentProcessName(context);
//			if (!context.getApplicationContext().getPackageName().equals(processName)) {
//				WebView.setDataDirectorySuffix(processName);
//			}
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		reSetHeight();
	}


	private void reSetHeight() {
		int contentHeight = getContentHeight();
		LogUtil.w("webview height = " + contentHeight);
		if(loadFinishListeren != null)
			loadFinishListeren.loadFinish(contentHeight);
//		if (oldContentHeight < contentHeight) {
//			oldContentHeight = contentHeight;
//			if(loadFinishListeren != null)
//				loadFinishListeren.loadFinish(contentHeight);
//		}
	}


	public void loadURL(String url) {
		LogUtil.d("http webview = " +url);
		if (!TextUtils.isEmpty(url)) {
			this.loadUrl(url);
		} else {

		}
	}

    public void setTitle(String title) {

    }


    private class MyWebChromeClient extends WebChromeClient {

		private View mCustomView;
		private CustomViewCallback mCustomViewCallback;

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(backFace != null)
				backFace.progress(newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if(backFace != null)
				backFace.title(title);
		}


		// For Android 3.0-
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			LogUtil.d("openFileChoose(ValueCallback<Uri> uploadMsg)");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg,null);

		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
			LogUtil.d("openFileChoose( ValueCallback uploadMsg, String acceptType )");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg,acceptType);
		}

		//For Android 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			LogUtil.d("openFileChoose(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
			if(backFace != null)
				backFace.faceOpenFileChooser(uploadMsg,acceptType);
		}

		// For Android 5.0+
		public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			LogUtil.d("onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
			if(backFace != null)
				backFace.faceOnShowFileChooser(filePathCallback,fileChooserParams);
			return true;
		}




		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			mCustomView = view;
			parentLayout.addView(mCustomView);
			mCustomViewCallback = callback;
			setVisibility(View.GONE);
			titleLayout.setVisibility(GONE);
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		public void onHideCustomView() {
			setVisibility(View.VISIBLE);
			if(appTitle == 1){
				titleLayout.setVisibility(VISIBLE);
			}
			if (mCustomView == null) {
				return;
			}
			mCustomView.setVisibility(View.GONE);
			parentLayout.removeView(mCustomView);
			mCustomViewCallback.onCustomViewHidden();
			mCustomView = null;
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			super.onHideCustomView();
		}

	}



	private class MyWebViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(backFace != null)
				backFace.onPageStart();
			if (url.startsWith("tmast://appdetails")) {
				try {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				} catch (Exception e) {
				}
				return true;
			} else if (url.startsWith("market://details?id=")) {//跳转应用市场
				LogUtil.d("url = " + url);
				toMarket(context, url, null);
				return true;
			}

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			mIsPageLoading = false;
			if(backFace != null)
				backFace.onPageFinished();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
//			mIsPageLoading = true;
			if(backFace != null)
				backFace.onPageStart();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

				LogUtil.w("error : url = " +  failingUrl + " " + description);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}

	public void setLoadFinishListeren(LoadFinishListeren loadFinishListeren) {
		this.loadFinishListeren = loadFinishListeren;
	}

	LoadFinishListeren loadFinishListeren;
	public interface LoadFinishListeren{
		void loadFinish(int contentHeight);
	}

	public interface WebViewBackFace{
		void title(String title);
		void progress(int progress);
		void faceOpenFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);
		void faceOnShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
		void onPageFinished();
		void onPageStart();
	}


	public void setWebViewBackFace(WebViewBackFace backFace){
		this.backFace = backFace;
	}


	public class MyDownLoadListeren implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			showHintDialog(url);
			LogUtil.d("下载链接 = " + url);
			downUrl = url;
		}
	}

	void showHintDialog(final String url){
		 new DoubleButtonDialog(context)
		.setDatas("下载" + url)
		.setClickBack(new ClickCallback() {
			@Override
			public void clickBack(View view) {
				checkIsInstalls();
			}
		}).showDialog();
	}


	public void startDownServer(){
		Intent it = new Intent(context, AppDownloadService.class);
		it.putExtra(KeyContacts.KEY_URL,downUrl);
		it.putExtra(KeyContacts.KEY_TITLE,"下载");
		context.startService(it);

	}

	public void checkIsInstalls() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			boolean canInstall = context.getPackageManager().canRequestPackageInstalls();
			if(canInstall){
				startDownServer();
			}else{

				new DoubleButtonDialog(context, R.style.common_loading_dialog)
				.setDatas("安装应用需要打开未知来源权限，请去设置中开启权限")
				.setClickBack(new ClickCallback() {
					@Override
					public void clickBack(View view) {
						Uri packageURI = Uri.parse("package:" + context.getPackageName());//设置包名，可直接跳转当前软件的设置页面
						Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
						context.startActivityForResult(intent, XWebView.GET_UNKNOWN_APP_SOURCES);
					}
				}).showDialog();
			}
		}else{
			startDownServer();
		}

	}



	//调起应用市场
	public static boolean toMarket(Context context, String url, String marketPkg) {
//		Uri uri = Uri.parse("market://details?id=" + appPkg);

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
			intent.setPackage(marketPkg);
		}
		try {
			context.startActivity(intent);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}





	public void loadHtmlData(String info) {
		this.loadDataWithBaseURL(null,
				getHtmlData(info), "text/html", "utf-8", null);


	}

	/**
	 * 加载html标签
	 *
	 */
	private String getHtmlData(String bodyHTML) {
		String head = "<head>" +
//				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
				"<style>img{max-width: 100%; width:auto; height:auto!important;}</style>" +
				"<style>*,body,html,div,p,img{border:0;margin:0;padding:0;} </style>" +
				"<style>p{font-size :13px !important;line-height:28px !important}</style>"+
				"</head>";
		return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
	}

}
