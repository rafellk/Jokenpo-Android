package br.com.rlmg.jokenpo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.utils.Utils;

public class MatchResultActivity extends MatchMakingProcessBaseActivity {

    private Match mPlayingMatch = null;
    private SelectableRoundedImageView mLoggedPlayer;
    private SelectableRoundedImageView mPlayer2;
    private TextView mLoggedPlayerTextView;
    private TextView mPlayer2TextView;
    private TextView mResultTextView;
    private Button mPlayAgainButton;
    private Button mExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        if (getIntent().getExtras() != null) {
            mPlayingMatch = Utils.getMatchFromJson(getIntent().getStringExtra("json"));

            mLoggedPlayer = (SelectableRoundedImageView) findViewById(R.id.match_result_logged_player);
            mPlayer2 = (SelectableRoundedImageView) findViewById(R.id.match_result_player2);
            mLoggedPlayerTextView = (TextView) findViewById(R.id.match_result_logged_player_text);
            mPlayer2TextView = (TextView) findViewById(R.id.match_result_player2_text);
            mResultTextView = (TextView) findViewById(R.id.match_result_text);

            mPlayAgainButton = (Button) findViewById(R.id.match_result_play_again_button);
            mExitButton = (Button) findViewById(R.id.match_result_exit_button);

            // go to the room activity to choose other player to play again
            mPlayAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String player2 = mPlayingMatch.getPlayer1().equals(Utils.sLoggedPlayer.getId())
                            ? mPlayingMatch.getPlayer2() : mPlayingMatch.getPlayer1();
                    challengePlayer(player2);
                }
            });

            // return to the player main hub
            mExitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MatchResultActivity.this, PlayerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }
            });

            if (Utils.sLoggedPlayer.getId().equals(mPlayingMatch.getPlayer1())) {
                mLoggedPlayer.setImageResource(Utils.getImageIdForChoice(mPlayingMatch.getPlayer1Move()));
                mPlayer2.setImageResource(Utils.getImageIdForChoice(mPlayingMatch.getPlayer2Move()));
            } else {
                mPlayer2.setImageResource(Utils.getImageIdForChoice(mPlayingMatch.getPlayer1Move()));
                mLoggedPlayer.setImageResource(Utils.getImageIdForChoice(mPlayingMatch.getPlayer2Move()));
            }

            if (mPlayingMatch.getWinner() != null) {
                if (mPlayingMatch.getWinner().equals(Utils.sLoggedPlayer.getId())) {
                    mLoggedPlayerTextView.setText(getResources().getString(R.string.match_result_winner));
                    mPlayer2TextView.setText(getResources().getString(R.string.match_result_loser));
                    mResultTextView.setText(getResources().getString(R.string.match_result_you_win));

                    mLoggedPlayerTextView.setTextColor(Color.GREEN);
                    mPlayer2TextView.setTextColor(Color.RED);

                    mLoggedPlayer.setBorderColor(Color.GREEN);
                    mLoggedPlayer.setBorderWidthDP(5);

                    mPlayer2.setBorderColor(Color.RED);
                    mPlayer2.setBorderWidthDP(3);
                } else {
                    mLoggedPlayerTextView.setText(getResources().getString(R.string.match_result_loser));
                    mPlayer2TextView.setText(getResources().getString(R.string.match_result_winner));
                    mResultTextView.setText(getResources().getString(R.string.match_result_you_lose));

                    mPlayer2TextView.setTextColor(Color.GREEN);
                    mLoggedPlayerTextView.setTextColor(Color.RED);

                    mPlayer2.setBorderColor(Color.GREEN);
                    mPlayer2.setBorderWidthDP(3);

                    mLoggedPlayer.setBorderColor(Color.RED);
                    mLoggedPlayer.setBorderWidthDP(1);
                }
            } else {
                mLoggedPlayerTextView.setText(getResources().getString(R.string.match_result_draw));
                mPlayer2TextView.setText(getResources().getString(R.string.match_result_draw));
                mResultTextView.setText(getResources().getString(R.string.match_result_draw));

                mPlayer2TextView.setTextColor(Color.GRAY);
                mLoggedPlayerTextView.setTextColor(Color.GRAY);

                mLoggedPlayer.setBorderColor(Color.GRAY);
                mLoggedPlayer.setBorderWidthDP(3);

                mPlayer2.setBorderColor(Color.GRAY);
                mPlayer2.setBorderWidthDP(3);
            }
            setTextSize();
        }
    }

    private void setTextSize(){
        mLoggedPlayerTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mPlayer2TextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize + 6);
        mPlayAgainButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mExitButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
    }
}
