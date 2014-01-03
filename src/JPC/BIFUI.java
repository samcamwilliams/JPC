/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import MISP.BuiltIn;
import MISP.Dict;
import MISP.Environment;
import MISP.LObj;
import MISP.MISPException;
import java.util.ArrayList;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 */
public class BIFUI {
    public static ArrayList<MISP.Variable> getAllVars() {
        ArrayList<MISP.Variable> bifs = new ArrayList<>();
        bifs.add(new MISP.Variable(new BIFUI.BIFBind()));
        bifs.add(new MISP.Variable(new BIFUI.BIFBindA()));
        bifs.add(new MISP.Variable(new BIFUI.BIFBindZ()));
        bifs.add(new MISP.Variable(new BIFUI.BIFUnbind()));
        bifs.add(new MISP.Variable(new BIFUI.BIFBindings()));
        bifs.add(new MISP.Variable(new BIFUI.BIFToBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFNameBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFFocusBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFSyncBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFUnsyncBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFIsSyncBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFDeleteBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFActiveBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFCountBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFPlaylistBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFPlaylistDeleteBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFPlaylistAddBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFPlaylistClearBuffer()));
        bifs.add(new MISP.Variable(new BIFUI.BIFAfter()));
        
        return bifs;
    }
    
    private static Binding makeBinding(Environment env) {
        String binding = env.state.get(0).getValue().toUnwrappedString();
        String function = env.state.get(1).getValue().toUnwrappedString();
        Boolean exec = false;
        if(env.state.size() == 3) {
            if(env.state.get(2).getValue().toString().equals("true"))
                exec = true;
        }
        
        return new Binding(binding, function, exec);
    }
    
    public static class BIFBind extends BuiltIn {
        @Override
        public String name() { return "bind"; }
        
        @Override
        public LObj execute(Environment env) {
            return new BIFBindZ().execute(env);
        }

    }
    
    public static class BIFBindA extends BuiltIn {
        @Override
        public String name() { return "binda"; }

        @Override
        public LObj execute(Environment env) {
            UIState.getInstance().bindings.add(0, BIFUI.makeBinding(env));
            return new LObj("ok");
        }

    }
    
    public static class BIFBindZ extends BuiltIn {
        @Override
        public String name() { return "bindz"; }

        @Override
        public LObj execute(Environment env) {
            UIState.getInstance().bindings.add(BIFUI.makeBinding(env));
            return new LObj("ok");
        }

    }
    
    public static class BIFBindings extends BuiltIn {
        @Override
        public String name() { return "bindings"; }

        @Override
        public LObj execute(Environment env) {
            LObj o = new LObj("()");
            
            for(Binding b: UIState.getInstance().bindings) {
                LObj o2 = new LObj("()");
                LObj b1 = new LObj("()");
                LObj mk = new LObj("binding");
                LObj mv = new LObj("\"" + b.getMatch() + "\"");
                b1.addLObj(mk);
                b1.addLObj(mv);
                
                LObj b2 = new LObj("()");
                LObj ck = new LObj("command");
                LObj cv = new LObj("\"" + b.getCom() + "\"");
                b2.addLObj(ck);
                b2.addLObj(cv);
                
                LObj b3 = new LObj("()");
                LObj ek = new LObj("autoexec");
                LObj ev = new LObj(b.getExec().toString());
                b3.addLObj(ek);
                b3.addLObj(ev);
                
                o2.addLObj(b1);
                o2.addLObj(b2);
                o2.addLObj(b3);
                
                o.addLObj(o2);
            }
            
            return o;
        }

    }
    
    public static class BIFUnbind extends BuiltIn {
        @Override
        public String name() { return "unbind"; }

        @Override
        public LObj execute(Environment env) {
            for(Binding b: UIState.getInstance().bindings) {
                if(b.getMatch().pattern().equals(env.state.get(0).getValue().toUnwrappedString())) {
                    UIState.getInstance().bindings.remove(b);
                    return new LObj("ok");
                }
            }
            
            return new LObj("not_found");
        }

    }
    
    public static class BIFToBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer"; }

