/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bff.javampd.MPD;
import org.bff.javampd.MPDDatabase;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.exception.*;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 */
public class MPDWrapper {
    private static volatile MPDWrapper instance = null;
    MPD mpd;
    private MPDStatus mpd_status;
    private Thread updater;

    public static MPDWrapper getInstance() {
        if (instance == null) {
            synchronized (MPDWrapper.class){
                if (instance == null)
                    instance = new MPDWrapper();
            }
        }
        return instance;
    }
    
    public MPDWrapper() {
        try {
            Logger.getLogger(MPDWrapper.class.getName()).log(Level.INFO, "Creating new MPD connection...");
            mpd = new MPD("127.0.0.1", 6600);
            mpd_status = getRawStatus();
            updater = new Thread(getUpdater());
        } catch (UnknownHostException | MPDConnectionException ex) {
            Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MPD getMPD() {
        return mpd;
    }
    
    public void close() {
        try {
            mpd.close();
            instance = null;
        } catch (MPDConnectionException | MPDResponseException ex) {
            Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Object execute(final MPDAction action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Object> future = executor.submit(action);
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch(TimeoutException ex) {
            try {
                Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, ex);
                java.lang.Runtime.getRuntime().exec("killall mpd");
                java.lang.Runtime.getRuntime().exec("mpd");
                mpd = new MPD("127.0.0.1", 6600);
            } catch (IOException | MPDConnectionException rex) {
                Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, rex);
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private Runnable getUpdater() {
        return new Runnable() {
            @Override
            public void run() {
                while(true) {
                    mpd_status = getRawStatus();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MPDWrapper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }
    
    private MPDStatus getRawStatus() {
        try {
            MPDStatus status = new MPDStatus();
            MPDPlaylist pls = mpd.getMPDPlaylist();

            status.setPlaylist((ArrayList<MPDSong>) pls.getSongList());
            status.setNowPlaying(pls.getCurrentSong());

            
            status.setStatus("Unknown");
            for(String mpd_resp_status: mpd.getStatus()) {
                switch ((String) mpd_resp_status) {
                    case "state: play":
                        status.setStatus("Playing");
                        break;
                    case "state: pause":
                        status.setStatus("Paused");
                        break;
                    case "state: stop":
                        status.setStatus("Stopped");
                        break;
                }
            }

            String volume = Integer.toString(mpd.getMPDPlayer().getVolume());
            if (volume.equals("0") || volume.equals("-1")) status.setVolume("mute");
            else status.setVolume(volume);

            if(status.getNowPlaying() != null) {
                status.setTrackLength(status.getNowPlaying().getLength());
                status.setElapsedTime(mpd.getMPDPlayer().getElapsedTime());
            }

            return status;
        }
        catch(Exception _) {
            return null;
        }
    }
    
    public MPDStatus getStatus() {
        if(!updater.isAlive())
            updater.start();
        return mpd_status;
    }
    
    public String getNowPlayingString() {
        return (String) execute(
            new MPDAction() {
                @Override
                public String call() throws MPDConnectionException, MPDPlaylistException {
                    MPDSong track = mpd.getMPDPlaylist().getCurrentSong();
                    if (track != null) {
                        return track.getTitle() + " - " + track.getArtist();
                    }
                    else {
                        return "Not Playing";
                    }
                }
            }
        );
    }

    public MPDSong getNowPlaying() {
        return (MPDSong) execute(
            new MPDAction() {
                @Override
                public MPDSong call() throws MPDConnectionException, MPDPlaylistException {
                    MPDSong song = mpd.getMPDPlaylist().getCurrentSong();
                    return song;
                }
            }
        );
    }

    public long getElapsedTime() {
        return (long) execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    return mpd.getMPDPlayer().getElapsedTime();
                }
            }
        );
    }
    
    public void update() {
        execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDAdminException {
                    mpd.getMPDAdmin().updateDatabase();
                    return new Object();
                }
            }
        );
    }

    public void seek(final int time) {
        execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().seek(time);
                    return new Object();
                }
            }
        );
    }
    
    public Collection<MPDSong> setPlaylist(final Collection<MPDSong> list) {
        return (Collection<MPDSong>) execute(
            new MPDAction() {
                @Override
                public Collection<MPDSong> call() throws MPDConnectionException, MPDPlaylistException {
                    mpd.getMPDPlaylist().clearPlaylist();
                    mpd.getMPDPlaylist().addSongs((List)list);
                    return list;
                }
            }
        );
    }
    
    public List<MPDSong> getPlaylist() {
        return (List<MPDSong>) execute(
            new MPDAction() {
                @Override
                public List<MPDSong> call() throws MPDConnectionException, MPDPlaylistException {
                    return mpd.getMPDPlaylist().getSongList();
                }
            }
        );
    }

