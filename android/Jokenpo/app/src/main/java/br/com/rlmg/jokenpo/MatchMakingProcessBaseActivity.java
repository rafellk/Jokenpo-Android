package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class MatchMakingProcessBaseActivity extends BaseActivity {

    protected ProgressDialog mProgressDialog = null;
    protected Match mMatch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // needs this because this holds reference
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void handleBroadcast(String json) {
        super.handleBroadcast(json);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject != null) {
            String action = jsonObject.get("action").getAsString();
            Match match = null;

            switch (action) {
                case Utils.sACCEPT_MATCH_REQUEST:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match that I am waiting for so it should be good
                    if (mMatch != null && mMatch.getId().equals(match.getId())) {
                        mProgressDialog.dismiss();

                        Intent intent = new Intent(MatchMakingProcessBaseActivity.this, MatchActivity.class);
                        intent.putExtra("json", jsonObject.get(WebService.sRESPONSE_DATA).getAsString());
                        startActivity(intent);
                    }

                    break;
                case Utils.sDECLINE_MATCH_REQUEST:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match I am waiting for so it should be good. Dismiss the progress dialog and show an alert dialog informing the match was declined
                    if ((mMatch != null && mMatch.getId().equals(match.getId())) && (match.getPlayer1().equals(Utils.sLoggedPlayer.getId()) || match.getPlayer2().equals(Utils.sLoggedPlayer.getId()))) {
                        mProgressDialog.dismiss();

                        AlertDialog.Builder builder = Utils.buildSimpleDialog(getResources().getString(R.string.declined_match_dialog_title), getResources().getString(R.string.declined_match_dialog_message), MatchMakingProcessBaseActivity.this);

                        builder.setPositiveButton(getResources().getString(R.string.declined_match_dialog_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    break;
            }
        }
    }

    /**
     * Method that creates a match using the logged player and the specified player
     *
     * @param playerId - String that represents the id of the player that will be challenged
     */
    protected void challengePlayer(String playerId) {
        new AsyncTask<String, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Utils.createSimpleProgressDialog(getResources().getString(R.string.room_progress_dialog_title), getResources().getString(R.string.room_progress_dialog_message), MatchMakingProcessBaseActivity.this);
            }

            @Override
            protected HashMap doInBackground(String... params) {
                String id = params[0];
                return WebService.challengePlayer(Utils.sLoggedPlayer.getId(), id);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                mMatch = Utils.getMatchFromJson(hashMap);
            }
        }.execute(playerId);
    }
}
