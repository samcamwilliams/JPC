/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;
import java.util.Collection;
import JPC.MPDWrapper;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 * 
 * Need to implement pause play and change tests to perform a predictable set of actions!
 */
public class BIFMPD {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        bifs.add(new Variable(new BIFMPD.BIFMPDCmd()));
        
        return bifs;
    }
    
    public static class BIFMPDCmd extends BuiltIn {

        @Override
        public String name() {
            return "mpd";
        }

        @Override

        public LObj execute(Environment env) throws MISPException {
            LObj ret = new LObj("(error unrecognised)");
            
            MPDWrapper wrapper = MPDWrapper.getInstance();
            String function = env.state.get(0).getValue().getObj().toString();
            
            if(function.equals("volume")) {
                if(isSet(env)) {
                    wrapper.setVolume(Integer.parseInt(env.state.get(1).getValue().toString()));
                    ret = new LObj("ok");
                }
                else {
                    ret = new LObj(wrapper.getVolume());
                }
            }
            
            if(function.equals("playing")) {
                if(isSet(env)) {
                    String arg = env.state.get(1).getValue().toString();
                    if(arg.equals("true")) {
                        wrapper.setPlaying(true);
                    }
                    else {
                        wrapper.setPlaying(false);
                    }
                    
                    ret = new LObj("ok");
                }
                else {
                    ret = new LObj(wrapper.getPlaying().toString());
                }
            }
            
            if(function.equals("pause")) {
                wrapper.togglePlay();
                ret = new LObj("ok");
            }
            
            if(function.equals("track-pos")) {
                if(isSet(env)) {
                    wrapper.seek(Integer.parseInt(env.state.get(1).getValue().toString()));
                    ret = new LObj("ok");
                }
                else {
                    ret = new LObj(Long.toString(wrapper.getElapsedTime()));
                }
            }
            
            if(function.equals("playlist-pos")) {
                if(isSet(env)) {
                    wrapper.setPlaylistPos(Integer.parseInt(env.state.get(1).getValue().toString()));
                    ret = new LObj("ok");
                }
                else {
                    int pos = wrapper.getPlaylistPos();
                    if(pos != -1)
                        ret = new LObj(pos);
                    else
                        ret = new LObj("(error not_playing)");
                }
            }
            
            if(function.equals("next")) {
                wrapper.next();
                ret = new LObj("ok");
            }
            
            if(function.equals("previous")) {
                wrapper.previous();
                ret = new LObj("ok");
            }
            
            if(function.equals("track")) {
                if(isSet(env)) {
                    Collection<MPDSong> tracks =
                        wrapper.search(
                            "track",
                            env
                                .state
                                .get(1)
                                .getValue()
                                .getObj()
                                .toUnwrappedString()
                        );
                    wrapper.setPlaylist(tracks);
                    ret = new LObj("ok");
                }
                else {
                    MPDSong song = wrapper.getNowPlaying();
                    if(song == null) {
                        ret = new LObj("not_playing");
                    }
                    else {
                        ret = makeSong(song);
                    }
                }
            }
            
            if(function.equals("search")) {
                Collection<MPDSong> tracks =
                    wrapper.search(
                        env.state.get(1).getValue().toString(),
                        env
                            .state
                            .get(2)
                            .getValue()
                            .getObj()
                            .toUnwrappedString()
                    );
                ret = makeSongs(tracks);
            }
            
            if(function.equals("playlist")) {
                if(isSet(env)) {
                    String str = env.state.get(1).getValue().toString();
                    
                    if(str.equals("clear")) {
                        wrapper.removeTracks(wrapper.getPlaylist());
                        ret = new LObj("ok");
                    }
                    else if(str.equals("save")) {
                        wrapper.savePlaylist(env.state.get(2).getValue().toUnwrappedString());
                        ret = new LObj("ok");
                    }
                    else if(str.equals("load")) {
                        wrapper.loadPlaylist(env.state.get(2).getValue().toUnwrappedString());
                        ret = new LObj("ok");
                    }
                    else if(str.equals("remove-saved")) {
                        wrapper.deletePlaylist(env.state.get(2).getValue().toUnwrappedString());
                        ret = new LObj("ok");
                    }
                    else if(str.equals("saved")) {
                        ret = new LObj(true);
                        for(String x: wrapper.listPlaylists()) {
                            ret.addLObj(new LObj(x));
                        }
                    }
                    else {
                        ArrayList<LObj> objs;
                        if(str.equals("add") || str.equals("remove")) {
                            objs = env.state.get(2).getValue().getList();
                        }
                        else {
                            objs = env.state.get(1).getValue().getList();
                        }

                        ArrayList<MPDSong> tracks = new ArrayList();

                        for(LObj obj: objs) {
                            Dict dict = new Dict(obj);
                            tracks.add((MPDSong) dict.get(new LObj("raw")).getObj().get());
                        }

                        if(str.equals("add")) {
                            wrapper.addTracks(tracks);
                        }
                        else if(str.equals("remove")) {
                            wrapper.removeTracks(tracks);
                        }
                        else {
                            wrapper.setPlaylist(tracks);
                        }
                        ret = new LObj("ok");
                    }
                }
                else {
                    Collection<MPDSong> tracks = wrapper.getPlaylist();
                    ret = makeSongs(tracks);
                }
            }
            
            return ret;
        }

    }
    
    public static Boolean isSet(Environment env) {
        if(env.state.size() == 1)
            return false;
        else
            return true;
    }
    
    static public LObj makeSong(MPDSong song) {
        LObj out = new LObj(true);
        
        out.addLObj(new LObj("(id " + song.getId() + ")"));
        
        out = addNotNull(out, "title", song.getTitle());
        
        if(song.getAlbum() != null)
            out = addNotNull(out, "album", song.getAlbum().getName());
        if(song.getArtist() != null)
            out = addNotNull(out, "artist", song.getArtist().getName());
        
        out = addNotNull(out, "file", song.getFile());
        out = addNotNull(out, "year", song.getYear());
        
        LObj raw_label = new LObj("raw");
        LObj raw_obj = new LObj(song);
        LObj list = new LObj(true);
        list.addLObj(raw_label);
        list.addLObj(raw_obj);
        out.addLObj(list);
        
        return out;
    }
    
    static private LObj addNotNull(LObj out, String label, String data) {
        if(data != null)
            out.addLObj(new LObj("(" + label + "'" + LObj.escape(data) +"')"));
        return out;
    }
    
    static public LObj makeSongs(Collection<MPDSong> songs) {
        LObj out = new LObj(true);
        
        for(MPDSong song: songs)
            out.addLObj(makeSong(song));
        
        return out;
    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) throws MISPException {
                    title = "Search and set track, set playing";
                    env.execute(new LObj("(mpd @track 'Coma White')"));
                    env.execute(new LObj("(mpd @playing true)"));
                    env.execute(new LObj("(sleep 500)"));
                    String out =
                        (new Dict(env.execute(new LObj("(mpd @track)"))))
                            .get("title")
                            .getObj()
                            .toUnwrappedString();
                    return out.equals("Coma White");
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set and get volume";
                    int start = Integer.parseInt(env.execute(new LObj("(mpd @volume)")).toString());
                    env.execute(new LObj("(mpd @volume 60)"));
                    env.execute(new LObj("(sleep 500)"));
                    int mid = Integer.parseInt(env.execute(new LObj("(mpd @volume)")).toString());
                    env.execute(new LObj("(mpd @volume " + start + ")"));
                    env.execute(new LObj("(sleep 500)"));
                    int end = Integer.parseInt(env.execute(new LObj("(mpd @volume)")).toString());
                    return (end == start) && (mid == 60);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set and get play state";
                    env.execute(new LObj("(mpd @playing false)"));
                    env.execute(new LObj("(sleep 500)"));
                    String f = env.execute(new LObj("(mpd @playing)")).toString();
                    env.execute(new LObj("(mpd @playing true)"));
                    env.execute(new LObj("(sleep 500)"));
                    String t = env.execute(new LObj("(mpd @playing)")).toString();
                    return (t.equals("true")) && (f.equals("false"));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set and get track position";
                    int start = Integer.parseInt(env.execute(new LObj("(mpd @track-pos)")).toString());
                    env.execute(new LObj("(mpd track-pos 60)"));
                    int end = Integer.parseInt(env.execute(new LObj("(mpd @track-pos)")).toString());
                    return (start < 60) && (end >= 60);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) throws MISPException {
                    title = "Search album";
                    String out =
                        (new Dict(env.execute(new LObj("(element 3 (mpd @search @album 'With Teeth'))"))))
                            .get("title")
                            .toUnwrappedString();
                    return out.equals("The Collector");
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set and get playlist";
                    env.execute(new LObj("(define start (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(mpd @playlist start)"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(define end (mpd @playlist))"));
                    env.execute(new LObj("(define getTitles "
                        + "(lambda (list) "
                        + "(map (lambda (x) (get 'title' x)) list) "
                        + "))"));
                    return MISPTests.compareLObj("true", env.execute(new LObj("(= (getTitles start) (getTitles end))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set and get playlist position";
                    env.execute(new LObj("(mpd playlist (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(mpd @playing true)"));
                    env.execute(new LObj("(mpd @playlist-pos 4)"));
                    env.execute(new LObj("(sleep 500)"));
                    return MISPTests.compareLObj("true", env.execute(new LObj("(= 4 (mpd @playlist-pos))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Add tracks to playlist";
                    env.execute(new LObj("(mpd @playlist @add (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    return MISPTests.compareLObj("Only",
                       env.execute(
                           new LObj("(get @title (element 21 (mpd @playlist)))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Remove track from playlist";
                    env.execute(new LObj("(mpd @playlist (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(mpd @playlist @remove (list (element 2 (mpd @playlist))))"));
                    return MISPTests.compareLObj("The Collector",
                       env.execute(
                           new LObj("(get @title (element 2 (mpd @playlist)))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Clear playlist";
                    env.execute(new LObj("(mpd @playlist (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(mpd @playlist @clear)"));
                    return MISPTests.compareLObj("true",
                       env.execute(
                           new LObj("(= () (mpd @playlist))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Save, load, list and delete playlist";
                    env.execute(new LObj("(mpd playlist (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(mpd @playlist @remove (list (element 2 (mpd @playlist))))"));
                    env.execute(new LObj("(define s1 (length (mpd @playlist)))"));
                    env.execute(new LObj("(mpd @playlist save '__JPCTESTPLS')"));
                    env.execute(new LObj("(mpd @playlist (mpd @search @album 'With Teeth'))"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(define s2 (length (mpd @playlist)))"));
                    env.execute(new LObj("(mpd @playlist @load '__JPCTESTPLS')"));
                    env.execute(new LObj("(sleep 500)"));
                    env.execute(new LObj("(define s3 (length (mpd @playlist)))"));
                    env.execute(new LObj("(mpd @playlist remove-saved '__JPCTESTPLS')"));
                    env.execute(new LObj("(define s4 (member '__JPCTESTPLS' (mpd @playlist @saved)))"));
                    return MISPTests.compareLObj("true",
                       env.execute(
                           new LObj("(and (and (not (= s1 s2)) (= s1 s3)) (not s4))")));
                }
            });
        
        
        return tests;
    }
}
