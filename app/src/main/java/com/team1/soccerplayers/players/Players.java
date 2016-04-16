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
public class Players {
    public final String playerId;
    public final String playerName;
        public final String playerDescription;
        public final String photoURL;

        public Players(String playerId, String playerName, String playerDescription, String photoURL) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.playerDescription = playerDescription;
            this.photoURL = photoURL;
        }
}
