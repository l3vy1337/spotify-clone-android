package com.example.spotifyapp.albums;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class AlbumRVAdapter extends RecyclerView.Adapter<AlbumRVAdapter.ViewHolder> {
    private ArrayList<AlbumRVModell> albumRVModellArrayList;
    private Context context;

    public AlbumRVAdapter(ArrayList<AlbumRVModell> albumRVModellArrayList, Context context) {
        this.albumRVModellArrayList = albumRVModellArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumRVAdapter.ViewHolder holder, int position) {
        AlbumRVModell albumRVModell = albumRVModellArrayList.get(position);
        Picasso.get().load(albumRVModell.imageUrl).into(holder.albumIV);
        holder.albumNameTV.setText(albumRVModell.name);
        holder.albumDetailTV.setText(albumRVModell.artistName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(context, AlbumDetailActivity.class);
                i.putExtra("id", albumRVModell.id);
                i.putExtra("name", albumRVModell.name);
                i.putExtra("img", albumRVModell.imageUrl);
                i.putExtra("artist", albumRVModell.artistName);
                i.putExtra("albumUrl", albumRVModell.external_urls);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumRVModellArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumIV;
        private TextView albumNameTV, albumDetailTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumIV = itemView.findViewById(R.id.idIVAlbum);
            albumNameTV = itemView.findViewById(R.id.idTVAlbumName);
            albumDetailTV = itemView.findViewById(R.id.idTVAlbumDetails);
        }
    }
}
