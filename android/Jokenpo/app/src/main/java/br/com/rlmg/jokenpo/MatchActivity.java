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
                    AlertDialog.Builder builder = Utils.buildSimpleDialog("Move Confirmation", "If you submit this move you will not be able to roll back. Are you sure you want to make this move?", MatchActivity.this);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final DialogInterface dialogInterface = dialog;

                            new AsyncTask<String, Void, HashMap>() {
                                @Override
                                protected void onPreExecute() {
                                    dialogInterface.dismiss();
                                    mProgressDialog = Utils.createSimpleProgressDialog("Match", "Waiting for player to make his move", MatchActivity.this);
                                    mProgressDialog.show();
                                }

                                @Override
                                protected HashMap doInBackground(String... params) {
                                    return WebService.move(mMatch.getId(), Utils.sLoggedPlayer.getId(), mCurrentChoice);
                                }
                            }.execute();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
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
                case Utils.sMATCH_END:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match that I am waiting for so it should be good
                    if (mMatch != null && mMatch.getId().equals(match.getId())) {
                        mProgressDialog.dismiss();

                        Intent intent = new Intent(MatchActivity.this, MatchResultActivity.class);
                        intent.putExtra("json", jsonObject.get(WebService.sRESPONSE_DATA).getAsString());

                        startActivity(intent);
                        finish();
                    }

                    break;
            }
        }
    }
}
