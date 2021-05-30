package com.nitin.rssfeeddownloaderapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapterForListView extends ArrayAdapter<FeedEntry> {
    public static final String TAG = "CustomAdapterForListView";
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
        //convertView is a view which is given to customAdapter which can be reused if it is not null
        ViewHolder viewHolder;
        View view;
        if(convertView==null){
            view  = layoutInflater.inflate(resourceID,parent,false);    // inflate a new  view of custom layout
            viewHolder = new ViewHolder(view);  // make a new ViewHolder tag for our newly created view
            view.setTag(viewHolder);    // set tag for our view
        }else {
            view = convertView; //reuse view
            viewHolder = (ViewHolder) view.getTag();
        }
        // update our 3 widgets in our view
        viewHolder.updateView(appInfoList.get(position));
        // return the updated view
        Log.d(TAG, "getView: custom adapter get called for view"+view.getTag());
        return view;
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final TextView tvReleaseDate;

        public ViewHolder(View view) {
            // find the 3 widgets in our customAdapter
            this.tvName = view.findViewById(R.id.tvName);
            this.tvArtist = view.findViewById(R.id.tvArtist);
            this.tvSummary = view.findViewById(R.id.tvSummary);
            this.tvReleaseDate  = view.findViewById(R.id.tvReleaseDate);
        }
        public void updateView(FeedEntry currentFeed ){
            this.tvArtist.setText(currentFeed.getArtist());
            this.tvSummary.setText(currentFeed.getSummery());
            this.tvName.setText(currentFeed.getName());
            this.tvReleaseDate.setText("Release date: "+currentFeed.getReleaseDate());
        }
    }

}
