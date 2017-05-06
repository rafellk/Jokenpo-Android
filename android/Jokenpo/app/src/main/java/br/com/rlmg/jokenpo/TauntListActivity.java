package br.com.rlmg.jokenpo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.rlmg.jokenpo.utils.Utils;

public class TauntListActivity extends AppCompatActivity {

    private ListView mListView;
    private List<Pair<String, String>> taunts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taunt_list);

        mListView = (ListView) findViewById(R.id.tauntListView);
        taunts = new ArrayList<>();
        taunts.add(new Pair<String, String>(Utils.sTAUNT_LOOSER, this.getString(R.string.taunt_loser)));
        taunts.add(new Pair<String, String>(Utils.sTAUNT_GOOD_LUCK, this.getString(R.string.taunt_good_luck)));
        taunts.add(new Pair<String, String>(Utils.sTAUNT_SMILE, this.getString(R.string.taunt_smile)));
        taunts.add(new Pair<String, String>(Utils.sTAUNT_CRY, this.getString(R.string.taunt_cry)));

        mListView.setAdapter(new TauntListViewAdapter());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String taunt = taunts.get(position).first;

                Intent returnIntent = new Intent();
                returnIntent.putExtra("taunt", taunt);
                setResult(AppCompatActivity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    static class ViewHolder {
        TextView taunt;
    }

    private class TauntListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return taunts.size();
        }

        @Override
        public Object getItem(int position) {
            return taunts.get(position).second;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String taunt = taunts.get(position).second;

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(TauntListActivity.this).inflate(R.layout.room_list_item, null);

                holder = new ViewHolder();
                holder.taunt = (TextView) convertView.findViewById(R.id.roomListViewItemPlayerName);
                setTextSize(holder.taunt);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.taunt.setText(taunt);

            return convertView;
        }

        private void setTextSize(TextView taunt){
            taunt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        }
    }
}
