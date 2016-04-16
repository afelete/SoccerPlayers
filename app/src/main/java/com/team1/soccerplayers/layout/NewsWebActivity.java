/*
 * Copyright 2016 Capstone Project Team I CSC483 Software Engineering
  * University of Michigan Flint
 *
 * Licensed under the Education License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.team1.soccerplayers.layout;
/**
 * @author: afelete Kita
 * @email: afeletek@umflint.edu, afelete_k@yahoo.com
 */
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
