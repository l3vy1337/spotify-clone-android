package com.example.spotifyapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifyapp.albums.AlbumRVAdapter;
import com.example.spotifyapp.albums.AlbumRVModell;
import com.example.spotifyapp.search.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeAlbumsRecyclerView();
        initializePopularAlbumsRecyclerView();
        initializeTrendingAlbumsRecyclerView();
        initializeSearchView();
    }

    private void initializeSearchView(){
        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchTracks(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void searchTracks(String searchQuery) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("searchQuery", searchQuery);
        startActivity(intent);
    }

    public String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getString("token", "Not Found");
    }

    protected void onStart() {
        super.onStart();
        generateToken();
    }

    private void generateToken() {
        String url = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String tk = jsonObject.getString("access_token");
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("token", "Bearer " + tk);
                    myEdit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String clientId = getString(R.string.CLIENT_ID);
                String clientSecret = getString(R.string.CLIENT_SECRET);
                String credentials = clientId + ":" + clientSecret;

                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(request);
    }

    private void initializeAlbumsRecyclerView() {
        RecyclerView albumsRecyclerView = findViewById(R.id.albumsRecyclerView);

        ArrayList<AlbumRVModell> albumRVModellArrayList = new ArrayList<>();

        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModellArrayList, this);
        albumsRecyclerView.setAdapter(albumRVAdapter);

        String url = "https://api.spotify.com/v1/albums?ids=5lnQLEUiVDkLbFJHXHQu9m,6ivqUxtSPTljd9Frfdyxbg,2gMWwDIxxGIiblnv1pQHyd,0bUTHlWbkSQysoM3VsWldT,0CxPbTRARqKUYighiEY9Sz,4undIeGmofnAYKhnDclN1w,4uG8q3GPuWHQlRbswMIRS6,2R7iJz5uaHjLEVnMkloO18,5GBcqixIFDPEom7AUNbFiM,2cRMVS71c49Pf5SnIlJX3U";

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String token = getToken();

        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albums = response.getJSONArray("albums");
                    for (int i = 0; i < albums.length(); i++) {
                        JSONObject album = albums.getJSONObject(i);

                        String album_type = album.getString("album_type");
                        String artistName = album.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = album.getJSONObject("external_ids").getString("upc");
                        String external_urls = album.getJSONObject("external_urls").getString("spotify");
                        String href = album.getString("href");
                        String id = album.getString("id");
                        String imageUrl = album.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = album.getString("label");
                        String name = album.getString("name");
                        int popularity = album.getInt("popularity");
                        String release_date = album.getString("release_date");
                        int total_tracks = album.getInt("total_tracks");
                        String type = album.getString("type");

                        albumRVModellArrayList.add(new AlbumRVModell(album_type, artistName, external_ids, external_urls, href, id, imageUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data: " + error, Toast.LENGTH_SHORT).show();
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                String errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
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
        queue.add(albumObjReq);
    }

    private void initializePopularAlbumsRecyclerView() {
        RecyclerView albumsRV = findViewById(R.id.popularAlbumsRecyclerView);
        ArrayList<AlbumRVModell> albumRVModellArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModellArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);

        String url = "https://api.spotify.com/v1/albums?ids=3RQQmkQEvNCY4prGKE6oc5,32iAEBstCjauDhyKpGjTuq,78bpIziExqiI9qztvNFlQu,7IKUTIc9UWuVngyGPtqNHS,01sfgrNbnnPUEyz6GZYlt9,7fJJK56U9fHixgO0HQkhtI,0GTrr2N5dR11RqBlewYgBi,6PFPjumGRpZnBzqnDci6qJ,4Gfnly5CzMJQqkUFfoHaP3,2VVvm4zJlUQm9XmBCvGN6z";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        // on below line adding data to array list.
                        albumRVModellArrayList.add(new AlbumRVModell(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
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
        queue.add(albumObjReq);
    }

    private void initializeTrendingAlbumsRecyclerView() {
        RecyclerView albumsRV = findViewById(R.id.trendingAlbumsRecyclerView);
        ArrayList<AlbumRVModell> albumRVModellArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModellArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);

        String url = "https://api.spotify.com/v1/albums?ids=1OteY9OFTmoZ0vmZT89wPd,18NOKLkZETa4sWwLMIm0UZ,3zu0hJJew2qXZNlselIQk8,6DmPNcfpkXBVRJsEIJY9tl,1D1hLipjrdB6pnxurMtC3E,6PbnGueEO6LGodPfvNldYf,1xJHno7SmdVtZAtXbdbDZp,0xojHpNNGFiPqc3TXmh6Gv,3Uo1dpUULgBeWEgjf5AMsd,2ua5bFkZLZl1lIgKWtYZIz";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        albumRVModellArrayList.add(new AlbumRVModell(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(albumObjReq);
    }
}