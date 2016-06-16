package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model;

public class LeftObjects {

    private String menuName;
    private int menuPic;

    LeftObjects() {
        super();
    }

    public LeftObjects(String s, int x) {
        this.menuName = s;
        this.menuPic = x;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getMenuPic() {
        return menuPic;
    }
}
