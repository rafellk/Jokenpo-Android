package br.com.rlmg.jokenpo.webservice;

import java.util.HashMap;

/**
 * Created by rlmg on 4/2/17.
 */

public interface WebServiceCallbackInterface {
    public void successCallback(HashMap map);
    public void errorCallback(HashMap map);
}
