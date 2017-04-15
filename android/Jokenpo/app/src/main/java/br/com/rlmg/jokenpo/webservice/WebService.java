package br.com.rlmg.jokenpo.webservice;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.InternalErrorCodes;
import br.com.rlmg.jokenpo.utils.Utils;

/**
 * Created by rlmg on 4/2/17.
 */

public class WebService {

    private static final String sSERVER_URL = "http://192.168.1.4:3003";

    /**
     * Constants that can be used as keys on the request results hashmap
     */
    public static final String sHTTP_ERROR = "http_error";
    public static final String sINTERNAL_ERROR = "internal_error";
    public static final String sRESPONSE_DATA = "data";
    public static final String sRESPONSE_RAW_DATA = "raw_data";

    /**
     * A generic method that performs a get request with the specified endpoint, creates a Gson
     * object targetting the specified class and passing a hashmap that contains the Gson object
     * to the listener.
     *
     * @param endpoint - String that represents the server endpoint to be targeted
     *
     * @param method - String that represents the http method that the request will use
     *
     * @param json - String that represents the json that will be appended in the request. This parameter if the request method is GET
     *
     * @param targetClass - Class that represents the class that will be targeted to create the data Gson object
     */
    private static HashMap performRequest(String endpoint, String method, String json, Class targetClass) {
        HashMap map = new HashMap();

        try {
            URL url = new URL(sSERVER_URL + endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10 * 1000);
            connection.setConnectTimeout(15 * 1000);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoInput(true);
            connection.setDoOutput(method != "GET");

            connection.connect();

            // Verifies if the user will append a json string to the request body
            if (method != "GET" && json != null) {

                // This teorically sets the string as json into the requests to body to be posted
//                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                OutputStream out = connection.getOutputStream();
                out.write(json.toString().getBytes("UTF-8"));
            }


            // Process the received data if the request succeeded
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED || connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {

                // Converts the input stream into a formatted model class
                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder result = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                map.put(sRESPONSE_DATA, (new Gson()).fromJson(result.toString(), targetClass));
                map.put(sRESPONSE_RAW_DATA, result.toString());

                reader.close();
            } else {

                // Created an error HashMap that contains the http response code that failed
                map.put(sHTTP_ERROR, connection.getResponseCode());
            }
        } catch (IOException exception) {
            // TODO: Analyze the possible cases of problem to map into an error enumeration
            map.put(sINTERNAL_ERROR, exception.getMessage());
        }

        return map;
    }

    private static HashMap performRequest(String endpoint, String method, Class targetClass) {
        return performRequest(endpoint, method, null, targetClass);
    }

    public static HashMap signin(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        String token = FirebaseInstanceId.getInstance().getToken();
        map.put("token", FirebaseInstanceId.getInstance().getToken());

        Gson gson = new Gson();
        String json = gson.toJson(map);

        return performRequest("/player/signin", "POST", json, GsonPlayer.class);
    }

    public static HashMap getPlayer(String id) {
        return performRequest("/player/id/" + id, "GET", GsonPlayer.class);
    }

    public static HashMap getPlayersOnline(String id) {
        return performRequest("/player/room/" + id, "GET", GsonPlayer[].class);
    }

    public static HashMap logout(String id) {
        return performRequest("/player/logout/" + id, "PUT", GsonPlayer.class);
    }

    public static HashMap getMatch(String id) {
        return performRequest("/match/" + id, "GET", GsonMatch.class);
    }

    public static HashMap getMatchPlayerIsPlaying(String playerId) {
        return performRequest("/match/player/playing/" + playerId, "GET", GsonMatch.class);
    }

    public static HashMap getMatchesPlayerHistory(String playerId) {
        return performRequest("/match/player/history/" + playerId, "GET", GsonMatch[].class);
    }

    public static HashMap challengePlayer(String player1Id, String player2Id) {
        Map<String, String> map = new HashMap<>();
        map.put("player1", player1Id);
        map.put("player2", player2Id);

        Gson gson = new Gson();
        String json = gson.toJson(map);

        return performRequest("/match/challenge/", "POST", json, GsonMatch.class);
    }

    public static HashMap acceptChallenge(String matchId) {
        return performRequest("/match/accept/" + matchId, "PUT", GsonMatch.class);
    }

    public static HashMap declineChallenge(String matchId) {
        return performRequest("/match/decline/" + matchId, "DELETE", GsonMatch.class);
    }

    public static HashMap move(String matchId, String playerId, String move) {
        return performRequest("/match/move/" + matchId + "/" + playerId + "/" + move, "PUT", GsonMatch.class);
    }

    public static HashMap ragequit(String matchId, String playerId) {
        return performRequest("/match/ragequit/" + matchId + "/" + playerId, "PUT", GsonMatch.class);
    }
}
