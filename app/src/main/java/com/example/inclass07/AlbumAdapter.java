package com.example.inclass07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AlbumAdapter extends ArrayAdapter<Album> {
    public AlbumAdapter(@NonNull Context context, int resource, @NonNull List<Album> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Album track = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.textViewAlbumName = convertView.findViewById(R.id.textViewAlbumName);
            viewHolder.textViewArtistName = convertView.findViewById(R.id.textViewArtistName);
            viewHolder.textViewDate = convertView.findViewById(R.id.textViewDate);
            viewHolder.textViewtrackname = convertView.findViewById(R.id.textViewtrackname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewAlbumName.setText(track.getAlbum_name());
        viewHolder.textViewArtistName.setText(track.getArtist_name());
        viewHolder.textViewDate.setText(track.getUpdated_time());
        viewHolder.textViewtrackname.setText(track.getTrack_name());
        return convertView;
    }

    private static class ViewHolder {
        TextView textViewDate;
        TextView textViewArtistName;
        TextView textViewAlbumName;
        TextView textViewtrackname;


    }
}
