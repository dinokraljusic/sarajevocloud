package de.rwth;

/**
 * Created by MiniP on 3/26/2016.
 */
public class Set {

    int _id;
    String _naziv;


    public Set(int id, String naziv){
        this._id = id;
        this._naziv=naziv;
    }


    public int getId(){
        return  _id;
    }
    public  String getNaziv(){
        return  _naziv;
    }
}
