package br.com.rlmg.jokenpo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Player;
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
            mEditTextUser.setError("Enter a valid user name");
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
                mProgressDialog = ProgressDialog.show(LoginActivity.this, "Wait", "Authenticating...", true);
            }

            @Override
            protected HashMap doInBackground(String... params) {
                String name = params[0];
                return WebService.signin(name);
            }

            @Override
            protected void onPostExecute(HashMap hashMap) {
                mProgressDialog.dismiss();
                Player player = ((GsonPlayer) hashMap.get(WebService.sRESPONSE_DATA)).convert();
                // TODO: save the logged user to the application defaults

                Intent intent = new Intent(LoginActivity.this, PlayerActivity.class);

                // TODO: remove this in the future because we gonna get this value from the application defaults
                intent.putExtra(Constants.EXTRA_USER, player.getName());

                startActivity(intent);
                finish();
            }
        }.execute(mEditTextUser.getText().toString());
    }
}
