package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model;

public class RightObjects {
    private String chap;
    private String start;
    private String end;

    RightObjects() {
        super();
    }

    public RightObjects(String s, String m, String o) {
        this.chap = s;
        this.start = m;
        this.end = o;
    }

    public String getChap() {
        return chap;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
