package com.nitin.rssfeeddownloaderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapterForListView extends ArrayAdapter<FeedEntry> {
    private static final String TAG = "CustomAdapterForListView";
    private final int resourceID;       // This is same as R.id for the item_info.xml as we will be needing it here.
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> appInfoList;

    public CustomAdapterForListView(Context context, int resource, List<FeedEntry> appInfoList) {
        super(context, resource);
        resourceID = resource;
        // we can get LayoutInflater from a Context.
        layoutInflater = LayoutInflater.from(context);
        this.appInfoList = appInfoList;
    }

    @Override   // total # of entries the adapter can work with
    public int getCount() {
        return appInfoList.size();
    }

    @NonNull
    @Override   // this method is called by our ListView every time it requires to present a new View on screen
    // position : tells us the index of Entry the ListView needs
    // parent : it refers to item_info.xml so that we can access our custom adapter layout
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // the LayoutInflater takes the layout specified to it and convert it to a single View object
        // inflate takes the xml_id(the id of item_info.xml) the id of custom adapter
        // then it takes parent(the item_info.xml) from which it will take  the layout of our custom adapter
        View view = layoutInflater.inflate(resourceID,parent,false);
        // find the 3 widgets in our customAdapter
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvArtist = view.findViewById(R.id.tvArtist);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        // update our 3 widgets in our view
        FeedEntry currentFeed = appInfoList.get(position);
        tvArtist.setText(currentFeed.getArtist());
        tvSummary.setText(currentFeed.getSummery());
        tvName.setText(currentFeed.getName());
        // return the updated view
        return view;
    }
}
