package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SQLHelper_DataBase_Creation_Query_Deletion extends SQLiteOpenHelper {

    private static String DB_PATH = "data/data/com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp/databases/";
    private static final String DATABASE_NAME = "quranDB.sqlite";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_FULLQURAN = "fullquran";
    public static final String TABLE_NAME_CHAPTERS = "chapters";
    public static final String BOOKMARKS_NAME = "bookmarks";
    public static SQLiteDatabase sqLiteDatabase;
    public Context context;
    public String[][] chapters_names_for_list_view_main_activity;
    int start, end;
    private ArrayList<Integer> rowid;
    private ArrayList<String> verse_name;
    private ArrayList<String> english;
    private ArrayList<String> arabic;
    private ArrayList<Integer> bookmarks;
    private ArrayList<String> bookmarks_hash;
    private Set<String> rowid_hash, verse_hash, english_hash, arabic_hash;

    private String hadiths;
    Boolean firstRow = true;
    List<String> search;
    String[][] dbArray;

    public SQLHelper_DataBase_Creation_Query_Deletion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void createDataBase() throws IOException {
        boolean databaseExist = checkDataBase();
        if (!databaseExist) {
            copyDataBase();
        }
    }

    public boolean checkDataBase() {
        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }

    private void copyDataBase() throws IOException {
        File createFile = new File(DB_PATH);
        createFile.mkdir();
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String OutPutFile = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(OutPutFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws IOException {
        String myPath = DB_PATH + DATABASE_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public void openDB() throws IOException {
        String myPath = DB_PATH + DATABASE_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createChapterList() {
        String chapQuery = "SELECT chapters, startchapter, endchapter FROM " + TABLE_NAME_CHAPTERS;
        chapters_names_for_list_view_main_activity = new String[114][3];
        Cursor cursor = sqLiteDatabase.rawQuery(chapQuery, null);
        int x = 0;

        if (cursor.moveToFirst()) {
            do {
                chapters_names_for_list_view_main_activity[x][0] = cursor.getString(0);
                chapters_names_for_list_view_main_activity[x][1] = cursor.getString(1);
                chapters_names_for_list_view_main_activity[x][2] = cursor.getString(2);
                x++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        grabHadithsViews();
    }

    public void grabChapterViews() {
        String queryChapter = "SELECT rowid, chver, english, arabic FROM "+ TABLE_NAME_FULLQURAN +" WHERE (rowid BETWEEN " + start + " AND " + end +")";
        rowid = new ArrayList<Integer>();
        verse_name = new ArrayList<String>();
        english = new ArrayList<String>();
        arabic = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery(queryChapter, null);

        if (cursor.moveToFirst()) {
            do {
                rowid.add(cursor.getInt(0));
                verse_name.add(cursor.getString(1));
                english.add(cursor.getString(2));
                arabic.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();

        grabBookmarkViews();
    }

    public void grabBookmarkViews() {
        String queryBookmark = "SELECT book FROM "+ BOOKMARKS_NAME +" WHERE (rowid BETWEEN " + start + " AND " + end +")";
        bookmarks = new ArrayList<Integer>();
        Cursor cursor = sqLiteDatabase.rawQuery(queryBookmark, null);

        if (cursor.moveToFirst()) {
            do {
                bookmarks.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
    }

    public void grabHadithsViews() {
        Random rand = new Random();
        int randomNum = rand.nextInt((20 - 1) + 1) + 1;
        String queryHadiths = "SELECT hadiths FROM hadith WHERE rowid == " + randomNum + ";";
        Cursor cursor = sqLiteDatabase.rawQuery(queryHadiths, null);

        if (cursor.moveToFirst()) {
            do {
                hadiths = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void updateBookMarksSave(int row) {
        sqLiteDatabase.execSQL("UPDATE bookmarks SET book = 1 WHERE  rowid = " + row + ";");
    }

    public void updateBookMarksDelete(int row) {
        sqLiteDatabase.execSQL("UPDATE bookmarks SET book = 0 WHERE  rowid = " + row + ";");
    }

    public void grabBookMarksFragmentView() {

        String queryBookFrag = "SELECT fullquran.rowid, fullquran.chver, fullquran.english, fullquran.arabic, bookmarks.book " +
        "FROM fullquran CROSS JOIN bookmarks ON fullquran.rowid = bookmarks.rowid ORDER BY bookmarks.book == 0;";

        rowid = new ArrayList<Integer>();
        verse_name = new ArrayList<String>();
        english = new ArrayList<String>();
        arabic = new ArrayList<String>();
        bookmarks = new ArrayList<Integer>();

        Cursor cursor = sqLiteDatabase.rawQuery(queryBookFrag, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(4) == 1) {
                    rowid.add(cursor.getInt(0));
                    verse_name.add(cursor.getString(1));
                    english.add(cursor.getString(2));
                    arabic.add(cursor.getString(3));
                    bookmarks.add(cursor.getInt(4));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public void getSearchPatterns(){
        String query = "SELECT fullquran.rowid, fullquran.chver, fullquran.english, fullquran.arabic, bookmarks.book " +
        "FROM fullquran CROSS JOIN bookmarks ON fullquran.rowid = bookmarks.rowid ORDER BY fullquran.rowid;";

        rowid_hash = new LinkedHashSet<String>();
        verse_hash = new LinkedHashSet<String>();
        english_hash = new LinkedHashSet<String>();
        arabic_hash = new LinkedHashSet<String>();
        bookmarks_hash = new ArrayList<String>();

        String ver, eng, arb, row, bkm;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        String searchMatch;

        for (String single : search) {
            Pattern pattern = Pattern.compile("\\b" + single + "(er|ing|ed|s|'s)?\\b", Pattern.CASE_INSENSITIVE);

            if (cursor.moveToFirst()) {

                do {

                    searchMatch = cursor.getString(2);

                    Matcher match = pattern.matcher(searchMatch);

                    if (match.find()) {
                        row = cursor.getString(0);
                        ver = cursor.getString(1);
                        eng = cursor.getString(2);
                        arb = cursor.getString(3);
                        bkm = cursor.getString(4);
                        english_hash.add(eng);


                        if (firstRow) {
                            rowid_hash.add(row);
                            verse_hash.add(ver);
                            english_hash.add(eng);
                            arabic_hash.add(arb);
                            bookmarks_hash.add(bkm);
                            firstRow = false;
                        }
                        else if (!firstRow){
                            if (english_hash.size() != rowid_hash.size()) {
                                rowid_hash.add(row);
                                verse_hash.add(ver);
                                english_hash.add(eng);
                                arabic_hash.add(arb);
                                bookmarks_hash.add(bkm);
                            }
                        }
                    }
                } while (cursor.moveToNext());

            }
        }

        cursor.close();

        createArray();
    }

    private void createArray() {
        if (rowid_hash.size() != 0) {
            dbArray = new String[rowid_hash.size()][5];
            int x = 0;

            Iterator numIT = rowid_hash.iterator();
            Iterator chvIT = verse_hash.iterator();
            Iterator arbIT = arabic_hash.iterator();
            Iterator engIT = english_hash.iterator();
            Iterator bkmIT = bookmarks_hash.iterator();

            do {
                dbArray[x][0] = numIT.next().toString();
                dbArray[x][1] = chvIT.next().toString();
                dbArray[x][2] = engIT.next().toString();
                dbArray[x][3] = arbIT.next().toString();
                dbArray[x][4] = bkmIT.next().toString();
                x++;

            } while (numIT.hasNext());

            Arrays.sort(dbArray, new Comparator<String[]>() {
                @Override
                public int compare(String[] lhs, String[] rhs) {
                    String rowFirst = lhs[3];
                    String rowSecond = rhs[3];
                    return rowFirst.compareTo(rowSecond);
                }
            });
        }
        else {
            dbArray = new String[0][0];
        }

    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setSearching(List<String> searching) {
        this.search = searching;
    }

    public String[][] getChapters_names_for_list_view_main_activity() {
        return chapters_names_for_list_view_main_activity;
    }

    public String getHadiths() {
        return hadiths;
    }

    public ArrayList<Integer> getRowid() {
        return rowid;
    }

    public ArrayList<String> getVerse_name() {
        return verse_name;
    }

    public ArrayList<String> getEnglish() {
        return english;
    }

    public ArrayList<String> getArabic() {
        return arabic;
    }

    public ArrayList<Integer> getBookmarks() {
        return bookmarks;
    }

    public String[][] getDbArray() {
        return dbArray;
    }

}
