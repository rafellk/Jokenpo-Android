package br.com.rlmg.jokenpo.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import br.com.rlmg.jokenpo.R;
import br.com.rlmg.jokenpo.models.Player;

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
    public static final String sCHALLENGE_PLAYERS = "CHALLENGE_PLAYERS";
    public static final String sDECLINE_MATCH_REQUEST = "DECLINE_MATCH_REQUEST";
    public static final String sACCEPT_MATCH_REQUEST = "ACCEPT_MATCH_REQUEST";
    public static final String sPLAYER_MOVE = "PLAYER_MOVE";
    public static final String sMATCH_END = "MATCH_END";


    public static void createSimpleNotification(Context context, String title, String content, Class activityClass) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);
        Intent resultIntent = new Intent(context, activityClass);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(1, builder.build());
    }
}
