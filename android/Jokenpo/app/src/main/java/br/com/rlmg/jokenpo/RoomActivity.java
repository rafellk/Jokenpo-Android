package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.rlmg.jokenpo.models.GsonMatch;
import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class RoomActivity extends MatchMakingProcessBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mListView;
    private List<Player> mFetchedPlayers;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) findViewById(R.id.roomListView);

        getPlayersOnline();
    }

    /**
     * Method that registers the context to receive refresh event notifications
     */
    @Override
    public void onRefresh() {
        getPlayersOnline();
    }

    /**
     * Method that retrieves the players currently online and update the list view with its refresh layout
     */
    private void getPlayersOnline() {
        new AsyncTask<Void, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                mRefreshLayout.setRefreshing(true);
            }

            @Override
            protected HashMap doInBackground(Void... params) {
                return WebService.getPlayersOnline(Utils.sLoggedPlayer.getId());
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                // TODO: handle http or any kind of error here before updating the UI
                mFetchedPlayers = new ArrayList<Player>();

                GsonPlayer gsonPlayer[] = (GsonPlayer[]) hashMap.get(WebService.sRESPONSE_DATA);
                for (GsonPlayer player : gsonPlayer) {
                    mFetchedPlayers.add(player.convert());
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
        mListView.setAdapter(new RoomListViewAdapter());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = mFetchedPlayers.get(position);
                challengePlayer(player.getId());
            }
        });
    }

    static class ViewHolder {
        TextView roomListViewItemPlayerName;
    }

    private class RoomListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFetchedPlayers.size();
        }

        @Override
        public Object getItem(int position) {
            return mFetchedPlayers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Player player = mFetchedPlayers.get(position);

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(RoomActivity.this).inflate(R.layout.room_list_item, null);

                holder = new ViewHolder();
                holder.roomListViewItemPlayerName = (TextView) convertView.findViewById(R.id.roomListViewItemPlayerName);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.roomListViewItemPlayerName.setText(player.getName());

            return convertView;
        }
    }
}
