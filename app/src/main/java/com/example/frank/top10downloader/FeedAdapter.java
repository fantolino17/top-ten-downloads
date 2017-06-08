package com.example.frank.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Frank on 12/23/2016.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflator;
    private List<FeedEntry> applications;

    public FeedAdapter(Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflator = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    // @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {//If null, call ViewHolder constructor which calls findviewbyid
            Log.d(TAG, "getView: called with null convertview");
            convertView = layoutInflator.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {//just get convertView tag, bc already called findviewbyid and set convertView tag
            Log.d(TAG, "getView: called with convertView provided");
            viewHolder = (ViewHolder) convertView.getTag();
        }


        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder { //Inner class to hold views so we dont call find view by id everytime
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }


    }


}


























