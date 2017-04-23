package br.com.rlmg.jokenpo;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.Match;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class PlayerActivity extends BaseActivity {

    private TextView mTextView_User;
    private Button mButtonPlay;
    private Button mButtonViewHistory;
    private Button mButtonLogout;
    private Button mButtonSettings;
    private String mRequestCode = "AlreadyCreated";
    //private MediaPlayer media = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //media = MediaPlayer.create(this, R.raw.click_one);

        // if this intent contains extras then the notification was tapped and we should present a dialog to accept or decline
        if (getIntent().getExtras() != null) {
            final String json = (String) getIntent().getExtras().get("json");
            final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            createChallengeDialog(json);
        }

        getIntent().setAction(mRequestCode);

        mTextView_User = (TextView) findViewById(R.id.textView_User);
        mTextView_User.setText(Utils.sLoggedPlayer.getName());

        mButtonPlay = (Button) findViewById(R.id.btn_play);
        mButtonViewHistory = (Button) findViewById(R.id.btn_viewHistory);
        mButtonLogout = (Button) findViewById(R.id.btn_logout);
        mButtonSettings = (Button) findViewById(R.id.btn_settings);

        setTextSize();

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

        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {settings(v);}
        });
    }

    public void play(View v) {
        //media.start();
        Intent intent = new Intent(PlayerActivity.this, RoomActivity.class);
        startActivity(intent);
    }

    public void viewHistory(View v) {
        //media.start();
        Intent intent = new Intent(PlayerActivity.this, MatchHistoryActivity.class);
        startActivity(intent);
    }

    public void settings(View v) {
        //media.start();
        Intent intent = new Intent(PlayerActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String action = getIntent().getAction();
        if(action == null || !action.equals(mRequestCode)) {
            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            getIntent().setAction(null);
        }
    }

    /**
     * Method that makes an asynchronous logout request to the webserver
     *
     * @param v
     */
    public void Logout(View v) {
        //media.start();
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

    private void setTextSize(){
        mTextView_User.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mButtonPlay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mButtonViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mButtonSettings.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mButtonLogout.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
    }
}
