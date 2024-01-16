package com.example.spotifyapp.tracks;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyapp.R;
import com.example.spotifyapp.player.PlayerActivity;

import java.util.ArrayList;

public class TracksRVAdapter extends RecyclerView.Adapter<TracksRVAdapter.ViewHolder> {
    private ArrayList<TrackRVModell> trackRVModells;
    private Context context;


    public TracksRVAdapter(ArrayList<TrackRVModell> trackRVModells, Context context) {
        this.trackRVModells = trackRVModells;
        this.context = context;
    }

    @NonNull
    @Override
    public TracksRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TracksRVAdapter.ViewHolder holder, int position) {
        TrackRVModell trackRVModell = trackRVModells.get(position);
        holder.trackNameTextView.setText(trackRVModell.getName());
        holder.trackArtistTextView.setText(trackRVModell.getTrackArtist());
    }


    @Override
    public int getItemCount() {
        return trackRVModells.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView trackNameTextView, trackArtistTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameTextView = itemView.findViewById(R.id.trackNameTextView);
            trackArtistTextView = itemView.findViewById(R.id.trackArtistTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrackRVModell currentTrack = trackRVModells.get(getAdapterPosition());

                    if(currentTrack.getTrackUrl() != null) {
                        Intent playerIntent = new Intent(context, PlayerActivity.class);
                        playerIntent.putExtra("preview_url", currentTrack.getTrackUrl());
                        playerIntent.putExtra("name", currentTrack.getName());
                        playerIntent.putExtra("artist", currentTrack.getTrackArtist());
                        playerIntent.putExtra("url", currentTrack.getTrackImgUrl());
                        playerIntent.putExtra("tracks", trackRVModells);
                        playerIntent.putExtra("currentTrackIndex", getAdapterPosition());
                        context.startActivity(playerIntent);

                    } else {
                        Toast.makeText(context, "This track is unavailable for playback", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

