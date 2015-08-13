package com.example.matthew.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 4/10/2015.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public ArrayList<Event> mDataSet;
    private Context mContext;
    OnItemClickListener mItemClickListener;

    public MyRecyclerViewAdapter(Context myContext, ArrayList myDataSet) {
        mContext = myContext;
        if (myDataSet != null)
            setItems(myDataSet);
    }

    public ArrayList<Event> getDataSet() {
        return mDataSet;
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null) {
            return 0;
        } else {
            return mDataSet.size();
        }
    }

    public void setItems(ArrayList<Event> arrayList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        dateFormat.format(currentDate);
        MainActivity.MyQuickSort sorter = new MainActivity.MyQuickSort();
        sorter.sort(arrayList);
        ArrayList<Event> futureEvents = new ArrayList<>();
        TimeIgnoringComparator timeIgnoringComparator = new TimeIgnoringComparator();
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                Date date = dateFormat.parse(arrayList.get(i).getStartDate());
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(date);
                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.setTime(currentDate);

                //Log.d("comparingEvent", arrayList.get(i).getName() + ":" + date.toString() +"vs" + currentDate.toString() +"=" + timeIgnoringComparator.compare(eventCalendar,todayCalendar));
                if (timeIgnoringComparator.compare(eventCalendar, todayCalendar) >= 0 && arrayList.get(i).getLocation().length() > 0) {
                    futureEvents.add(arrayList.get(i));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mDataSet = futureEvents;

    }

    public class TimeIgnoringComparator implements Comparator<Calendar> {
        public int compare(Calendar c1, Calendar c2) {
            if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
                return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
            if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
                return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
            return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
        }
    }

    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_event, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = new Event(mDataSet.get(position));
        holder.bindMovieData(event.getId(), event.getName(), event.getStartDate(), event.getEndDate(), event.getDescription(), event.getGuestsToString(), event.getLocation());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView vTitle;
        public TextView vDescription;
        public TextView vLocation;
        public TextView vGuest;


        public ViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.eventTitle);
            vDescription = (TextView) v.findViewById(R.id.eventDescription);
            vLocation = (TextView) v.findViewById(R.id.eventLocation);
            vGuest = (TextView) v.findViewById(R.id.eventGuest);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemLongClick(v, getPosition());
                    }
                    return true;
                }
            });


        }

        public void bindMovieData(int id, String title, String start, String end, String description, String guests, String location) {

            vTitle.setText("  " + start + " - " + title);
            vDescription.setText(description);
            vLocation.setText(location);
            vGuest.setText(guests);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public static class MyDownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public MyDownloadImageAsyncTask(ImageView imv) {
            imageViewReference = new WeakReference<ImageView>(imv);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            for (String url : urls) {
                bitmap = MyUtility.downloadImage(url);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

    }
}
