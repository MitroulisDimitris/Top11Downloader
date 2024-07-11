package com.example.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override//gets callled everytime you scroll and needs a new view to display
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;//create an obj of viewHolder
        if(convertView == null){
            Log.d(TAG, "getView: called with null convereView");
            convertView = layoutInflater.inflate(layoutResource,parent,false);// to reuse the view if available and not create a new one
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            Log.d(TAG, "getView: provided a convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);//find view by id is a costly operation and we do it multiple times
////      TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
////      TextView tvSummary  = (TextView) convertView.findViewById(R.id.tvSummary);



        FeedEntry currentApp = applications.get(position);
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v){
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);

        }
    }

}
