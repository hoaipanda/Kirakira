package com.cameraeffects.kirakira.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.cameraeffects.kirakira.AppController;
import com.cameraeffects.kirakira.MethodUtils;
import com.cameraeffects.kirakira.R;
import com.cameraeffects.kirakira.data.FilterItem;

import java.util.ArrayList;


public class FilterItemAdapter extends RecyclerView.Adapter<FilterItemAdapter.ViewHolder> {

    private Context analog3_context;
    private onListenerFilterItem analog3_listener;
    private ArrayList<FilterItem> analog3_listAnalogjapan1FilterItem;
    private int selected;

    public FilterItemAdapter(Context context, onListenerFilterItem listener, ArrayList<FilterItem> arrayList) {
        this.analog3_context = context;
        this.analog3_listener = listener;
        this.analog3_listAnalogjapan1FilterItem = arrayList;
        this.selected = 0;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(analog3_context).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FilterItem analogjapan1FilterItem = analog3_listAnalogjapan1FilterItem.get(position);
        Bitmap bitmap = analogjapan1FilterItem.getBitmap();
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(analog3_context.getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(MethodUtils.analog3_dpToPx(analog3_context, 2));
        holder.analog3_imgBitmap.setImageDrawable(roundedBitmapDrawable);
        holder.analog3_tvName.setText(analogjapan1FilterItem.getName());
        if (position == selected) {
            holder.imgSelect.setVisibility(View.VISIBLE);
            holder.analog3_tvName.setTextColor(ContextCompat.getColor(AppController.getContext(),R.color.colorPrimaryDark));
        } else {
            holder.imgSelect.setVisibility(View.GONE);
            holder.analog3_tvName.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != position) {
                    selected = position;
                    analog3_listener.onClickItem(position, analogjapan1FilterItem);
                    notifyDataSetChanged();
                } else {
                    if (position != 0) {
                        analog3_listener.onClick2Item(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return analog3_listAnalogjapan1FilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView analog3_imgBitmap;
        TextView analog3_tvName;
        ImageView imgSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            analog3_imgBitmap = (ImageView) itemView.findViewById(R.id.analog3_imgBitMap);
            analog3_tvName = (TextView) itemView.findViewById(R.id.analog3_tvName);
            imgSelect = (ImageView) itemView.findViewById(R.id.imgSelected);
        }
    }

    public interface onListenerFilterItem {
        void onClickItem(int position, FilterItem filterItem);

        void onClick2Item(int position);
    }
}
