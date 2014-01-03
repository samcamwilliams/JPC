/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.util.ArrayList;

/**
 *
 * @author spine
 */
public class Logger {
    private ArrayList<LogItem> items;
    
    public Logger() {
        items = new ArrayList<>();
    }
    
    public ArrayList<LogItem> get() {
        return items;
    }
    
    public void add(int level, String text) {
        items.add(new LogItem(level, text));
    }
    
    public String format(int level) {
        String out = "";
        
        for(LogItem e: items) {
            if(e.getLevel() >= level)
                out += e.format() + "\n";
        }
        
        return out;
    }
    
}
