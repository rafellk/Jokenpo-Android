package br.com.rlmg.jokenpo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnLogin;
    private EditText mEditTextUser;

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
        if (!validate()) {
            return;
        }

        mBtnLogin.setEnabled(false);

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Wait", "Authenticating...", true);

        new Handler().postDelayed(
            new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    String userName = mEditTextUser.getText().toString();
                    Intent intent = new Intent(LoginActivity.this, PlayerActivity.class);
                    intent.putExtra(Constants.EXTRA_USER, userName);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
    }

    public boolean validate() {
        boolean valid = true;

        String user = mEditTextUser.getText().toString();

        if (user.isEmpty()) {
            mEditTextUser.setError("Enter a valid user name");
            valid = false;
        } else {
            mEditTextUser.setError(null);
        }

        return valid;
    }
}
