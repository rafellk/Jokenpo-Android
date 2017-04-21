package br.com.rlmg.jokenpo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class MatchHistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mListView;
    private List<Match> mFetchedMatch;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        setTitle(getResources().getString(R.string.match_history_title));

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshMatchLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mListView = (ListView) findViewById(R.id.matchHistoryListView);

        getMatches();
    }

    @Override
    public void onRefresh() {
        getMatches();
    }

    /**
     * Method that retrieves the matches of currently player
     */
    private void getMatches() {
        new AsyncTask<Void, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                mRefreshLayout.setRefreshing(true);
            }

            @Override
            protected HashMap doInBackground(Void... params) {
                return WebService.getMatchesPlayerHistory(Utils.sLoggedPlayer.getId());
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                mFetchedMatch = new ArrayList<Match>();

                GsonMatch gsonMatch[] = (GsonMatch[]) hashMap.get(WebService.sRESPONSE_DATA);
                for (GsonMatch match : gsonMatch) {
                    mFetchedMatch.add(match.convert());
                }

                setupListView();
                mRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    /**
     * Method that configures the list view adapter and OnItemClickListener observer
     */
    private void setupListView() {
        mListView.setAdapter(new MatchListViewAdapter());
    }

    /**
     * A generic class that represents the data that will be displayed in each list view cell
     */
    private class MatchViewHolder {
        TextView rageQuitMessage;
        TextView player1Name;
        TextView player2Name;
        ImageView movePlayer1;
        ImageView movePlayer2;
        TextView date;
        TextView result;
        boolean rageQuitted;
    }

    private class MatchListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFetchedMatch.size();
        }

        @Override
        public Object getItem(int position) {
            return mFetchedMatch.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Match match = mFetchedMatch.get(position);

            MatchViewHolder matchHolder = null;
            final boolean matchRageQuitted = matchRageQuitted(match);

            if (convertView == null) {
                matchHolder = new MatchViewHolder();
                convertView = inflateConvertView(matchHolder, matchRageQuitted);
            } else {
                matchHolder = (MatchViewHolder) convertView.getTag();

                // if the last match index rage quit boolean property is different than the actual match index then we need to inflate a different list view cell
                if (matchRageQuitted != matchHolder.rageQuitted) {
                    convertView = inflateConvertView(matchHolder, matchRageQuitted);
                }
            }

            matchHolder.rageQuitted = matchRageQuitted;

            final MatchViewHolder matchHolderFinal =  matchHolder;

            String idOpponent = match.getPlayer1().equals(Utils.sLoggedPlayer.getId()) ? match.getPlayer2() : match.getPlayer1();

            new AsyncTask<String, Void, HashMap>() {
                @Override
                protected HashMap doInBackground(String... params) {
                    return WebService.getPlayer(params[0]);
                }

                @Override
                protected void onPostExecute(HashMap hashMap) {
                    Player player2 = Utils.getPlayerFromJson(hashMap);
                    String loggedPlayerID = Utils.sLoggedPlayer.getId();

                    String movePlayer1;
                    String movePlayer2;
                    if(match.getPlayer1().equals(loggedPlayerID)) {
                        movePlayer1 = match.getPlayer1Move();
                        movePlayer2 = match.getPlayer2Move();
                    }
                    else{
                        movePlayer1 = match.getPlayer2Move();
                        movePlayer2 = match.getPlayer1Move();
                    }

                    if (matchRageQuitted) {
                        if (loggedPlayerID.equals(match.getWinner())) {
                            matchHolderFinal.rageQuitMessage.setText(player2.getName() + " rage quitted the match :(");
                        } else {
                            matchHolderFinal.rageQuitMessage.setText("You rage quitted the match :(");
                        }

                        matchHolderFinal.date.setText(Utils.getTimePassed(match.getCreatedAt(), getBaseContext()));
                        Pair<String, Integer> result = decideResult(Utils.sLoggedPlayer.getId(), match.getWinner());
                        matchHolderFinal.result.setText(result.first);
                        matchHolderFinal.result.setTextColor(result.second);
                    } else {
                        matchHolderFinal.player1Name.setText(Utils.sLoggedPlayer.getName().trim());
                        matchHolderFinal.player2Name.setText(player2.getName().trim());
                        matchHolderFinal.movePlayer1.setImageResource(Utils.getImageNoBackgroundIdForChoice(movePlayer1));
                        matchHolderFinal.movePlayer2.setImageResource(Utils.getImageNoBackgroundIdForChoice(movePlayer2));
                        matchHolderFinal.date.setText(Utils.getTimePassed(match.getCreatedAt(), getBaseContext()));
                        Pair<String, Integer> result = decideResult(Utils.sLoggedPlayer.getId(), match.getWinner());
                        matchHolderFinal.result.setText(result.first);
                        matchHolderFinal.result.setTextColor(result.second);
                    }
                }
            }.execute(idOpponent);

            return convertView;
        }
    }

    /**
     * Method that determines if the match ended by rage quit or not
     *
     * @param match - The match to be analised
     *
     * @return TRUE if the match was rage quitted. FALSE otherwise
     */
    private boolean matchRageQuitted(Match match) {
        return match.getPlayer1Move().equals("NONE") || match.getPlayer2Move().equals("NONE");
    }

    /**
     * Method that receives a view and fill a MatchViewHolder with the view's properties
     *
     * @param matchViewHolder - the holder to be filled
     *
     * @param convertView - The view to be used as source
     */
    private void fillMatchViewHolder(MatchViewHolder matchViewHolder, View convertView) {
        matchViewHolder.rageQuitMessage = (TextView) convertView.findViewById(R.id.rageQuitMessage);
        matchViewHolder.player2Name = (TextView) convertView.findViewById(R.id.player2NameTextView);
        matchViewHolder.player1Name = (TextView) convertView.findViewById(R.id.player1NameTextView);
        matchViewHolder.movePlayer1 = (ImageView) convertView.findViewById(R.id.movePlayer1Image);
        matchViewHolder.movePlayer2 = (ImageView) convertView.findViewById(R.id.movePlayer2Image);
        matchViewHolder.date = (TextView) convertView.findViewById(R.id.dateTextView);
        matchViewHolder.result = (TextView) convertView.findViewById(R.id.resultTextView);
    }

    /**
     * Method that inflates the correct list view cell depending on the rage quit boolean flag
     *
     * @param matchViewHolder - the MatchViewHolder to hold the cell values for future use
     *
     * @param matchRageQuitted - a boolean that represents if the match was rage quitted or not
     */
    private View inflateConvertView(MatchViewHolder matchViewHolder, boolean matchRageQuitted) {
        View convertView = null;

        // if the match was rage quitted then load the specific rage quit game list view cell
        if (matchRageQuitted) {
            convertView = LayoutInflater.from(MatchHistoryActivity.this).inflate(R.layout.match_history_rage_list_item, null);
        } else {
            convertView = LayoutInflater.from(MatchHistoryActivity.this).inflate(R.layout.match_hisotry_list_item, null);
        }

        fillMatchViewHolder(matchViewHolder, convertView);
        convertView.setTag(matchViewHolder);

        return convertView;
    }

    /**
     * Method that decides the match result based on the player id
     * @param playerId
     * @param matchWinner
     * @return
     */
    private Pair<String, Integer> decideResult(String playerId, String matchWinner) {
        if(matchWinner == null || matchWinner.isEmpty()){
            return new Pair<String, Integer>(getResources().getString(R.string.match_result_draw), Color.GRAY);
        }
        else if(matchWinner.equals(playerId)){
            return new Pair<String, Integer>(getResources().getString(R.string.match_result_winner),Color.GREEN);
        }
        else{
            return new Pair<String, Integer>(getResources().getString(R.string.match_result_loser),Color.RED);
        }
    }
}
