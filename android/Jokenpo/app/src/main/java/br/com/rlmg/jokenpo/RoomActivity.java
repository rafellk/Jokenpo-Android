package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class RoomActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ProgressDialog mProgressDialog;
    private ListView mListView;
    private List<Player> mFetchedPlayers;
    private SwipeRefreshLayout mRefreshLayout;
    private Match mMatch;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) findViewById(R.id.roomListView);

        registerBroadcast();
        getPlayersOnline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        // needs this because this holds reference
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
     * Method that handles the message received broadcast
     *
     * @param json - String that represents the message content in json
     */
    private void handleBroadcast(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject != null) {
            String action = jsonObject.get("action").getAsString();
            Match match = null;

            switch (action) {
                case Utils.sACCEPT_MATCH_REQUEST:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match that I am waiting for so it should be good
                    if (mMatch != null && mMatch.getId() == match.getId()) {
                        mProgressDialog.dismiss();

                        Intent intent = new Intent(RoomActivity.this, PlayerActivity.class);
                        startActivity(intent);
                    }

                    break;
                case Utils.sDECLINE_MATCH_REQUEST:
                    match = (gson.fromJson(jsonObject.get(WebService.sRESPONSE_DATA).getAsString(), GsonMatch.class)).convert();

                    // if this is the match I am waiting for so it should be good. Dismiss the progress dialog and show an alert dialog informing the match was declined
                    if ((mMatch != null && mMatch.getId().equals(match.getId())) && (match.getPlayer1().equals(Utils.sLoggedPlayer.getId()) || match.getPlayer2().equals(Utils.sLoggedPlayer.getId()))) {
                        mProgressDialog.dismiss();

                        AlertDialog.Builder builder = Utils.buildSimpleDialog(getResources().getString(R.string.declined_match_dialog_title), getResources().getString(R.string.declined_match_dialog_message), RoomActivity.this);

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

    /**
     * Method that creates a match using the logged player and the specified player
     *
     * @param playerId - String that represents the id of the player that will be challenged
     */
    private void challengePlayer(String playerId) {
        new AsyncTask<String, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                mProgressDialog = Utils.createSimpleProgressDialog(getResources().getString(R.string.room_progress_dialog_title), getResources().getString(R.string.room_progress_dialog_message), RoomActivity.this);
            }

            @Override
            protected HashMap doInBackground(String... params) {
                String id = params[0];
                return WebService.challengePlayer(Utils.sLoggedPlayer.getId(), id);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                GsonMatch gsonMatch = (GsonMatch) hashMap.get(WebService.sRESPONSE_DATA);
                mMatch = gsonMatch.convert();
            }
        }.execute(playerId);
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
