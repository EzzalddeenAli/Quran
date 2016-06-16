package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.media.MediaPlayer;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp.Async_task_fragment_chapters_bookmarks.ChaptersListObjects;

import java.util.ArrayList;

public class Bookmarks_ListView extends ArrayAdapter<ChaptersListObjects> {
    LayoutInflater getInflater = null;
    Context context;
    ArrayList<ChaptersListObjects> data = null;
    boolean playing = false;
    int EXPANSION_NUMBER;
    int[] disPic;
    MediaPlayer mp = null;
    View v = null;
    ArrayList<Integer> lastPos = new ArrayList<Integer>();
    Fragment_Bookmarks bookers = null;
    TextView results_textview;
    int lengther = 0;

    public Bookmarks_ListView(Context context, LayoutInflater getLayoutInflater, ArrayList<ChaptersListObjects> aryList, int exp_num, View view) {
        super(context, android.R.layout.simple_list_item_single_choice, aryList);
        this.getInflater = getLayoutInflater;
        this.context = context;
        this.EXPANSION_NUMBER = exp_num;
        this.disPic = new int[aryList.size()];
        this.v = view;
        data = aryList;
        lengther = aryList.size();

        results_textview = (TextView) view.findViewById(R.id.number_of_results);

        for (int x = 0; x < aryList.size(); x++) {
            disPic[x] = 0;
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void remove(ChaptersListObjects object) {
        super.remove(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public ChaptersListObjects getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(ChaptersListObjects item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Animation pushed_in = AnimationUtils.loadAnimation(getContext(), R.anim.buttons_anim_push_in);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chapters_bookmarks_search_custom_listview_layout, parent, false);
        }

        final ChaptersListObjects listObjects = super.getItem(position);

        if (listObjects != null) {
            holder = new ViewHolder();

            holder.player = (ImageButton) convertView.findViewById(R.id.button_player);
            holder.book = (ImageButton) convertView.findViewById(R.id.button_booker);
            holder.arb = (ImageView) convertView.findViewById(R.id.arabic_verse_textview);
            holder.eng_ver = (TextView) convertView.findViewById(R.id.english_verse_textview);
            holder.ver_name = (TextView) convertView.findViewById(R.id.verse_name_textview);

            if (holder.player != null || holder.book != null || holder.arb != null ||
                    holder.eng_ver != null || holder.ver_name != null) {

                final Typeface font_style = Typeface.createFromAsset(context.getAssets(), "list_textviews.otf");

                holder.eng_ver.setText(listObjects.english_chapter);
                Spanned verse_bold = Html.fromHtml("<b>" + listObjects.verse_chapter + "</b>");
                holder.ver_name.setText(verse_bold);

                holder.ver_name.setTypeface(font_style);
                holder.eng_ver.setTypeface(font_style);

                if (listObjects.bookmarks_chapter == 1) {
                    holder.book.setBackgroundResource(context.getResources().getIdentifier("bookmark_saved", "drawable", context.getPackageName()));
                }

                if (disPic[position] == 0) {
                    holder.player.setBackgroundResource(context.getResources().getIdentifier("play", "drawable", context.getPackageName()));
                } else if (disPic[position] == 1) {
                    holder.player.setBackgroundResource(context.getResources().getIdentifier("pause", "drawable", context.getPackageName()));
                }

                holder.arb.setImageResource(listObjects.arabic_resource);

                holder.book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        v.startAnimation(pushed_in);
                        viewToastCustom(R.string.bookmark_deleted_saved_string, font_style);
                        dbConnectUpdater(false, listObjects.rowid_number);
                        remove(getItem(position));

                        updatedDelList(1);
                        notifyDataSetChanged();
                    }
                });

                holder.player.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        v.startAnimation(pushed_in);

                        if (disPic[position] == 0) {
                            viewToastCustom(R.string.player_playing, font_style);
                            playerPlayMusic(listObjects.arabic_chapter.trim(), true, position);

                            for (int x = 0; x < disPic.length; x++) {
                                if (disPic[x] != 0) {
                                    disPic[x] = 0;
                                }
                            }

                            disPic[position] = 1;
                            notifyDataSetChanged();
                        } else if (disPic[position] == 1) {
                            viewToastCustom(R.string.player_pause, font_style);
                            playerPlayMusic(listObjects.arabic_chapter.trim(), false, position);
                            disPic[position] = 0;
                            notifyDataSetChanged();
                        }
                    }
                });
            }

        }

        return convertView;
    }

    public void updatedDelList(int num) {
        if ((lengther - 1) != 0) {
            Spanned results_bold = Html.fromHtml("Total saved verses: <b>" + (lengther - 1) + "</b>");
            results_textview.setText(results_bold);
            lengther -= 1;
        }
        else if ((lengther - 1) == 0) {
            Spanned results_bold = Html.fromHtml("Total saved verses: <b>" + (lengther - 1) + "</b>");
            results_textview.setText(results_bold);

            final Typeface text_view_font = Typeface.createFromAsset(context.getAssets(), "list_textviews.otf");
            TextView empty_text = (TextView) v.findViewById(R.id.empty_bookmarks);
            empty_text.setTypeface(text_view_font);
            empty_text.setText("Bookmarks empty...");
            empty_text.setVisibility(View.VISIBLE);

            ListView list = (ListView) v.findViewById(R.id.listView_chapters_bookmarks_search);
            list.setVisibility(View.GONE);
            lengther -= 1;
        }
    }

    private void playerPlayMusic(String fileName, boolean bool, int position) {
        final int pos = position;

        if (bool) {
            try {
                ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(getContext().getApplicationContext(),
                        EXPANSION_NUMBER, 0);
                mp = new MediaPlayer();
                AssetFileDescriptor descriptor = expansionFile.getAssetFileDescriptor(fileName + ".mp3");
                mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        disPic[pos] = 0;
                        notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (!bool) {
            if (mp != null) {
                mp.stop();
                mp.release();
                disPic[pos] = 0;
                mp = null;
                notifyDataSetChanged();
            }
        }
    }

    public void turnMediaOFF() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private class ViewHolder {
        ImageButton player, book;
        ImageView arb;
        TextView eng_ver, ver_name;
    }

    private void viewToastCustom(int ID_STRING, Typeface chapters_info_font) {
        LayoutInflater demoToastInflate = getInflater;
        View toastView = demoToastInflate.inflate(R.layout.demo_layout_toast, null);

        TextView toastText = (TextView) toastView.findViewById(R.id.demo_string_replace);
        toastText.setText(ID_STRING);
        toastText.setTextSize(16);
        toastText.setTypeface(chapters_info_font);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }

    private void dbConnectUpdater(boolean determine, int row) {
        SQLHelper_DataBase_Creation_Query_Deletion dbHelp = new SQLHelper_DataBase_Creation_Query_Deletion(context);

        try {
            dbHelp.openDB();

            if (determine) {
                dbHelp.updateBookMarksSave(row);
            }
            else if (!determine) {
                dbHelp.updateBookMarksDelete(row);
            }

            dbHelp.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbHelp.close();
        }
    }

}
