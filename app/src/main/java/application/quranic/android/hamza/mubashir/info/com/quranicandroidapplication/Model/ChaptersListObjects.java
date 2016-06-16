package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model;

public class ChaptersListObjects {
    private int rowid_number;
    private String verse_chapter;
    private String english_chapter;
    private String arabic_chapter;
    private int bookmarks_chapter;
    private int arabic_resource;

    ChaptersListObjects() {
        super();
    }

    public ChaptersListObjects(int rowid, String verse_chapter, String english_chapter, String arabic_chapter,
                               int bookmarks_chapter, int arabic_resource) {
        this.rowid_number = rowid;
        this.verse_chapter = verse_chapter;
        this.english_chapter = english_chapter;
        this.arabic_chapter = arabic_chapter;
        this.bookmarks_chapter = bookmarks_chapter;
        this.arabic_resource = arabic_resource;
    }

    public int getRowid_number() {
        return rowid_number;
    }

    public String getVerse_chapter() {
        return verse_chapter;
    }

    public String getEnglish_chapter() {
        return english_chapter;
    }

    public String getArabic_chapter() {
        return arabic_chapter;
    }

    public int getBookmarks_chapter() {
        return bookmarks_chapter;
    }

    public int getArabic_resource() {
        return arabic_resource;
    }

    public void setRowid_number(int rowid_number) {
        this.rowid_number = rowid_number;
    }

    public void setVerse_chapter(String verse_chapter) {
        this.verse_chapter = verse_chapter;
    }

    public void setEnglish_chapter(String english_chapter) {
        this.english_chapter = english_chapter;
    }

    public void setArabic_chapter(String arabic_chapter) {
        this.arabic_chapter = arabic_chapter;
    }

    public void setBookmarks_chapter(int bookmarks_chapter) {
        this.bookmarks_chapter = bookmarks_chapter;
    }

    public void setArabic_resource(int arabic_resource) {
        this.arabic_resource = arabic_resource;
    }
}