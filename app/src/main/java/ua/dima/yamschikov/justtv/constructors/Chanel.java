package ua.dima.yamschikov.justtv.constructors;

/**
 * Created by user on 05.02.2017.
 */

public class Chanel {

     String title;
     String url;
     int pic;

    public Chanel(int _pic, String _title, String _url) {
        pic = _pic;
        title = _title;
        url = _url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
