package br.com.rlmg.jokenpo.services;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.util.Map;

import br.com.rlmg.jokenpo.utils.Utils;

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
}
