package com.example.lzd.music;

/**
 * Created by LZD on 2016/10/6.
 */
public class MusicData {
    private String name;
    private String artist;
    private String album;
    private long duration;
    private String id;
    private String path;
    private String display_name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() { return artist; }

    public void setAlbum(String album) { this.album = album; }

    public String getAlbum() { return album; }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() { return duration; }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() { return path; }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_name() { return  display_name; }
}
