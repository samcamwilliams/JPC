/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;
import org.bff.javampd.objects.MPDSong;

public class Buffer {
    private javax.swing.JScrollPane scroll;
    private javax.swing.JEditorPane output;
    private ArrayList<MPDSong> tracks;
    private String old_output = "";
    private Boolean active = false;
    private Boolean sync = false;
    private String title = "New Buffer";

    public Buffer(String title, JTabbedPane tabbed) {
        this.tracks = new ArrayList();
        this.title = title;
        output = new javax.swing.JEditorPane();
        scroll = new javax.swing.JScrollPane();
        
        output.setEditable(false);
        output.setContentType("text/html");
        output.setToolTipText(null);
        output.setFocusable(false);
        scroll.setViewportView(output);
        
        tabbed.addTab(formatName(title), scroll);
        DefaultCaret c = (DefaultCaret)output.getCaret();
        c.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        refresh();
    }
    
    public Buffer(String title, JTabbedPane tabbed, ArrayList<MPDSong> tracks) {
        this(title, tabbed);
        this.tracks = tracks;
        refresh();
    }
    
    public Buffer(JScrollPane scroll, JEditorPane output) {
        this.scroll = scroll;
        this.output = output;
        this.tracks = new ArrayList();
        DefaultCaret c = (DefaultCaret)output.getCaret();
        c.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        refresh();
    }
    
    public Buffer(JScrollPane scroll, JEditorPane output, ArrayList<MPDSong> tracks) {
        this(scroll, output);
        this.tracks = tracks;
        refresh();
    }
    
    public void setActive(Boolean b) {
        this.active = b;
        refresh();
    }
    
    public Boolean isActive() {
        return this.active;
    }
    
    public void setSync(Boolean b) {
        this.sync = b;
        this.setName();
        refresh();
    }
    
    public Boolean isSync() {
        return this.sync;
    }
    
    public void refresh() {
        refresh(UIState.getInstance().wrapper.getStatus());
    }
    
    public void refresh(MPDStatus status) {   
        if(isSync()) tracks = status.getPlaylist();
        
        if(status.getPlaylist().equals(tracks))
            UIState.getInstance().tabbed.setIconAt(this.getIndex(), UIManager.getIcon("FileView.directoryIcon"));
        else
            UIState.getInstance().tabbed.setIconAt(this.getIndex(), null);
        
        if(!active) return; // If the buffer is inactive, don't bother updating
        
        String pls = "";
        
        MPDSong current = status.getNowPlaying();
        
        
        int i = 1;
        for(MPDSong song: tracks) {
            
            if(song.equals(current)) pls += "<b>";
            
            pls += Integer.toString(i) + ". ";
            
            if(song.getArtist() != null)
                pls += song.getArtist().getName();
            else
                pls += "Unknown Artist";
            
            pls += " - ";
            
            if(song.getAlbum() != null)
                pls += song.getAlbum().getName();
            else
                pls += "Unknown Album";
            
            pls += " - " + song.getTitle() + "<br>";
            
            if(song.equals(current)) pls += "</b>";
            
            i++;
        }
        
        if(pls.equals(this.old_output)) return;
       
        output.setText(pls);
        this.old_output = pls;
    }
    
    public void sync() {
        if(this.sync) UIState.getInstance().wrapper.setPlaylist(tracks);
    }
    
    public ArrayList<MPDSong> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<MPDSong> tracks) {
        this.tracks = tracks;
        sync();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
                }
                refresh();
            }
          });
    }

    public int size() {
        return tracks.size();
    }

    public MPDSong get(int index) {
        return tracks.get(index);
    }

    public void remove(int index) {
        tracks.remove(index);
        sync();
        refresh();
    }

    public void addAll(Collection<MPDSong> c) {
        tracks.addAll(c);
        if(this.sync) UIState.getInstance().wrapper.addTracks((List<MPDSong>) c);
        refresh();
    }

    public void removeAll(Collection<MPDSong> c) {
        tracks.removeAll(c);
        if(this.sync) UIState.getInstance().wrapper.removeTracks((List<MPDSong>) c);
        refresh();
    }

    public void clear() {
        tracks.clear();
        sync();
        refresh();
    }
    
    public int getIndex() {
        int index = UIState.getInstance().buffers.indexOf(this);
        if(index == -1) return UIState.getInstance().buffers.size();
        return index;
    }
    
    public static void activateBuffer(UIState state, int i) {
        int j = 0;
        for(Buffer b: state.buffers) { b.setActive(i == j); j++; }
        state.tabbed.setSelectedIndex(i);
    }
    
    public void setName() {
        int index = this.getIndex();
        
        UIState.getInstance().tabbed.setTitleAt(
            index,
            formatName(title)
        );
    }
    
    public void setName(String new_name) {
        title = new_name;
        setName();
    }
    
    public String formatName(String str) {
        String out = (getIndex() + 1) + ": " + str;
        
        if(this.isSync()) out += " [Sync]";
        
        return out;
    }
}
