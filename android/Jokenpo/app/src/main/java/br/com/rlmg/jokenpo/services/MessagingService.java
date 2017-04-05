package br.com.rlmg.jokenpo.services;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import br.com.rlmg.jokenpo.PlayerActivity;
import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("TAG", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());
            fireBroadcast(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Method that fires the message received broadcast passing the message data to all subscribers
     *
     * @param map - HashMap that represents the message json data
     */
    private void fireBroadcast(Map map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);

        Intent intent = new Intent(Utils.sMESSAGE_RECEIVED);
        intent.putExtra("map", json);
        sendBroadcast(intent);
    }

    /**
     * Method that fires the message received notification
     *
     * @param map - HashMap that represents the message json data
     */
    private void fireNotification(Map map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);

        JsonObject jsonObject = (JsonObject) gson.fromJson(json, JsonObject.class);
        String action = jsonObject.get("action").getAsString();

        if (action == Utils.sCHALLENGE_PLAYERS) {
            Match match = Utils.getMatchFromJson(jsonObject.get("data").getAsString());

            new AsyncTask<String, Void, HashMap>() {
                @Override
                protected HashMap doInBackground(String... params) {
                    String id = params[0];
                    return WebService.getPlayer(id);
                }

                @Override
                protected void onPostExecute(HashMap hashMap) {
                    Player player = Utils.getPlayerFromJson(hashMap);
                    Utils.createSimpleNotification(MessagingService.this, "A new challenge", "The player "  + player.getName() + "challenged you for a match", PlayerActivity.class);
                }
            }.execute(match.getId());
        }
    }
}