        @Override
        public LObj execute(Environment env) throws MISPException {
            ArrayList<LObj> objs = env.state.get(0).getValue().getList();
            ArrayList<MPDSong> tracks = new ArrayList();
            for(LObj obj: objs)
                tracks.add((MPDSong) new Dict(obj).get(new LObj("raw")).getObj().get());
            
            UIState state = UIState.getInstance();
            
            if(env.state.size() == 2)
                state.buffers.get(Integer.parseInt(env.state.get(1).getValue().toString()) - 1).setTracks(tracks);
            else
                state.buffers.add(
                    new Buffer(
                        "New Buffer",
                        state.tabbed,
                        tracks
                    )
                );
            
            return new LObj("ok");
        }

    }
    
    public static class BIFNameBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-name"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            int target;
            
            if(env.state.size() == 2)
                target = Integer.parseInt(env.state.get(1).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            state.buffers.get(target)
                .setName(env.state.get(0).getValue().toUnwrappedString());

            return new LObj("ok");
        }

    }
    
    public static class BIFFocusBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-focus"; }

        @Override
        public LObj execute(Environment env) throws MISPException {
            UIState state = UIState.getInstance();
            try {
                Buffer.activateBuffer(state,
                    Integer.parseInt(env.state.get(0).getValue().toString()) - 1);
            } catch(IndexOutOfBoundsException _) {
                throw new MISPException(
                    "Buffer "
                        + Integer.parseInt(env.state.get(0).getValue().toString())
                        + " not found.");
            }
            return new LObj("ok");
        }

    }
    
    public static class BIFSyncBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-sync"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            state.buffers.get(target).setSync(true);
            return new LObj("ok");
        }

    }
    
    public static class BIFUnsyncBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-unsync"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            state.buffers.get(target).setSync(false);
            return new LObj("ok");
        }

    }
    
    public static class BIFIsSyncBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-is-sync"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            return new LObj(String.valueOf(state.buffers.get(target).isSync()));
        }

    }
    
    public static class BIFPlaylistBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-playlist"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            
            return MISP.BIFMPD.makeSongs(state.buffers.get(target).getTracks());
        }

    }
    
    public static class BIFPlaylistAddBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-playlist-add"; }

        @Override
        public LObj execute(Environment env) throws MISPException {
            UIState state = UIState.getInstance();
            LObj obj = env.state.get(0).getValue();
            
            int dst;
            
            if(env.state.size() >= 2)
                dst = Integer.parseInt(env.state.get(1).getValue().toString()) - 1;
            else
                dst = state.tabbed.getSelectedIndex();
            
            int src = dst;
            
            if(env.state.size() == 3)
                src = Integer.parseInt(env.state.get(2).getValue().toString()) - 1;
            
            ArrayList<MPDSong> songs = new ArrayList();
            
            if(obj.isLiteral())
                songs.add(state.buffers.get(src).get(obj.getObj().toInt() - 1));
            else {
                ArrayList<LObj> list = obj.getList();
                for(LObj o: list) {
                    if(o.isLiteral()) {
                        songs.add(state.buffers.get(src).get(o.getObj().toInt() - 1));
                    }
                    else {
                        songs.add((MPDSong) new Dict(o).get(new LObj("raw")).getObj().get());
                    }
                }
            }
            
            state.buffers.get(dst).addAll(songs);
            
            return new LObj("ok");
        }

    }
    
    public static class BIFPlaylistDeleteBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-playlist-delete"; }

        @Override
        public LObj execute(Environment env) throws MISPException {
            UIState state = UIState.getInstance();
            LObj obj = env.state.get(0).getValue();
            
            int target;
            
            if(env.state.size() == 2)
                target = Integer.parseInt(env.state.get(1).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            ArrayList<MPDSong> songs = new ArrayList();
            
            if(obj.isLiteral())
                songs.add(state.buffers.get(target).get(obj.getObj().toInt() - 1));
            else
                for(LObj o: obj.getList())
                    if(o.isLiteral())
                        songs.add(state.buffers.get(target).get(o.getObj().toInt() - 1));
                    else
                        songs.add((MPDSong) new Dict(o).get(new LObj("raw")).getObj().get());
            
            state.buffers.get(target).removeAll(songs);
            
            return new LObj("ok");
        }

    }
    
    public static class BIFPlaylistClearBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-playlist-clear"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            state.buffers.get(target).clear();
            
            return new LObj("ok");
        }

    }
    
    public static class BIFDeleteBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-delete"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            int target;
            
            if(env.state.size() == 1)
                target = Integer.parseInt(env.state.get(0).getValue().toString()) - 1;
            else
                target = state.tabbed.getSelectedIndex();
            
            state.buffers.remove(target); 
            state.tabbed.removeTabAt(target);
            
            for(Buffer b: state.buffers) b.setName();
            
            state.tabbed.invalidate();
            
            return new LObj("ok");
        }

    }
    
    public static class BIFActiveBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-active"; }

        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            return new LObj(state.tabbed.getSelectedIndex() + 1);
        }

    }
    
    public static class BIFCountBuffer extends BuiltIn {
        @Override
        public String name() { return "buffer-count"; }

        @Override
        public LObj execute(Environment env) {
            return new LObj(UIState.getInstance().tabbed.getTabCount());
        }

    }
    
    public static class BIFAfter extends BuiltIn {
        @Override
        public String name() { return "after"; }
        
        @Override
        public Boolean isConditional() { return true; }

        @Override
        public LObj execute(Environment env) {
            Hook h;
            if(env.state.size() == 1)
                h = new Hook(Hook.Types.TRACK, env.state.get(0).getValue());
            else
                h = new Hook(Hook.Types.TRACK, env.state.get(0).getValue(),
                    Boolean.parseBoolean(env.state.get(0).getValue().toUnwrappedString()));
            
            UIState.getInstance().hooks.add(h);
            
            return new LObj("ok");
        }

    }
    
}
