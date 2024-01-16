package com.example.spotifyapp.albums;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifyapp.R;
import com.example.spotifyapp.tracks.TrackRVModell;
import com.example.spotifyapp.tracks.TracksRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlbumDetailActivity extends AppCompatActivity {

    String albumId;
    String albumImgUrl, albumName, artist, albumUrl;

    private TextView albumNameTextView, albumArtistTextView;
    private ImageView albumImageView;
    private FloatingActionButton playFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        albumId = getIntent().getStringExtra("id");
        albumImageView = findViewById(R.id.albumDetailImageView);
        albumImgUrl = getIntent().getStringExtra("img");
        albumName = getIntent().getStringExtra("name");
        artist = getIntent().getStringExtra("artist");
        albumUrl = getIntent().getStringExtra("albumUrl");
        Log.e("TAG", "album id is : " + albumId);
        albumNameTextView = findViewById(R.id.albumNameTextView);

        albumArtistTextView = findViewById(R.id.artistNameTextView);
        albumNameTextView.setText(albumName);
        albumArtistTextView.setText(artist);


        Picasso.get().load(albumImgUrl).into(albumImageView);
        getAlbumTracks(albumId);

    }

    private String getToken() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "Not Found");
    }

    private void getAlbumTracks(String albumId) {
        String url = "https://api.spotify.com/v1/albums/" + albumId + "/tracks?market=HU";
        ArrayList<TrackRVModell> trackRVModells = new ArrayList<>();
        TracksRVAdapter tracksRVAdapter = new TracksRVAdapter(trackRVModells, this);
        RecyclerView trackRecyclerView = findViewById(R.id.albumDetailRecyclerView);

        trackRecyclerView.setAdapter(tracksRVAdapter);
        RequestQueue queue = Volley.newRequestQueue(AlbumDetailActivity.this);
        JsonObjectRequest trackObj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray itemsArray = response.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemObj = itemsArray.getJSONObject(i);
                        String trackName = itemObj.getString("name");
                        String id = itemObj.getString("id");
                        String trackArtist = itemObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        if (!itemObj.isNull("preview_url")) {
                            String previewUrl = itemObj.getString("preview_url");
                            trackRVModells.add(new TrackRVModell(trackName, trackArtist, id, previewUrl, albumImgUrl));
                        } else {
                            trackRVModells.add(new TrackRVModell(trackName, trackArtist, id, null, albumImgUrl));
                        }

                    }
                    tracksRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AlbumDetailActivity.this, "Fail to get Tracks" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(trackObj);
    }
}