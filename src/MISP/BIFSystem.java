/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import JPC.UIState;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 */
public class BIFSystem {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        bifs.add(new Variable(new BIFSystem.BIFSleep()));
        bifs.add(new Variable(new BIFSystem.BIFQuit()));
        bifs.add(new Variable(new BIFSystem.BIFJPC()));
        
        return bifs;
    }
    
    public static class BIFSleep extends BuiltIn {

        @Override
        public String name() {
            return "sleep";
        }

        @Override
        public LObj execute(Environment env) {
            int ms = Integer.parseInt(env.state.get(0).getValue().toString());
            
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ex) {
                Logger.getLogger(BIFSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return new LObj("ok");
        }

    }
    
    public static class BIFQuit extends BuiltIn {

        @Override
        public String name() {
            return "quit";
        }

        @Override
        public LObj execute(Environment env) {
            System.exit(0);
            return null;
        }

    }
    
    public static class BIFJPC extends BuiltIn {

        @Override
        public String name() {
            return "jpc";
        }

        @Override
        public LObj execute(Environment env) throws MISPException {
            UIState state = UIState.getInstance();
            LObj ret = new LObj("(error unrecognised)");
            
            String function = env.state.get(0).getValue().getObj().toString();
            
            if(function.equals("search")) {
                if(env.state.size() > 1) {
                    if(env.state.get(1).getValue().toString().equals("false")) {
                        state.search.clear();
                    }
                    else {
                        ArrayList<MPDSong> tracks = new ArrayList();
                        for(LObj obj: env.state.get(1).getValue().getList()) {
                            Dict dict = new Dict(obj);
                            tracks.add((MPDSong) dict.get(new LObj("raw")).getObj().get());
                        }
                        state.search.clear();
                        state.search.addAll(tracks);
                    }
                    ret = new LObj("ok");
                }
                else {
                    if(state.search.isEmpty()) {
                        ret = new LObj("false");
                    }
                    else {
                        ret = BIFMPD.makeSongs(state.search);
                    }
                }
            }
            
            return ret;
        }

    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Suspend execution for 0.5 seconds";
                    long start = new Date().getTime();
                    env.execute(new LObj("(sleep 500)"));
                    long end = new Date().getTime();
                    return (end - start) > 400;
                }
            });
        return tests;
    }
}
