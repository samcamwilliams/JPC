/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.util.Date;

/**
 *
 * @author spine
 */
public class LogItem {
    private String message;
    private Date time;
    private int level;
    
    public LogItem(int level, String message) {
        this.message = message;
        this.level = level;
        time = new Date();
    }
    
    public String format() {
        return Integer.toString(level) + "\t"
               + doubleDigitFormat(time.getHours()) + ":"
               + doubleDigitFormat(time.getMinutes()) + ":"
               + doubleDigitFormat(time.getSeconds()) + " "
               + doubleDigitFormat(time.getDate()) + "/"
               + doubleDigitFormat(time.getDate()) + "/"
               + doubleDigitFormat(time.getDate()) + " "
               + message;
    }
    
    private String doubleDigitFormat(int in) {
        String out = Integer.toString(in);
        
        if(out.length() == 1)
            out = "0" + out;
        
        return out;
    }
    
    public int getLevel() {
        return level;
    }
}