    public void savePlaylist(final String title) {
        execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException {
                    mpd.getMPDPlaylist().savePlaylist(title);
                    return new Object();
                }
            }
        );
    }

    public void loadPlaylist(final String title) {
        execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException {
                    mpd.getMPDPlaylist().clearPlaylist();
                    mpd.getMPDPlaylist().loadPlaylist(title);
                    return new Object();
                }
            }
        );
    }

    public ArrayList<String> listPlaylists() {
        return (ArrayList<String>) execute(
            new MPDAction() {
                @Override
                public Collection<String> call() throws MPDConnectionException, MPDPlaylistException, MPDDatabaseException {
                    return mpd.getMPDDatabase().listPlaylists();
                }
            }
        );
    }
    
    public void deletePlaylist(final String title) {
        execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException {
                    mpd.getMPDPlaylist().deletePlaylist(title);
                    return new Object();
                }
            }
        );
    }
    
    public String getVolume() {
        return (String) execute(
            new MPDAction() {
                @Override
                public String call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    String volume = Integer.toString(mpd.getMPDPlayer().getVolume());
                    if (volume.equals("0") || volume.equals("-1"))
                        return "mute";
                    else
                        return volume;
                }
            }
        );
    }
    
    public Collection<MPDSong> search(final String search, final String text) {
        return (Collection<MPDSong>) execute(
            new MPDAction() {
                @Override
                public Collection<MPDSong> call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException, MPDDatabaseException {
                    MPDDatabase mpddb = mpd.getMPDDatabase();
                    Collection<MPDSong> list;
                    switch (search) {
                        case "artist":
                            list = mpddb.searchArtist(text);
                            break;
                        case "album":
                            list = mpddb.searchAlbum(text);
                            break;
                        case "track":
                            list = mpddb.searchTitle(text);
                            break;
                        case "filename":
                            list = mpddb.searchFileName(text);
                            break;
                        default:
                            list = mpddb.findAny(text);
                            break;
                    }
                    return list;
                }
            }
        );
    }

    public void addTracks(final List<MPDSong> list) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException {
                    mpd.getMPDPlaylist().addSongs(list);
                    return new Object();
                }
            }
        );
    }

    public void removeTracks(final List<MPDSong> list) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException {
                    List<MPDSong> current = mpd.getMPDPlaylist().getSongList();
                    for(MPDSong track: list)
                        if(current.contains(track)) {
                            mpd.getMPDPlaylist().removeSong(track);
                            current.remove(track);
                        }
                    return new Object();
                }
            }
        );
    }

    public void togglePlay() {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    if (getStatus().getStatus().equals("Playing"))
                        mpd.getMPDPlayer().pause();
                    else
                        mpd.getMPDPlayer().play();
                    return new Object();
                }
            }
        );
    }

    public void setPlaying(final Boolean status) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    if(status)
                        mpd.getMPDPlayer().play();
                    else
                        mpd.getMPDPlayer().stop();
                    return new Object();
                }
            }
        );
    }

    public Boolean getPlaying() {
         return (Boolean) execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlayerException, MPDResponseException {
                    Collection<String> status = mpd.getStatus();
                    
                    for(String x: status) {
                        if(x.equals("state: play")) {
                            return true;
                        }
                    }
                    
                    return false;
                }
            }
        );
    }
    
    public void stop() {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    if (getStatus().equals("Stopped"))
                        mpd.getMPDPlayer().stop(); // BROKEN
                    else
                        mpd.getMPDPlayer().play();
                    return new Object();
                }
            }
        );
    }
    
    public void next() {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().playNext();
                    return new Object();
                }
            }
        );
    }
    
    public void previous() {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().playPrev();
                    return new Object();
                }
            }
        );
    }
    
    public void delete() {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlaylist().removeSong(mpd.getMPDPlayer().getCurrentSong());
                    return new Object();
                }
            }
        );
    }
    
    public void setPlaylistPos(final int track) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().seekId(getPlaylist().get(track - 1), 0);
                    return new Object();
                }
            }
        );
    }
    
    public int getPlaylistPos() {
         return (int) execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    List<MPDSong> tracks = mpd.getMPDPlaylist().getSongList();
                    MPDSong track = mpd.getMPDPlaylist().getCurrentSong();
                    int i = 0;
                    for(MPDSong x: tracks) {
                        i++;
                        if(x.equals(track))
                            return i;
                    }
                    
                    return -1;
                }
            }
        );
    }
    
    public void setVolume(final int volume) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().setVolume(volume);
                    return new Object();
                }
            }
        );
    }
    
    public void setRepeat(final Boolean mode) {
         execute(
            new MPDAction() {
                @Override
                public Object call() throws MPDConnectionException, MPDPlaylistException, MPDPlayerException {
                    mpd.getMPDPlayer().setRepeat(mode);
                    return new Object();
                }
            }
        );
    }
    
    public class MPDAction implements Callable<Object> {
        @Override
        public Object call() throws 
                InterruptedException,
                MPDConnectionException,
                MPDPlayerException,
                MPDAdminException,
                MPDPlaylistException,
                MPDResponseException {
            return null;
        }
    }
}
