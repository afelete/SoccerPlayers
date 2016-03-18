package com.team1.soccerplayers.layout;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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


public class DisplayPlayersActivity extends ListActivity {
    // List of Player objects representing the Players
    ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_players);

        //receive the intent and initiate the view
       //setListAdapter(new MyAdapter());
        // create ArrayAdapter to bind weatherList to the weatherListView
        String strUrl = "http://dhcp-141-216-26-99.umflint.edu/index.php";//baseUrl + module+".php";
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(strUrl);

        mListView = (ListView) findViewById(android.R.id.list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileView(view);

            }
        });

    }
    public void profileView(View view){
        Intent intent = new Intent(this,DisplayFavoritePlayersActivity.class);
        startActivity(intent);
    }
    //private method to download the url
    private String downloadUrl(String strUrl) throws IOException{

        String data = null;
        try{
            URL url = new URL(strUrl);
            URLConnection urlConnection =  url.openConnection();
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

    private class DownloadTask extends AsyncTask<String, Integer, String>{

        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch (Exception e){
                Log.d("Baground Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(result);
        }
    }

    private class ListViewLoaderTask extends  AsyncTask<String, Void, SimpleAdapter>{

        JSONObject jObject;

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
            int[] to = {R.id.playersImageView,R.id.descText};

            return (new SimpleAdapter(getBaseContext(),players,R.layout.list_item,from,to));
        }


        @Override
        protected void onPostExecute(SimpleAdapter adapter){
            mListView.setAdapter(adapter);
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

        @SafeVarargs
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
            SimpleAdapter adapter = (SimpleAdapter) mListView.getAdapter();
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
            hm.put("photo", path);
            adapter.notifyDataSetChanged();
        }
    }


}
