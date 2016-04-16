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
package com.team1.soccerplayers.players;
/**
 * @author: afelete Kita
 * @email: afeletek@umflint.edu, afelete_k@yahoo.com
 */
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
