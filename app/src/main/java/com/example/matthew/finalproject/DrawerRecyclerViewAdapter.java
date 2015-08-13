package com.example.matthew.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 2/8/2015.
 */
public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ViewHolder> {
    private List<Map<String, ?>> mDataSet;
    private Context mContext;
    private Integer viewType;
    OnItemClickListener mItemClickListener;

    public DrawerRecyclerViewAdapter(Context myContext, List<Map<String, ?>> myDataSet) {
        mContext = myContext;
        mDataSet = myDataSet;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, ?> data = mDataSet.get(position);
        viewType = (Integer) data.get("viewtype");
        return viewType;
    }

    public DrawerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.draw_cardview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, ?> movie = mDataSet.get(position);
        holder.bindMovieData(movie);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vTitle;
        public RelativeLayout vBackground;

        public ViewHolder(View v) {
            super(v);
            if (viewType == 0) {
                vIcon = (ImageView) v.findViewById(R.id.cardview_image);
                vTitle = (TextView) v.findViewById(R.id.cardview_text);
                vBackground = (RelativeLayout) v.findViewById(R.id.draw_background);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(v, getPosition());
                        }
                    }
                });
            }

        }

        public void bindMovieData(Map<String, ?> entry) {
            if (viewType == 0) {
                if ((Integer) entry.get("image") != null)
                    vIcon.setImageResource((Integer) entry.get("image"));
                vTitle.setText((String) entry.get("name"));
                if (Boolean.valueOf((Boolean) entry.get("selection"))) {
                    vBackground.setBackgroundResource(R.drawable.buttonshapeselected);
                } else {
                    vBackground.setBackgroundResource(R.drawable.buttonshape);
                }
            }

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
