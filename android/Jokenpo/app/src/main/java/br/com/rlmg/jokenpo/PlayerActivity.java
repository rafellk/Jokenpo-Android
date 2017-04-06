package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class PlayerActivity extends AppCompatActivity {

    private TextView mTextView_User;
    private Button mButtonPlay;
    private Button mButtonViewHistory;
    private Button mButtonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // if this intent contains extras then the notification was tapped and we should present a dialog to accept or decline
        if (getIntent().getExtras() != null) {
            final String json = (String) getIntent().getExtras().get("json");
            final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

                    manager.cancel(Utils.sNOTIFICATION_ID);
                    dialog.dismiss();

                    Intent intent = new Intent(PlayerActivity.this, MatchActivity.class);
                    intent.putExtra("json", json);
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

                    manager.cancel(Utils.sNOTIFICATION_ID);
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        TextView textView = (TextView) findViewById(R.id.textView_User);
        textView.setText(Utils.sLoggedPlayer.getName());

        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mButtonViewHistory = (Button) findViewById(R.id.btn_viewHistory);
        mButtonLogout = (Button) findViewById(R.id.btn_logout);

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {play(v);}
        });

        mButtonViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {viewHistory(v);}
        });

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Logout(v);}
        });
    }

    public void play(View v) {
        Intent intent = new Intent(PlayerActivity.this, RoomActivity.class);
        startActivity(intent);
    }

    public void viewHistory(View v) {

    }

    /**
     * Method that makes an asynchronous logout request to the webserver
     *
     * @param v
     */
    public void Logout(View v) {
        new AsyncTask<String, Void, HashMap>() {
            @Override
            protected HashMap doInBackground(String... params) {
                String id = params[0];
                return WebService.logout(id);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                // TODO: show pop up if there was any kind of error during the request
                Utils.sLoggedPlayer = null;

                Intent intent = new Intent(PlayerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }.execute(Utils.sLoggedPlayer.getId());
    }
}
