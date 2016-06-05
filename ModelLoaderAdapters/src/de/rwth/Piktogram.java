package de.rwth;

/**
 * Created by MiniP on 3/27/2016.
 */
public class Piktogram {

    private int _id;
    private String _naziv;
    private String _putPiktogram;
    private String _putTekstura;
    private float _latitude,
                _longitude;

    /**
     * No args constructor for use in serialization
     */
    public Piktogram() {
    }

    /**
     * @param id
     * @param naziv
     * @param putPiktogram
     * @param putTekstura
     */
    public Piktogram(int id, String naziv, String putPiktogram, String putTekstura) {
        this._id = id;
        this._naziv = naziv;
        this._putPiktogram = putPiktogram;
        this._putTekstura = putTekstura;
    }

    /**
     * @param id
     * @param naziv
     * @param putPiktogram
     * @param putTekstura
     */
    public Piktogram(int id, String naziv, String putPiktogram, String putTekstura, float latitude, float longitude) {
        this._id = id;
        this._naziv = naziv;
        this._putPiktogram = putPiktogram;
        this._putTekstura = putTekstura;
        this._latitude = latitude;
        this._longitude= longitude;
    }

    /**
     * @return The id
     */
    public int getId() {
        return _id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this._id = id;
    }

    /**
     * @return The naziv
     */
    public String getNaziv() {
        return _naziv;
    }

    /**
     * @param naziv The naziv
     */
    public void setNaziv(String naziv) {
        this._naziv = naziv;
    }

    /**
     * @return The putPiktogram
     */
    public String getPutPiktogram() {
        return _putPiktogram;
    }

    /**
     * @param putPiktogram The put_piktogram
     */
    public void setPutPiktogram(String putPiktogram) {
        this._putPiktogram = putPiktogram;
    }

    /**
     * @return The putTekstura
     */
    public String getPutTekstura() {
        return _putTekstura;
    }

    /**
     * @param putTekstura The put_tekstura
     */
    public void setPutTekstura(String putTekstura) {
        this._putTekstura = putTekstura;
    }

    public float get_latitude() {
        return _latitude;
    }

    public void set_latitude(float _latitude) {
        this._latitude = _latitude;
    }

    public float get_longitude() {
        return _longitude;
    }

    public void set_longitude(float _longitude) {
        this._longitude = _longitude;
    }
}
