package br.com.rlmg.jokenpo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import br.com.rlmg.jokenpo.models.GsonPlayer;
import br.com.rlmg.jokenpo.models.Player;
import br.com.rlmg.jokenpo.utils.Utils;
import br.com.rlmg.jokenpo.webservice.WebService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnLogin;
    private EditText mEditTextUser;
    private ProgressDialog mProgressDialog = null;
    //private MediaPlayer media = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEditTextUser = (EditText) findViewById(R.id.editTextUser);
        //media = MediaPlayer.create(this, R.raw.click_one);

        setTextSize();

        mBtnLogin.setOnClickListener(this);
    }

    private void setTextSize(){
        mBtnLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize);
        mEditTextUser.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.sSTextSize + 3);
    }

    @Override
    public void onClick(View v) {
        //media.start();
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
