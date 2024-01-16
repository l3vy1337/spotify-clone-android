package com.example.spotifyapp.player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifyapp.R;
import com.example.spotifyapp.tracks.TrackRVModell;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity {

    ImageView albumImageView;
    TextView songTextView;
    TextView artistTextView;
    SeekBar playerSeekBar;
    TextView currentTimeTextView;
    TextView totalTimeTextView;
    ImageButton playButton;
    ImageButton nextButton;
    ImageButton previousButton;
    ImageButton shuffleButton;
    ImageButton repeatButton;
    ExoPlayer player;
    ArrayList<TrackRVModell> tracks;
    int currentTrackIndex = 0;
    boolean isUserSeeking = false;
    int userSelectedPosition = 0;
    ArrayList<TrackRVModell> originalTracks;
    boolean isShuffleModeEnabled = false;
    boolean isRepeatModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        albumImageView = findViewById(R.id.albumImageView);
        songTextView = findViewById(R.id.songTextView);
        artistTextView = findViewById(R.id.artistTextView);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);
        player = new ExoPlayer.Builder(this).build();

        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        repeatButton = findViewById(R.id.repeatButton);

        nextButton.setOnClickListener(view -> skipToNextTrack());
        previousButton.setOnClickListener(view -> skipToPreviousTrack());

        String previewUrl = getIntent().getStringExtra("preview_url");
        String songTitle = getIntent().getStringExtra("name");
        String artistName = getIntent().getStringExtra("artist");
        String trackImgUrl = getIntent().getStringExtra("url");
        tracks = (ArrayList<TrackRVModell>) getIntent().getSerializableExtra("tracks");
        currentTrackIndex = getIntent().getIntExtra("currentTrackIndex", 0);

        playTrack(previewUrl, songTitle, artistName, trackImgUrl);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffleModeEnabled) {
                    disableShuffle();
                } else {
                    enableShuffle();
                }


            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeatModeEnabled) {
                    isRepeatModeEnabled = false;
                    repeatButton.setImageResource(R.drawable.ic_repeat);

                } else {
                    isRepeatModeEnabled = true;
                    repeatButton.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player != null && fromUser) {
                    userSelectedPosition = progress;
                    currentTimeTextView.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;

                if (player != null) {
                    player.seekTo(userSelectedPosition);
                }

                playerSeekBar.setProgress(userSelectedPosition);
                currentTimeTextView.setText(formatTime(userSelectedPosition));

            }

        });
    }

    private void playTrack(String previewUrl, String songTitle, String artistName, String trackImgUrl) {
        songTextView.setText(songTitle);
        artistTextView.setText(artistName);
        Picasso.get().load(trackImgUrl).into(albumImageView);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(previewUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    playButton.setImageResource(R.drawable.ic_pause); // Your pause icon
                } else {
                    playButton.setImageResource(R.drawable.ic_play2); // Your play icon
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    player.seekTo(0);
                    player.pause();
                }
                if (state == Player.STATE_READY) {
                    playerSeekBar.setMax((int) player.getDuration());
                }
            }
        });
        updateSeekBar();
        handlePlayPause();
    }

    /*private void setupPlayer(String previewUrl) {

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(previewUrl));

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    playButton.setImageResource(R.drawable.ic_pause); // Your pause icon
                } else {
                    playButton.setImageResource(R.drawable.ic_play2); // Your play icon
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    player.seekTo(0);
                    player.pause();
                }
                if (state == Player.STATE_READY) {
                    playerSeekBar.setMax((int) player.getDuration());
                }
            }


        });
    }*/

    private void handlePlayPause() {
        playButton.setOnClickListener(view -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });
    }
    private void updateSeekBar() {
        final Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null && !isUserSeeking) {
                    int currentPosition = (int) player.getCurrentPosition();
                    int totalDuration = (int) player.getDuration();
                    playerSeekBar.setProgress(currentPosition);
                    currentTimeTextView.setText(formatTime(currentPosition));
                    totalTimeTextView.setText(formatTime(totalDuration));

                }
                handler.postDelayed(this, 10);
            }
        });
    }

    private void skipToNextTrack() {
        if (currentTrackIndex < tracks.size() - 1) {
            currentTrackIndex++;
            TrackRVModell nextTrack = tracks.get(currentTrackIndex);
            playTrack(nextTrack.getTrackUrl(), nextTrack.getName(), nextTrack.getTrackArtist(), nextTrack.getTrackImgUrl());
        } else if (isRepeatModeEnabled) {
            currentTrackIndex = 0;
            TrackRVModell nextTrack = tracks.get(currentTrackIndex);
            playTrack(nextTrack.getTrackUrl(), nextTrack.getName(), nextTrack.getTrackArtist(), nextTrack.getTrackImgUrl());
        }
    }



    private void skipToPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--;
            TrackRVModell previousTrack = tracks.get(currentTrackIndex);
            playTrack(previousTrack.getTrackUrl(), previousTrack.getName(), previousTrack.getTrackArtist(), previousTrack.getTrackImgUrl());
        } else if (isRepeatModeEnabled) {
            currentTrackIndex = tracks.size() - 1;
            TrackRVModell nextTrack = tracks.get(currentTrackIndex);
            playTrack(nextTrack.getTrackUrl(), nextTrack.getName(), nextTrack.getTrackArtist(), nextTrack.getTrackImgUrl());
        }
    }
    private void enableShuffle() {
        if (!isShuffleModeEnabled) {
            originalTracks = new ArrayList<>(tracks);
            Collections.shuffle(tracks);
            isShuffleModeEnabled = true;
            shuffleButton.setImageResource(R.drawable.ic_shuffle_on);
        }
    }
    private void disableShuffle() {
        if (isShuffleModeEnabled) {
            tracks = new ArrayList<>(originalTracks);
            isShuffleModeEnabled = false;
            shuffleButton.setImageResource(R.drawable.ic_shuffle);
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }
}