package br.com.rlmg.jokenpo.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rlmg on 4/3/17.
 */

public class GsonPlayer {

    private String playing;

    private String _id;

    private String name;

    private String __v;

    private String created_at;

    private String logged;

    public String getPlaying ()
    {
        return playing;
    }

    public void setPlaying (String playing)
    {
        this.playing = playing;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String get__v ()
    {
        return __v;
    }

    public void set__v (String __v)
    {
        this.__v = __v;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getLogged ()
    {
        return logged;
    }

    public void setLogged (String logged)
    {
        this.logged = logged;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [playing = "+playing+", _id = "+_id+", name = "+name+", __v = "+__v+", created_at = "+created_at+", logged = "+logged+"]";
    }

    /**
     * Method that converts the string values from the gson model object into java friendly model object
     *
     * @return the converted java object
     */
    public Player convert() {
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            date = formatter.parse(created_at);
        } catch (ParseException exception) {
            return null;
        }

        return new Player(_id, name, new Boolean(logged), new Integer(__v), date);
    }
}