package br.com.rlmg.jokenpo.webservice;

import java.util.HashMap;

/**
 * Created by rlmg on 4/2/17.
 */

public interface WebServiceCallbackInterface {
    void successCallback(HashMap map);
    void errorCallback(HashMap map);
}
