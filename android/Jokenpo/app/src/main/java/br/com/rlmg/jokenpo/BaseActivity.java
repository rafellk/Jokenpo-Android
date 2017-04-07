package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

/**
 * Created by rlmg on 4/6/17.
 */

public class BaseActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.sPaused = false;
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.sPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.sPaused = false;
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    /**
     * Method that register the message received broadcast
     */
    private void registerBroadcast() {
        if (mReceiver == null) {
            IntentFilter intentFilter = new IntentFilter(Utils.sMESSAGE_RECEIVED);
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    handleBroadcast(intent.getExtras().getString("map"));
                }
            };
            registerReceiver(mReceiver, intentFilter);
        }
    }

    /**
     * Method that unRegisters the broadcast if it is registered
     */
    private void unRegisterBroadcast() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * Method that handles the message received broadcast
     *
     * @param json - String that represents the message content in json
     */
    protected void handleBroadcast(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject != null) {
            String action = jsonObject.get("action").getAsString();
            final String dataJSON = jsonObject.get(WebService.sRESPONSE_DATA).getAsString();

            if (action.equals(Utils.sCHALLENGE_PLAYER)) {
                createChallengeDialog(dataJSON);
            }
        }
    }

    /**
     * Creates a challenge dialog box confirmation using the specified match json
     *
     * @param json - String that represents the match json data
     */
    protected void createChallengeDialog(String json) {
        final String finalJSON = json;
        final Match match = Utils.getMatchFromJson(json);

        AlertDialog.Builder builder = Utils.buildSimpleDialog(getResources().getString(R.string.incomming_match_dialog_title), getResources().getString(R.string.incomming_match_dialog_message), this);
        builder.setPositiveButton(getResources().getString(R.string.incomming_match_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<String, Void, HashMap>() {
                    @Override
                    protected HashMap doInBackground(String... params) {
                        String id = params[0];
                        return WebService.acceptChallenge(id);
                    }
                }.execute(match.getId());

                dialog.dismiss();

                Intent intent = new Intent(BaseActivity.this, MatchActivity.class);
                intent.putExtra("json", finalJSON);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.incomming_match_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<String, Void, HashMap>() {
                    @Override
                    protected HashMap doInBackground(String... params) {
                        return WebService.declineChallenge(match.getId());
                    }
                }.execute();

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
