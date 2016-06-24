package de.rwth;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by MiniP on 3/27/2016.
 */
public class Spremnik {
    private static Spremnik _instance = new Spremnik();
    //private static AtomicReference<String> _url = new AtomicReference<>("http://192.168.1.3"), // "http://filter.omniapps.info"),
    private static AtomicReference<String> _url = new AtomicReference<>("http://filter.omniapps.info"),
            _downloadServiceAddress = new AtomicReference<>("download.php"),
            _piktogramServiceAddress = new AtomicReference<>("piktogram.php"),
            _setServiceAddress = new AtomicReference<>("set.php"),
            _piktogramLokacijaServiceAddress = new AtomicReference<>("piktogramLokacije.php"),
            _uploadPictureServiceAddress = new AtomicReference<>("uploadPicture.php"),
            _currId = new AtomicReference<>("1"),
            _lastId = new AtomicReference<>("1"),
            _userServiceAddress = new AtomicReference<>("user.php"),
            _userId = new AtomicReference<>("0"),
            _userName = new AtomicReference<>(""),

            _objekatEkst = new AtomicReference<>(),
            _teksturaEkst = new AtomicReference<>(),

            _slikaPath = new AtomicReference<>(),
            _previousActivity = new AtomicReference<>();
    private static AtomicReference<Piktogram> piktogram = new AtomicReference<>();


    private Spremnik() {
    }


    public static Spremnik getInstance() { return _instance; }

    public AtomicReference<String> get_slikaPath() { return _slikaPath; }
    public void set_slikaPath(AtomicReference<String> _slikaPath) { Spremnik._slikaPath = _slikaPath; }

    public String getCurrId() { return _currId.get();}
    public static void setCurrId(String value) {
        Spremnik._currId.getAndSet(value);
    }

    public String getLastId() { return _lastId.get();}
    public static void setLastId(String value) {
        Spremnik._lastId.getAndSet(value);
    }

    public Piktogram getPiktogram() {
        return piktogram.get();
    }
    public void setPiktogramIme(Piktogram p) {
        piktogram.getAndSet(p);
    }

    public String getUserId() { return _userId.get(); }
    public void setUserId(String value) { _userId.getAndSet(value);}

    public String getUserName() { return _userName.get(); }
    public void setUserName(String value) { _userName.getAndSet(value);}

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

    public String getUploadPictureServiceAddress() {
        return _url.get() + "/" + _uploadPictureServiceAddress.get();
    }

    public String getUserServiceAddress() {
        return _url.get() + "/" + _userServiceAddress.get();
    }

    public String getPiktogramLokacijaServiceAddress() {
        return _url.get() + "/" + _piktogramLokacijaServiceAddress.get();
    }

    public String getDownloadServiceAddress() {
        return _url.get() + "/" + _downloadServiceAddress.get();
    }

    public String getPiktogramServiceAddress() {
        return _url.get() + "/" + _piktogramServiceAddress;
    }

    public String getSetServiceAddress() {
        return _url.get() + "/" + _setServiceAddress.get();
    }
    public String getPreviousActivity() {
        return _previousActivity.get();
    }
    public void setPreviousActivity(String previousActivity) {
        this._previousActivity.getAndSet(previousActivity);
    }
}