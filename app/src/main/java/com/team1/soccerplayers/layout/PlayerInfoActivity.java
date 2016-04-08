package com.team1.soccerplayers.layout;

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
    ListView infoListView;
    String playerName;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        Intent intent = getIntent();
        playerName = intent.getStringExtra(DisplayFavoritePlayersActivity.EXTRA_MESSAGE);
        Toast.makeText(PlayerInfoActivity.this, "resrult: " + playerName, Toast.LENGTH_SHORT).show();


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
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) infoListView.getItemAtPosition(position);
                url = map.get("Url");
                //int fadedTitleColor = getResources().getColor(R.color.marked_as_read_title_text);
                // int fadedSummaryColor = getResources().getColor(R.color.marked_as_read_summary_text);
                Toast.makeText(PlayerInfoActivity.this, "Url: " + url, Toast.LENGTH_SHORT).show();
                profileView(view);
            }
        });

    }

    public void profileView(View view) {
        Bundle newsBundle = new Bundle();

        Intent intent = new Intent(this, PlayerInfoActivity.class);
        if (!url.isEmpty()) {
            intent.putExtra(EXTRA_MESSAGE, playerName);

        }
        startActivity(intent);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        //private String APILink = "https://api.datamarket.azure.com/Bing/Search/v1/";

        public String APILink = "https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27" + playerName + "%20%27&$format=json";
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
