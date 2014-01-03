/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author spine
 */
public class StackTrace {
    ArrayList<Call> calls;

    public StackTrace() {
        calls = new ArrayList();
    }
    
    public StackTrace(ArrayList<Call> calls) {
        this.calls = calls;
    }
    
    public void add(LObj obj) {
        calls.add(new Call(obj));
    }
    
    public ArrayList<Call> getCalls() {
        return this.calls;
    }
    
    @Override
    public StackTrace clone() {
        return new StackTrace((ArrayList<Call>) this.calls.clone());
    }
    
    @Override
    public String toString() {
        String str = "";
        
        for(Call c: calls)
            str += c.toString() + "\n";
        
        return str;
    }
    
    public static class Call {
        private LObj obj;
        
        public Call(LObj obj) {
            this.obj = obj;
        }
        
        public LObj get() {
            return obj;
        }
        
        public String toString() {
            return obj.toString();
        }
    }
    
    
}
