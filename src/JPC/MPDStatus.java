/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.util.ArrayList;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 */
public class MPDStatus {
    private ArrayList<MPDSong> playlist;
    private MPDSong current_track;
    private String status;
    private String volume;
    private long elapsedTime;
    private int track_length;

    public MPDStatus() {
        
    }
    
    public MPDStatus(long elapsedTime, String status, String volume, int track_length, ArrayList<MPDSong> playlist, MPDSong current_track) {
        this.elapsedTime = elapsedTime;
        this.status = status;
        this.volume = volume;
        this.track_length = track_length;
        this.playlist = playlist;
        this.current_track = current_track;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getTrackLength() {
        return track_length;
    }

    public void setTrackLength(int track_length) {
        this.track_length = track_length;
    }

    public ArrayList<MPDSong> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<MPDSong> playlist) {
        this.playlist = playlist;
    }

    public MPDSong getNowPlaying() {
        return current_track;
    }

    public void setNowPlaying(MPDSong current_track) {
        this.current_track = current_track;
    }
    
    public String getNowPlayingString() {
        if (current_track != null)
            return current_track.getTitle() + " - " + current_track.getArtist();
        return "Not Playing";
    }
}
