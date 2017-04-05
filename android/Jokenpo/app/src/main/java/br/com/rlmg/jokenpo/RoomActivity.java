package br.com.rlmg.jokenpo;

import android.app.ProgressDialog;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class RoomActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ListView mListView;
    private List<Player> mFetchedPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        mListView = (ListView) findViewById(R.id.roomListView);

        new AsyncTask<Void, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                // run spinner here
                mProgressDialog = ProgressDialog.show(RoomActivity.this, getResources().getString(R.string.room_progress_dialog_title), getResources().getString(R.string.room_progress_dialog_message), true);
            }

            @Override
            protected HashMap doInBackground(Void... params) {
                return WebService.getPlayersOnline(Utils.sLoggedPlayer.getId());
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                mFetchedPlayers = new ArrayList<Player>();

                GsonPlayer gsonPlayer[] = (GsonPlayer[]) hashMap.get(WebService.sRESPONSE_DATA);
                for (GsonPlayer player : gsonPlayer) {
                    mFetchedPlayers.add(player.convert());
                }

                mListView.setAdapter(new RoomListViewAdapter());
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    static class ViewHolder {
        TextView roomListViewItemText;
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
                holder.roomListViewItemText = (TextView) convertView.findViewById(R.id.roomListViewItemText);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.roomListViewItemText.setText(player.getName());

            return convertView;
        }
    }
}
