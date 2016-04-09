package com.team1.soccerplayers.layout;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.team1.soccerplayers.AddFavoritePlayersActivity;
import com.team1.soccerplayers.R;
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
import java.util.List;

public class DisplayFavoritePlayersActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.team1.soccerplayers.MESSAGE";
    ListView favoriteListView;
    String playerName;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_favorite_players);

        //get the share preferences to retrieve used id
        SharedPreferences userSharedPreferences  = getSharedPreferences("UserFile", Context.MODE_PRIVATE);
       userId = userSharedPreferences.getString("UserFile", null);
        if(userId != null)
        Toast.makeText(DisplayFavoritePlayersActivity.this, "User Id: " + userId, Toast.LENGTH_SHORT).show();

        // create ArrayAdapter to bind weatherList to the weatherListView
        String strUrl = "http://dhcp-141-216-26-99.umflint.edu/index.php";//baseUrl + module+".php";

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(strUrl);
        } else {
            Toast.makeText(DisplayFavoritePlayersActivity.this, "Unable to Connect to the server, Please try later.", Toast.LENGTH_SHORT).show();
        }


        favoriteListView = (ListView) findViewById(android.R.id.list);
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String,String> map =(HashMap<String,String>)favoriteListView.getItemAtPosition(position);
                playerName = map.get("player");

                Toast.makeText(DisplayFavoritePlayersActivity.this, "palyer name: " + playerName, Toast.LENGTH_SHORT).show();


                profileView(view);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.manage_account) {
            return true;
        }
        if (id == R.id.manage_players) {
            startActivity(new Intent(this, AddFavoritePlayersActivity.class));
            return true;
        }
        if (id == R.id.create_account) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void profileView(View view){
        Intent intent = new Intent(this,PlayerInfoActivity.class);
        if (!playerName.isEmpty()) {
            intent.putExtra(EXTRA_MESSAGE, playerName);

        }
        startActivity(intent);
    }
    //private method to download the url
    private String downloadUrl(String strUrl) throws IOException {
      /* String request = null;

        try{
            // Set Request parameter
            if (userId != null) {
                request += "&" + URLEncoder.encode("data", "UTF-8") + "=" + userId;
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        String data = null;
        try{
            URL url = new URL(strUrl);
            URLConnection urlConnection =  url.openConnection();

            // Send POST userId request
            //--------------------------------just added
          /* urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write( request );
            wr.flush();*/
            //-----------------------------------------end
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine())!= null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = "";
        private ProgressDialog Dialog = new ProgressDialog(DisplayFavoritePlayersActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch (Exception e){
                Log.d("Baground Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            // Close progress dialog
            Dialog.dismiss();
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(result);
        }
    }

    private class ListViewLoaderTask extends  AsyncTask<String, Void, SimpleAdapter>{
        JSONObject jObject;
        private ProgressDialog Dialog = new ProgressDialog(DisplayFavoritePlayersActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }


        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try{
                jObject = new JSONObject(strJson[0]);
                PlayersJSONParser playersJSONParser = new PlayersJSONParser();
                playersJSONParser.parse(jObject);
            }catch (Exception e){
                e.printStackTrace();
            }

            PlayersJSONParser playersJSONParser = new PlayersJSONParser();
            List<HashMap<String, Object>> players = null;
            try{
                players = playersJSONParser.parse(jObject);

            }catch (Exception e){
                e.printStackTrace();
            }

            String[] from = {"photo","details"};
            int[] to = {R.id.favoriteImageView,R.id.favoriteText};

            return (new SimpleAdapter(getBaseContext(),players,R.layout.list_item_favorite,from,to));
        }


        @Override
        protected void onPostExecute(SimpleAdapter adapter){
            Dialog.dismiss();
            favoriteListView.setAdapter(adapter);
            for (int i=0; i<adapter.getCount();i++){
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("photo_path");
                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                // HashMap<String, Object> hmDownload = new HashMap<>();
                hm.put("photo_path",imgUrl );
                hm.put("position",i);
                imageLoaderTask.execute(hm);
            }
        }

    }


    private class ImageLoaderTask extends  AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>>{


        @Override
        protected final HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {

            String imgUrl = (String) hm[0].get("photo_path");
            int position = (Integer) hm[0].get("position");
            URL url;
            try{
                url = new URL(imgUrl);
                URLConnection urlConnection =  url.openConnection();

                File cacheDirectory = getBaseContext().getCacheDir();
                File tmpFile = new File(cacheDirectory.getPath()+ position+".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                Bitmap b = BitmapFactory.decodeStream(urlConnection.getInputStream());
                b.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                HashMap<String, Object> hmBitmap = new HashMap<>();
                hmBitmap.put("photo",tmpFile.getPath());
                hmBitmap.put("position",position);
                return hmBitmap;
            }catch (Exception e ){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            String path = (String) result.get("photo");
            int position = (Integer) result.get("position");
            SimpleAdapter adapter = (SimpleAdapter) favoriteListView.getAdapter();
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
            hm.put("photo", path);
            adapter.notifyDataSetChanged();
        }
    }


}
