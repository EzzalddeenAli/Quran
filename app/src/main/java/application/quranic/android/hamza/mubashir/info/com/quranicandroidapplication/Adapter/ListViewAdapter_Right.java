package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.RightObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;

public class ListViewAdapter_Right extends ArrayAdapter<RightObjects> {
    Context context;
    LayoutInflater getLayoutInflator;
    Typeface chapters_info_font;
    RightObjects[] data;
    int color_font;

    public ListViewAdapter_Right(Context context, LayoutInflater getLayoutInflater, RightObjects[] items, Typeface typeface, int color_font) {
        super(context, android.R.layout.simple_list_item_single_choice, items);
        this.context = context;
        this.getLayoutInflator = getLayoutInflater;
        this.data = items;
        this.chapters_info_font = typeface;
        this.color_font = color_font;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void add(RightObjects object) {
        super.add(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        super.setNotifyOnChange(notifyOnChange);
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    @Override
    public RightObjects getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(RightObjects item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chaptersdata, parent, false);
        }

        RightObjects rightObjects = super.getItem(position);

        if (rightObjects != null) {
            holder = new ViewHolder();

            holder.text = (TextView) convertView.findViewById(R.id.txtDataChapters);
            if (holder.text != null) {
                holder.text.setText(rightObjects.getChap());
                holder.text.setTypeface(chapters_info_font);
                holder.text.setTextColor(color_font);
            }
        }

        return convertView;
    }

    public class ViewHolder {
        TextView text;
    }
}
