package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.joooonho.SelectableRoundedImageView;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class MatchActivity extends BaseActivity {

    private SelectableRoundedImageView mCurrentChoiceImageView;
    private String mCurrentChoice = null;
    private ProgressDialog mProgressDialog = null;
    private Match mMatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        if (getIntent().getExtras() != null) {
            mMatch = Utils.getMatchFromJson(getIntent().getStringExtra("json"));
            mCurrentChoiceImageView = (SelectableRoundedImageView) findViewById(R.id.match_player_choice);

            SelectableRoundedImageView rock = (SelectableRoundedImageView) findViewById(R.id.match_player_choice_rock);
            SelectableRoundedImageView paper = (SelectableRoundedImageView) findViewById(R.id.match_player_choice_paper);
            SelectableRoundedImageView scissors = (SelectableRoundedImageView) findViewById(R.id.match_player_choice_scissors);
            Button submit = (Button) findViewById(R.id.match_submit_button);
            Button exitButton = (Button) findViewById(R.id.match_exit_button);

            mCurrentChoice = "ROCK";
            mCurrentChoiceImageView.setImageResource(Utils.getImageIdForChoice(mCurrentChoice));

            rock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentChoice = "ROCK";
                    mCurrentChoiceImageView.setImageResource(Utils.getImageIdForChoice(mCurrentChoice));
                }
            });

            paper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentChoice = "PAPER";
                    mCurrentChoiceImageView.setImageResource(Utils.getImageIdForChoice(mCurrentChoice));
                }
            });

            scissors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentChoice = "SCISSORS";
                    mCurrentChoiceImageView.setImageResource(Utils.getImageIdForChoice(mCurrentChoice));
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitMove();
                }
            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exit();
                }
            });
        }
    }

    private void submitMove() {
        AlertDialog.Builder builder = Utils.buildSimpleDialog(getResources().getString(R.string.match_move_confirmation),
                getResources().getString(R.string.match_move_message_confirmation), MatchActivity.this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DialogInterface dialogInterface = dialog;

                new AsyncTask<String, Void, HashMap>() {
                    @Override
                    protected void onPreExecute() {
                        dialogInterface.dismiss();
                        mProgressDialog = Utils.createSimpleProgressDialog(getResources().getString(R.string.match_waiting_title),
                                getResources().getString(R.string.match_waiting_message), MatchActivity.this);
                        mProgressDialog.show();
                    }

                    @Override
                    protected HashMap doInBackground(String... params) {
                        return WebService.move(mMatch.getId(), Utils.sLoggedPlayer.getId(), mCurrentChoice);
                    }
                }.execute();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.match_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exit() {
        AlertDialog.Builder builder =
                Utils.buildSimpleDialog(getResources().getString(R.string.match_exit_confirmation),
                getResources().getString(R.string.match_exit_message), MatchActivity.this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DialogInterface dialogInterface = dialog;

                new AsyncTask<String, Void, HashMap>() {
                    @Override
                    protected void onPreExecute() {
                        dialogInterface.dismiss();
                        mProgressDialog = Utils.createSimpleProgressDialog(
                                getResources().getString(R.string.match_exit_confirmation),
                                getResources().getString(R.string.match_load_give_up), MatchActivity.this);
                        mProgressDialog.show();
                    }

                    @Override
                    protected HashMap doInBackground(String... params) {
                        return WebService.ragequit(mMatch.getId(), Utils.sLoggedPlayer.getId());
                    }

                    @Override
                    protected void onPostExecute(HashMap hashMap) {
                        mProgressDialog.dismiss();
                        Intent intent = new Intent(MatchActivity.this, PlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }.execute();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.match_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        final JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject != null) {
            String action = jsonObject.get("action").getAsString();
            Match match = null;

            switch (action) {
                case Utils.sMATCH_END:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match that I am waiting for so it should be good
                    if (mMatch != null && mMatch.getId().equals(match.getId())) {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }

                        Intent intent = new Intent(MatchActivity.this, MatchResultActivity.class);
                        intent.putExtra("json", jsonObject.get(WebService.sRESPONSE_DATA).getAsString());

                        startActivity(intent);
                        finish();
                    }

                    break;
                case Utils.sMATCH_CANCELED:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    if (mMatch != null && mMatch.getId().equals(match.getId())) {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }

                        Intent intent = new Intent(MatchActivity.this, MatchResultActivity.class);
                        intent.putExtra("json", jsonObject.get(WebService.sRESPONSE_DATA).getAsString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(getResources().getString(R.string.match_alert_gave_up))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(MatchActivity.this, PlayerActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    break;
            }
        }
    }
}
