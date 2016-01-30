/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.anxpp.blog.fragment;

import com.anxpp.blog.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutFragment extends Fragment {
	public static final String TAG = AboutFragment.class.getSimpleName();

	private static final String ABOUT_SCHEME = "settings";
	private static final String ABOUT_AUTHORITY = "about";
	public static final Uri ABOUT_URI = new Uri.Builder().scheme(ABOUT_SCHEME).authority(ABOUT_AUTHORITY).build();

	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.about, container, false);

		//打开另一个fragment
		//		v.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				final Activity a = getActivity();
		//				if (a instanceof ExamplesActivity) {
		//					final ExamplesActivity examplesActivity = (ExamplesActivity) a;
		//					examplesActivity.updateContent(SandboxFragment.SETTINGS_URI);
		//				}
		//			}
		//		});
		return v;
	}
	@Override
	public void onStart() {
		initView();
		super.onStart();
	}
	@Override
	public void onDestroyView() {
		getActivity().setTitle(R.string.app_name);
		super.onDestroyView();
	}
	private void Toast(String msg){
		android.widget.Toast.makeText(getActivity(), msg, android.widget.Toast.LENGTH_SHORT).show();
	}
	@SuppressLint("SetJavaScriptEnabled")
	private void initView(){
		webView = (WebView) getView().findViewById(R.id.webView);
		//		webView.setVisibility(View.GONE);
		webView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress){
				Activity activity = getActivity();
				/**
				 * 此处若不加这个判断，会因为Fragment在网页加载完成前退出而导致程序错误
				 * I am not sure why are you getting this error, 
				 * i think it should be something like NullPointerException. 
				 * Try next: 
				 *     Evert time you calling getActivity() on Fragment instance you should be sure,
				 *     that fragment is actually have this Activity. 
				 *     Because when your webview is loading you are calling this function:
				 * */
				if(activity == null) return;
				activity.setTitle("Loading..." + progress + "%");
				activity.setProgress(progress * 100);
				if(progress == 100){
					activity.setTitle(R.string.app_name);
					webView.setVisibility(View.VISIBLE);
				}        
			}  
		});
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url){                
				Toast("shouldOverrideUrlLoading");
				view.loadUrl(url);     
				return true;         
			}
		});    
		//获取浏览器设置
		WebSettings webSettings = webView.getSettings();
		//有限使用缓存
		//webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//支持js
		webSettings.setJavaScriptEnabled(true);
		//打开页面时， 自适应屏幕
		webSettings.setUseWideViewPort(false); //设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
		webView.loadUrl("http://anxpp.com");
		//设置应用内打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true; //false为调用浏览器打开
			}
		});
		webView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { //表示按返回键 时的操作
						webView.goBack(); //后退
						return true; //已处理
					}
				}
				return false;
			}
		});
	}
}
