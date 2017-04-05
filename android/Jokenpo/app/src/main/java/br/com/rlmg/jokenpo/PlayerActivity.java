package br.com.rlmg.jokenpo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class PlayerActivity extends AppCompatActivity {

    private TextView mTextView_User;
    private Button mButtonPlay;
    private Button mButtonViewHistory;
    private Button mButtonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        TextView textView = (TextView) findViewById(R.id.textView_User);
        textView.setText(Utils.sLoggedPlayer.getName());

        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mButtonViewHistory = (Button) findViewById(R.id.btn_viewHistory);
        mButtonLogout = (Button) findViewById(R.id.btn_logout);

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {play(v);}
        });

        mButtonViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {viewHistory(v);}
        });

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Logout(v);}
        });
    }

    public void play(View v) {

    }

    public void viewHistory(View v) {

    }

    /**
     * Method that makes an asynchronous logout request to the webserver
     *
     * @param v
     */
    public void Logout(View v) {
        new AsyncTask<String, Void, HashMap>() {
            @Override
            protected HashMap doInBackground(String... params) {
                String id = params[0];
                return WebService.logout(id);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                // TODO: show pop up if there was any kind of error during the request
                Utils.sLoggedPlayer = null;

                Intent intent = new Intent(PlayerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }.execute(Utils.sLoggedPlayer.getId());
    }
}
