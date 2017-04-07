package br.com.rlmg.jokenpo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

            Button playAgain = (Button) findViewById(R.id.match_result_play_again_button);
            Button exit = (Button) findViewById(R.id.match_result_exit_button);

            // go to the room activity to choose other player to play again
            playAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String player2 = mPlayingMatch.getPlayer1().equals(Utils.sLoggedPlayer.getId()) ? mPlayingMatch.getPlayer2() : mPlayingMatch.getPlayer1();
                    challengePlayer(player2);
                }
            });

            // return to the player main hub
            exit.setOnClickListener(new View.OnClickListener() {
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
                    mLoggedPlayerTextView.setText("Winner");
                    mPlayer2TextView.setText("Looser");

                    mLoggedPlayerTextView.setTextColor(Color.GREEN);
                    mPlayer2TextView.setTextColor(Color.GRAY);

                    mLoggedPlayer.setBorderColor(Color.GREEN);
                    mLoggedPlayer.setBorderWidthDP(5);

                    mPlayer2.setBorderColor(Color.GRAY);
                    mPlayer2.setBorderWidthDP(1);
                } else {
                    mLoggedPlayerTextView.setText("Looser");
                    mPlayer2TextView.setText("Winner");

                    mPlayer2TextView.setTextColor(Color.GREEN);
                    mLoggedPlayerTextView.setTextColor(Color.GRAY);

                    mPlayer2.setBorderColor(Color.GREEN);
                    mPlayer2.setBorderWidthDP(5);

                    mLoggedPlayer.setBorderColor(Color.GRAY);
                    mLoggedPlayer.setBorderWidthDP(1);
                }
            } else {
                mLoggedPlayerTextView.setText("Draw");
                mPlayer2TextView.setText("Draw");

                mPlayer2TextView.setTextColor(Color.YELLOW);
                mLoggedPlayerTextView.setTextColor(Color.YELLOW);

                mLoggedPlayer.setBorderColor(Color.YELLOW);
                mLoggedPlayer.setBorderWidthDP(1);

                mPlayer2.setBorderColor(Color.YELLOW);
                mPlayer2.setBorderWidthDP(1);
            }
        }
    }

}
