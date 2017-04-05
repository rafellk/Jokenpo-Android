package br.com.rlmg.jokenpo.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.rlmg.jokenpo.utils.Utils;

/**
 * Created by rlmg on 4/4/17.
 */

public class InstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String authenticationToken = FirebaseInstanceId.getInstance().getToken();
        Log.v("TAG", authenticationToken);
    }
}
