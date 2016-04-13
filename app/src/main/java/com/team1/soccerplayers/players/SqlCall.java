package com.team1.soccerplayers.players;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.team1.soccerplayers.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ChrisWandor
 */
public class SqlCall  extends AsyncTask<String, Integer, String>{

    String data = null;
    private ProgressDialog Dialog;

    public SqlCall(Context myContext){
        Dialog = new ProgressDialog(myContext);
    }

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
            Log.d("Baground Task",e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        Dialog.dismiss();
        ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
        listViewLoaderTask.execute(result);
    }



    private String downloadUrl(String strUrl) throws IOException {

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




    private class ListViewLoaderTask extends  AsyncTask<String, Void, SimpleAdapter>{

        JSONObject jObject;

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

            String[] from = {"photo","palyername","details"};
            int[] to = {R.id.playersImageView,R.id.playername,R.id.descText};

            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //return (new SimpleAdapter(getBaseContext(),players,R.layout.list_item,from,to));

            return(new SimpleAdapter(Dialog.getContext(),players,R.layout.list_item,from,to));
        }


        @Override
        protected void onPostExecute(SimpleAdapter adapter){
            Dialog.dismiss();

            /*
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
            */

        }

    }

}