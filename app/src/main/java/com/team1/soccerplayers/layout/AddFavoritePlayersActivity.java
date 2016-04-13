package com.team1.soccerplayers.layout;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.team1.soccerplayers.R;
import com.team1.soccerplayers.customlist.CheckableLinearLayout;
import com.team1.soccerplayers.players.PlayersJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddFavoritePlayersActivity extends ListActivity {
    // List of Player objects representing the Players

    ListView mListViewAdd;
    Set<String> set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_players);
        set = new HashSet<>();
        //receive the intent and initiate the view
        //setListAdapter(new MyAdapter());
        // create ArrayAdapter to bind weatherList to the weatherListView
        // if (isOnline()) {
        String strUrl = "http://dhcp-141-216-26-99.umflint.edu/getAllPlayersWithUserSelection.php";//baseUrl + module+".php";


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(strUrl);
        } else {
            Toast.makeText(AddFavoritePlayersActivity.this, "Unable to Connect to the server, Please try later.", Toast.LENGTH_SHORT).show();
        }
        mListViewAdd = (ListView) findViewById(android.R.id.list);
        mListViewAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean checked = ((CheckableLinearLayout) view).isChecked();

                HashMap<String, String> map = (HashMap<String, String>) mListViewAdd.getItemAtPosition(position);
                String value = map.get("player");
                if (checked) {
                    set.add(value);
                    Toast.makeText(AddFavoritePlayersActivity.this, "palyer name: " + value, Toast.LENGTH_SHORT).show();
                } else {
                    set.remove(value);
                    Toast.makeText(AddFavoritePlayersActivity.this, "palyer name: " + value, Toast.LENGTH_SHORT).show();
                }

            }
        });


        // }
        // else{
        // Toast.makeText(this,"Netwok not Available", Toast.LENGTH_SHORT );
        // }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                // CheckableLinearLayout checkableLinearLayout = new CheckableLinearLayout(this,Context.);
                if (!set.isEmpty()) {
                    // We need an Editor object to make preference changes.
                    // All objects are from android.context.Context
                    SharedPreferences sharedPreferences = getSharedPreferences("PlayersFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    for (String myset : set) {
                        editor.putString("playername", myset);
                        Toast.makeText(AddFavoritePlayersActivity.this, "palyer name: " + myset, Toast.LENGTH_SHORT).show();
                    }

                    // Commit the edits!
                    editor.commit();
                    SharedPreferences userSharedPreferences = getSharedPreferences("UserFile", Context.MODE_PRIVATE);

                    SharedPreferences.Editor userEditor = userSharedPreferences.edit();
                    userEditor.putString("userid", "0001");
                    userEditor.commit();

                    //also sent data to the server
                    profileView(view);
                }

                Toast.makeText(AddFavoritePlayersActivity.this, "Please Select your Favorite players to Follow.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //method to check network availability and connectivity

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckableLinearLayout) view).isChecked();
        if (checked) {
            set.add(String.valueOf(R.id.playernameAdd));
        } else {
            set.remove(String.valueOf(R.id.playernameAdd));
        }

    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void profileView(View view) {
        Intent intent = new Intent(this, DisplayFavoritePlayersActivity.class);
        startActivity(intent);
    }

    //private method to download the url
    private String downloadUrl(String strUrl) throws IOException {

        String data = null;
        try {
            URL url = new URL(strUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        String data = null;
        private ProgressDialog Dialog = new ProgressDialog(AddFavoritePlayersActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Baground Task", e.toString());
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

    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {

        JSONObject jObject;
        private ProgressDialog Dialog = new ProgressDialog(AddFavoritePlayersActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try {
                jObject = new JSONObject(strJson[0]);
                PlayersJSONParser playersJSONParser = new PlayersJSONParser();
                playersJSONParser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PlayersJSONParser playersJSONParser = new PlayersJSONParser();
            List<HashMap<String, Object>> players = null;
            try {
                players = playersJSONParser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] from = {"photo", "palyername", "details"};
            int[] to = {R.id.playersImageViewAdd, R.id.playernameAdd, R.id.descTextAdd};

            return (new SimpleAdapter(getBaseContext(), players, R.layout.list_item_add, from, to));
        }


        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            Dialog.dismiss();
            mListViewAdd.setAdapter(adapter);
            for (int i = 0; i < adapter.getCount(); i++) {
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("photo_path");
                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                // HashMap<String, Object> hmDownload = new HashMap<>();
                hm.put("photo_path", imgUrl);
                hm.put("position", i);
                imageLoaderTask.execute(hm);
            }
        }

    }


    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

        @SafeVarargs
        @Override
        protected final HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {

            String imgUrl = (String) hm[0].get("photo_path");
            int position = (Integer) hm[0].get("position");
            URL url;
            try {
                url = new URL(imgUrl);
                URLConnection urlConnection = url.openConnection();

                File cacheDirectory = getBaseContext().getCacheDir();
                File tmpFile = new File(cacheDirectory.getPath() + position + ".png");
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                Bitmap b = BitmapFactory.decodeStream(urlConnection.getInputStream());
                b.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                HashMap<String, Object> hmBitmap = new HashMap<>();
                hmBitmap.put("photo", tmpFile.getPath());
                hmBitmap.put("position", position);
                return hmBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            String path = (String) result.get("photo");
            int position = (Integer) result.get("position");
            SimpleAdapter adapter = (SimpleAdapter) mListViewAdd.getAdapter();
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
            hm.put("photo", path);
            adapter.notifyDataSetChanged();
        }
    }


}