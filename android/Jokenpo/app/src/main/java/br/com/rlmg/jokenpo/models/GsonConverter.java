package br.com.rlmg.jokenpo.models;

/**
 * Created by rlmg on 4/4/17.
 */

public interface GsonConverter<T> {
    /**
     * Method that converts the string values from the gson model object into java friendly model object
     *
     * @return the converted java object
     */
    public T convert();
}
