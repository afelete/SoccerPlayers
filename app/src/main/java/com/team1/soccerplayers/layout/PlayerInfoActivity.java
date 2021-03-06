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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.team1.soccerplayers.R;
import com.team1.soccerplayers.players.DocumentJSONParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class PlayerInfoActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.team1.soccerplayers.MESSAGE";
    static final String STATE_PLAYER = "playerNames";
    public String APILink;
    ListView infoListView;
    String playerName;
    String firstName[];
    String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            playerName = savedInstanceState.getString(STATE_PLAYER);

        } else {

            // Probably initialize members with default values for a new instance
            setContentView(R.layout.activity_player_info);
            Intent intent = getIntent();
            playerName = intent.getStringExtra(DisplayFavoritePlayersActivity.EXTRA_MESSAGE);

        }
            if (playerName.contains(" ")) {
                firstName = playerName.split(" ");

            }

            if (playerName.contains(" ")) {
                APILink = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27" + firstName[0].trim() + "%20" + firstName[1].trim() + "%20%27&$format=json";
            } else {
                APILink = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27" + playerName + "%20%27&$format=json";
            }
            //Toast.makeText(PlayerInfoActivity.this, "resrult: " + playerName, Toast.LENGTH_SHORT).show();


            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute();
            } else {
                Toast.makeText(PlayerInfoActivity.this, "Unable to Connect to the server, Please try later.", Toast.LENGTH_SHORT).show();
            }

            infoListView = (ListView) findViewById(android.R.id.list);

            final TextView title1 = new TextView(this);
            final TextView summary1 = new TextView(this);
            infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    HashMap<String, String> map = (HashMap<String, String>) infoListView.getItemAtPosition(position);
                    page = map.get("Url");
                    int fadedTitleColor = getResources().getColor(R.color.marked_as_read_title_text);
                    int fadedSummaryColor = getResources().getColor(R.color.marked_as_read_summary_text);

                    profileView(view);
                }
            });

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        savedInstanceState.putString(STATE_PLAYER,playerName);
        super.onSaveInstanceState(savedInstanceState);
        //  Parcelable state =  c
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        playerName = savedInstanceState.getString(STATE_PLAYER);

        // Restore state members from saved instance

    }


    public void profileView(View view) {
        //Bundle newsBundle = new Bundle();


        Intent intent = new Intent(this, NewsWebActivity.class);
        if (!page.isEmpty()) {
            intent.putExtra(EXTRA_MESSAGE, page);

        }

        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        //private String APILink = "https://api.datamarket.azure.com/Bing/Search/v1/";

        String data = "";
        String nextUrl = APILink;
        private String API_KEY = "CtZokufCIN2Fst6Ghgce8mOS6pdQL72R9P70zwviFsc";
        private ProgressDialog Dialog = new ProgressDialog(PlayerInfoActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //For some reason post method doesn't work.
            //Only Get request work for this API.
            //Prepare Post request.

            //JSONArray to receive the result in JSON format

            HttpClient httpClient = new DefaultHttpClient();

            //Build Link
            HttpGet httpget = new HttpGet(APILink);
            String auth = API_KEY + ":" + API_KEY;
            String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
            Log.e("", encodedAuth);
            httpget.addHeader("Authorization", "Basic " + encodedAuth);

            //Execute and get the response.
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpget);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        data += line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(result);

        }
    }

    private class ListViewLoaderTask extends  AsyncTask<String, Void, SimpleAdapter>{


        JSONObject bingResult = null;
        private ProgressDialog Dialog = new ProgressDialog(PlayerInfoActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try{
                JSONObject jObject = new JSONObject(strJson[0]);
                bingResult = jObject.getJSONObject("d");
                DocumentJSONParser documentJSONParser = new DocumentJSONParser();
                documentJSONParser.parse(bingResult);
            }catch (Exception e){
                e.printStackTrace();
            }

            DocumentJSONParser documentJSONParser = new DocumentJSONParser();
            List<HashMap<String, Object>> documents = null;
            try{
                documents = documentJSONParser.parse(bingResult);

            }catch (Exception e){
                e.printStackTrace();
            }

            String[] from = {"Title", "Description"};
            int[] to = {R.id.infoText, R.id.sammaryText};

            return (new SimpleAdapter(getBaseContext(), documents, R.layout.list_item_info, from, to));
        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            Dialog.dismiss();
            infoListView.setAdapter(adapter);

        }
    }


}
