package com.team1.soccerplayers.layout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.team1.soccerplayers.R;

public class NewsWebActivity extends AppCompatActivity {
    String newUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web);
        Intent intent = getIntent();
        newUrl = intent.getStringExtra(PlayerInfoActivity.EXTRA_MESSAGE);

        WebView newsWebView = (WebView) findViewById(R.id.newsWebView);
        newsWebView.getSettings().setJavaScriptEnabled(true);

        //Toast urlplz = Toast.makeText(getApplicationContext(), newUrl, Toast.LENGTH_SHORT);
        //urlplz.show();

        newsWebView.setWebViewClient(new WebViewClient());


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            newsWebView.loadUrl(newUrl);
        } else {
            Toast.makeText(NewsWebActivity.this, "Unable to load the page, Please try later.", Toast.LENGTH_SHORT).show();
        }
    }
}
