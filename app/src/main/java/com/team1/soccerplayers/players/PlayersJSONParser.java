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

import com.team1.soccerplayers.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: afelete Kita
 * @email: afeletek@umflint.edu, afelete_k@yahoo.com
 */

/** A class to parse json data */
public class PlayersJSONParser {

    // Receives a JSONObject and returns a list
    public List<HashMap<String,Object>> parse(JSONObject jObject){

        JSONArray jPlayers = null;
        try {
            // Retrieves all the elements in the 'players' array 
            jPlayers = jObject.getJSONArray("players");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getPlayers with the array of json object
        // where each json object represent a player
        return getPlayers(jPlayers);
    }


    private List<HashMap<String, Object>> getPlayers(JSONArray jPlayers){
        int playerCount = jPlayers.length();
        List<HashMap<String, Object>> playerList = new ArrayList<>();
        HashMap<String, Object> player;

        // Taking each player, parses and adds to list object 
        for(int i=0; i<playerCount;i++){
            try {
                // Call getPlayer with player JSON object to parse the player 
                player = getPlayer((JSONObject) jPlayers.get(i));
                playerList.add(player);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return playerList;
    }

    // Parsing the Player JSON object 
    private HashMap<String, Object> getPlayer(JSONObject jPlayer){

        HashMap<String, Object> player = new HashMap<>();
        String playerId;
        String playerName;
        String photo;
        String details;

        try {
            playerId = jPlayer.getString("playerid");
            playerName = jPlayer.getString("playername");
            details = jPlayer.getString("description");
            photo = jPlayer.getString("photo");

            details = playerName+ " " +details;
            player.put("playerid", playerId);
            player.put("player", playerName);
            player.put("details", details);
            player.put("photo", R.mipmap.ic_launcher);
            player.put("photo_path", photo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return player;
    }
}