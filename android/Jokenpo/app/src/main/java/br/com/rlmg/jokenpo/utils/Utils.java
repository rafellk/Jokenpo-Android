package br.com.rlmg.jokenpo.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.HashMap;

import br.com.rlmg.jokenpo.LoginActivity;
import br.com.rlmg.jokenpo.R;
import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.webservice.WebService;

/**
 * Created by rlmg on 4/2/17.
 */

public class Utils {
    public static Player sLoggedPlayer = null;
    public static final int NOTIFICATION_ID = 1000;

    /**
     * Message received notification constants
     */
    public static final String sMESSAGE_RECEIVED = "br.com.rlmg.jokenpo.MESSAGE_RECEIVED";
    public static final String sCHALLENGE_PLAYER = "CHALLENGE_PLAYER";
    public static final String sDECLINE_MATCH_REQUEST = "DECLINE_MATCH_REQUEST";
    public static final String sACCEPT_MATCH_REQUEST = "ACCEPT_MATCH_REQUEST";
    public static final String sPLAYER_MOVE = "PLAYER_MOVE";
    public static final String sMATCH_END = "MATCH_END";
    public static final int sNOTIFICATION_ID = 1000;

    public static AlertDialog.Builder buildSimpleDialog(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title);

        return builder;
    }

    public static void createSimpleNotification(Context context, String title, String content, Class activityClass, String json) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);
        Intent resultIntent = new Intent(context, activityClass);
        if (json != null) {
            resultIntent.putExtra("json", json);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(sNOTIFICATION_ID, builder.build());
    }

    public static ProgressDialog createSimpleProgressDialog(String title, String message, Context context) {
        return ProgressDialog.show(context, title, message, true);
    }


    public static Player getPlayerFromJson(HashMap json) {
        Gson gson = new Gson();
        GsonPlayer gsonPlayer = (GsonPlayer) json.get(WebService.sRESPONSE_DATA);
        return gsonPlayer.convert();
    }

    public static Match getMatchFromJson(HashMap json) {
        Gson gson = new Gson();
        GsonMatch gsonMatch = (GsonMatch) json.get(WebService.sRESPONSE_DATA);
        return gsonMatch.convert();
    }

    public static Player getPlayerFromJson(String json) {
        Gson gson = new Gson();
        GsonPlayer gsonPlayer = (GsonPlayer) gson.fromJson(json, GsonPlayer.class);
        return gsonPlayer.convert();
    }

    public static Match getMatchFromJson(String json) {
        Gson gson = new Gson();
        GsonMatch gsonMatch = (GsonMatch) gson.fromJson(json, GsonMatch.class);
        return gsonMatch.convert();
    }
}
