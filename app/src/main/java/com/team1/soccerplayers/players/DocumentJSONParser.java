package com.team1.soccerplayers.players;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DocumentJSONParser {

    // Receives a JSONObject and returns a list
    public List<HashMap<String, Object>> parse(JSONObject jObject) {

        JSONArray jDocuments = null;
        try {
            // Retrieves all the elements in the 'players' array
            jDocuments = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getPlayers with the array of json object
        // where each json object represent a player
        return getDocuments(jDocuments);
    }


    private List<HashMap<String, Object>> getDocuments(JSONArray jDocuments) {
        int newsCount = jDocuments.length();
        List<HashMap<String, Object>> documentList = new ArrayList<>();
        HashMap<String, Object> documents;

        // Taking each player, parses and adds to list object
        for (int i = 0; i < newsCount; i++) {
            try {
                // Call getPlayer with player JSON object to parse the player
                documents = getDocument((JSONObject) jDocuments.get(i));
                documentList.add(documents);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return documentList;
    }

    // Parsing the Player JSON object
    private HashMap<String, Object> getDocument(JSONObject jDocument) {

        HashMap<String, Object> document = new HashMap<>();
        String title;
        String documentUrl;
        String summary;

        try {
            title = jDocument.getString("Title");
            documentUrl = jDocument.getString("Url");
            summary = jDocument.getString("Description");


            document.put("Title", title);
            document.put("Url", documentUrl);
            document.put("Description", summary);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return document;
    }
}
