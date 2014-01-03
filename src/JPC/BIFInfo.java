/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import MISP.BuiltIn;
import MISP.Environment;
import MISP.LObj;
import MISP.Variable;
import java.util.ArrayList;

/**
 *
 * @author spine
 */
public class BIFInfo {
    public static ArrayList<MISP.Variable> getAllVars() {
        ArrayList<MISP.Variable> bifs = new ArrayList<>();
        bifs.add(new MISP.Variable(new BIFInfo.BIFInfoBindings()));
        bifs.add(new MISP.Variable(new BIFInfo.BIFInfoDefinitions()));
        bifs.add(new MISP.Variable(new BIFInfo.BIFInfoMain()));
        bifs.add(new MISP.Variable(new BIFInfo.BIFInfoHTML()));
        return bifs;
    }
    
    public static class BIFInfoBindings extends BuiltIn {
        @Override
        public String name() { return "info-bindings"; }
        
        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            String str = "<table><tr><td><b>Pattern</b></td><td>Command</td></tr>";
            
            for(Binding b: state.bindings) {
                str += "<tr><td><b>"
                    + b.getPattern() + "</b></td><td>" + b.getCom() + "</td></tr>";
            }
            
            str += "</table>";
            
            state.showInfo("Bindings", str);
            
            return new LObj("ok");
        }

    }
    
    public static class BIFInfoDefinitions extends BuiltIn {
        @Override
        public String name() { return "info-definitions"; }
        
        @Override
        public LObj execute(Environment env, Environment global) {
            UIState state = UIState.getInstance();
            
            String str = "<table><tr><td><b>Key</b></td><td>Value</td></tr>";
            
            for(Variable v: global.state) {
                str += "<tr><td><b>"
                    + v.getName() + "</b></td><td>" + v.getValue() + "</td></tr>";
            }
            
            str += "</table>";
            
            state.showInfo("Definitions", str);
            
            return new LObj("ok");
        }

    }
    
    public static class BIFInfoMain extends BuiltIn {
        @Override
        public String name() { return "info"; }
        
        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            String str = "<pre>" + env.state.get(0).getValue().toUnwrappedString() + "</pre>";
            
            if(env.state.size() == 1)
                state.showInfo("Info", str);
            else
                state.showInfo(env.state.get(1).getValue().toUnwrappedString(), str);
            
            return new LObj("ok");
        }

    }
    
    public static class BIFInfoHTML extends BuiltIn {
        @Override
        public String name() { return "info-html"; }
        
        @Override
        public LObj execute(Environment env) {
            UIState state = UIState.getInstance();
            
            String str = env.state.get(0).getValue().toUnwrappedString();
            
            if(env.state.size() == 1)
                state.showInfo("Info", str);
            else
                state.showInfo(env.state.get(1).getValue().toString(), str);
            
            return new LObj("ok");
        }

    }
}
