/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;

/**
 *
 * @author spine
 */
public class Dict {
    ArrayList<Pair> pairs;
    
    public Dict(LObj obj) throws MISPException {
        pairs = new ArrayList<>();
        ArrayList<LObj> objs = obj.getList();
        
        if(objs == null)
            throw new MISPException("Not a list: " + obj);
        
        for(LObj x: objs) {
            ArrayList<LObj> x_objs = x.getList();
            
            if(x_objs == null) throw new MISPException("Not a list: " + x_objs);
            
            pairs.add(
                new Pair(
                    x_objs.get(0),
                    x_objs.get(1)
                )
            );
        }
        
    }
    
    public LObj get(String str) {
        return get(new LObj(str));
    }
    
    public LObj get(LObj obj) {
        for(Pair x: pairs) {
            if(x.key.toString().equals(obj.toString())) {
                return x.val;
            }
        }
        
        return null;
    }
    
    public void set(LObj key, LObj val) {
        for(Pair x: pairs) {
            if(x.key == key) {
                x.val = val;
                return;
            }
        }
        
        pairs.add(new Pair(key, val));
    }
    
    public LObj toLObj() {
        LObj obj = new LObj(true);
        
        for(Pair x: pairs) {
            LObj y = new LObj(true);
            y.addLObj(x.key);
            y.addLObj(x.val);
            obj.addLObj(y);
        }
        
        return obj;
    }
    
    public class Pair {
        public LObj key;
        public LObj val;
        
        public Pair(LObj key, LObj val) {
            this.key = key;
            this.val = val;
        }
    }
}
