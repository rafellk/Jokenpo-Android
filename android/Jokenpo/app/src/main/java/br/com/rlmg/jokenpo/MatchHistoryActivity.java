package br.com.rlmg.jokenpo;

import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.GsonPlayer;
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

    static class ViewHolder {
        TextView player1Name;
        TextView player2Name;
        ImageView movePlayer1;
        ImageView movePlayer2;
        TextView date;
        TextView result;
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

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(MatchHistoryActivity.this).inflate(R.layout.match_hisotry_list_item, null);

                holder = new MatchHistoryActivity.ViewHolder();
                holder.player1Name = (TextView) convertView.findViewById(R.id.player1NameTextView);
                holder.player2Name = (TextView) convertView.findViewById(R.id.player2NameTextView);
                holder.movePlayer1 = (ImageView) convertView.findViewById(R.id.movePlayer1Image);
                holder.movePlayer2 = (ImageView) convertView.findViewById(R.id.movePlayer2Image);
                holder.date = (TextView) convertView.findViewById(R.id.dateTextView);
                holder.result = (TextView) convertView.findViewById(R.id.resultTextView);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            final ViewHolder holderFinal =  holder;

            String idOpponent = match.getPlayer1().equals(Utils.sLoggedPlayer.getId()) ? match.getPlayer2() : match.getPlayer1();

            new AsyncTask<String, Void, HashMap>() {
                @Override
                protected HashMap doInBackground(String... params) {
                    return WebService.getPlayer(params[0]);
                }

                @Override
                protected void onPostExecute(HashMap hashMap) {
                    Player player2 = Utils.getPlayerFromJson(hashMap);

                    String movePlayer1;
                    String movePlayer2;
                    if(match.getPlayer1().equals(Utils.sLoggedPlayer.getId())){
                        movePlayer1 = match.getPlayer1Move();
                        movePlayer2 = match.getPlayer2Move();
                    }
                    else{
                        movePlayer1 = match.getPlayer2Move();
                        movePlayer2 = match.getPlayer1Move();
                    }

                    holderFinal.player1Name.setText(Utils.sLoggedPlayer.getName().trim());
                    holderFinal.player2Name.setText(player2.getName().trim());
                    holderFinal.movePlayer1.setImageResource(Utils.getImageNoBackgroundIdForChoice(movePlayer1));
                    holderFinal.movePlayer2.setImageResource(Utils.getImageNoBackgroundIdForChoice(movePlayer2));
                    holderFinal.date.setText(Utils.getTimePassed(match.getCreatedAt(), getBaseContext()));
                    Pair<String, Integer> result = decideResult(Utils.sLoggedPlayer.getId(), match.getWinner());
                    holderFinal.result.setText(result.first);
                    holderFinal.result.setTextColor(result.second);
                }

                private Pair<String, Integer> decideResult(String playerId, String matchWinner){
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

            }.execute(idOpponent);

            return convertView;
        }
    }
}
