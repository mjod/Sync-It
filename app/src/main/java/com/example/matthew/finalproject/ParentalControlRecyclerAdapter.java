package com.example.matthew.finalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ParentalControlRecyclerAdapter extends RecyclerView.Adapter<ParentalControlRecyclerAdapter.ViewHolder> {
    private ArrayList<FamilyMem> mDataSet;
    private Context mContext;

    public ParentalControlRecyclerAdapter(Context myContext, ArrayList<FamilyMem> myDataSet) {
        mContext = myContext;
        mDataSet = myDataSet;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    public ParentalControlRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_isparent, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        FamilyMem mem = mDataSet.get(position);
        holder.bindMovieData(mem, position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RadioButton yes;
        public RadioButton no;
        public RadioGroup radioGroup;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.radioName);

            radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
            yes = (RadioButton) v.findViewById(R.id.radio_yes);
            no = (RadioButton) v.findViewById(R.id.radio_no);
/*
            yes.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                }
            });
            */

        }

        public void bindMovieData(FamilyMem mem, final int position) {
            name.setText(mem.getName());
            if (mem.getParent() == 1) {
                yes.setChecked(true);
            } else {
                no.setChecked(true);
            }
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.familyMembers.get(position).setParent(1);
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.familyMembers.get(position).setParent(0);
                }
            });


        }
    }
}
