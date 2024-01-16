package com.example.spotifyapp.tracks;

import java.io.Serializable;

public class TrackRVModell implements Serializable {
        private String trackName;
        private String trackArtist;
        private String trackAlbum;
        private String id;
        private String previewUrl;
        private String trackImgUrl;

        public String getName() {
            return trackName;
        }

        public void setName(String trackName) {
            this.trackName = trackName;
        }

        public String getTrackArtist() {
            return trackArtist;
        }

        public void setTrackArtist(String trackArtist) {
            this.trackArtist = trackArtist;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTrackUrl() {
            return previewUrl;
        }

        public void setTrackUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }

        public String getTrackImgUrl() {
            return trackImgUrl;
        }

        public void setTrackImgUrl(String trackImgUrl) {
            this.trackImgUrl = trackImgUrl;
        }

        public TrackRVModell(String trackName, String trackArtist, String id, String previewUrl, String trackImgUrl) {
            this.trackName = trackName;
            this.trackArtist = trackArtist;
            this.id = id;
            this.previewUrl = previewUrl;
            this.trackImgUrl = trackImgUrl;
        }
}
