package br.com.rlmg.jokenpo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnLogin;
    private EditText mEditTextUser;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEditTextUser = (EditText) findViewById(R.id.editTextUser);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!validateEntry()) {
            return;
        }

        signinOrSignup();
    }

    /**
     * Method that validates if the user input is valid
     *
     * @return a boolean that indicates if the user input is valid or not
     */
    private boolean validateEntry() {
        boolean valid = true;
        String user = mEditTextUser.getText().toString();

        if (user.isEmpty()) {
            mEditTextUser.setError(getResources().getString(R.string.valid_user_name_message));
            return false;
        }

        return valid;
    }

    /**
     * Method that makes a post request to the server to sign up or sign in an user
     */
    private void signinOrSignup() {
        new AsyncTask<String, Void, HashMap>() {
            @Override
            protected void onPreExecute() {
                mBtnLogin.setEnabled(false);
                mProgressDialog = Utils.createSimpleProgressDialog(getResources().getString(R.string.authentication_progress_dialog_title), getResources().getString(R.string.authentication_progress_dialog_message), LoginActivity.this);
            }

            @Override
            protected HashMap doInBackground(String... params) {
                String name = params[0];
                return WebService.signin(name);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                // TODO: show pop up if there was any kind of error during the request
                mProgressDialog.dismiss();
                GsonPlayer gsonPlayer = (GsonPlayer) hashMap.get(WebService.sRESPONSE_DATA);
                Player player = gsonPlayer.convert();

                Intent intent = new Intent(LoginActivity.this, PlayerActivity.class);

                Utils.sLoggedPlayer = player;

                startActivity(intent);
                finish();
            }
        }.execute(mEditTextUser.getText().toString());
    }
}
