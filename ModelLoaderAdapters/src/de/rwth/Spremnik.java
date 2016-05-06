package de.rwth;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by MiniP on 3/27/2016.
 */
public class Spremnik {
    private static Spremnik _instance = new Spremnik();
    private static AtomicReference<String> _url = new AtomicReference<>("http://192.168.1.100:8080"),
            _downloadServiceAddress = new AtomicReference<>("download.php"),
            _piktogramServiceAddress = new AtomicReference<>("piktogram.php"),
            _setServiceAddress = new AtomicReference<>("set.php"),

            _objekatEkst = new AtomicReference<>(),
            _teksturaEkst = new AtomicReference<>();
    private static AtomicReference<Piktogram> piktogram = new AtomicReference<>();


    private Spremnik() {
    }


    public static Spremnik getInstance() {
        return _instance;
    }

    public Piktogram getPiktogram() {
        return piktogram.get();
    }
    public void setPiktogramIme(Piktogram p) {
        piktogram.getAndSet(p);
    }

    public String getObjekatPut() {
        return _objekatEkst.get();
    }
    public void setObjekatPut(String ime) {
        _objekatEkst.getAndSet(ime);
    }

    public String getTeksturaPut() {
        return _teksturaEkst.get();
    }
    public void setTeksturaPut(String ime) {
        _teksturaEkst.getAndSet(ime);
    }

    public String getUrl() {
        return _url.get();
    }
    public void setURL(String url){_url.getAndSet(url);}

    public String getDownloadServiceAddress() {
        return _url.get() + "/" + _downloadServiceAddress.get();
    }

    public String getPiktogramServiceAddress() {
        return _url.get() + "/" + _piktogramServiceAddress;
    }

    public String getSetServiceAddress() {
        return _url.get() + "/" + _setServiceAddress.get();
    }
}