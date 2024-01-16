package com.example.spotifyapp.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifyapp.R;
import com.example.spotifyapp.tracks.TrackRVModell;
import com.example.spotifyapp.tracks.TracksRVAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    String searchQuery = "";
    private EditText searchEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchQuery = getIntent().getStringExtra("searchQuery");
        searchEditText.setText(searchQuery);

        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getTracks(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        getTracks(searchQuery);
    }

    private String getToken() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "Not Found");
    }

    private void getTracks(String searchQuery) {
        RecyclerView songsRecyclerView = findViewById(R.id.songsRecyclerView);
        ArrayList<TrackRVModell> trackRVModells = new ArrayList<>();
        TracksRVAdapter tracksRVAdapter = new TracksRVAdapter(trackRVModells, this);
        songsRecyclerView.setAdapter(tracksRVAdapter);

        String url = "https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track";
        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject tracks = response.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String trackName = item.getString("name");
                        String trackArtist = item.getJSONArray("artists").getJSONObject(0).getString("name");
                        String id = item.getString("id");
                        String trackImgUrl = item.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                        if (!item.isNull("preview_url")) {
                            String previewUrl = item.getString("preview_url");
                            trackRVModells.add(new TrackRVModell(trackName, trackArtist, id, previewUrl, trackImgUrl));
                        } else {
                            trackRVModells.add(new TrackRVModell(trackName, trackArtist, id, null, trackImgUrl));
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
                Toast.makeText(SearchActivity.this, "Fail to get data: " + error, Toast.LENGTH_SHORT).show();
            }

    }) {
        @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}