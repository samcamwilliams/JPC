/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author spine
 */
public class Binding {
    private Pattern match;
    private String pattern;
    private String com;
    private Boolean exec;

    public Pattern getMatch() {
        return match;
    }

    public String getCom() {
        return com;
    }

    public Boolean getExec() {
        return exec;
    }
    
    public Binding(String match, String com) {
        this.match = Pattern.compile("^" + match + "$");
        this.pattern = match;
        this.com = com;
        this.exec = false;
    }
    
    public Binding(String match, String com, Boolean exec) {
        this.match = Pattern.compile("^" + match + "$");
        this.pattern = match;
        this.com = com;
        this.exec = exec;
    }
    
    public Boolean matches(String str) {
        return match.matcher(str).find();
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public String getCom(String str) {
        int c = 1;
        String command = this.com;
        Matcher matcher = match.matcher(str);
        matcher.find();

        while(c <= matcher.groupCount()) {
            command = command.replace("$" + c, matcher.group(c));
            c++;
        }
        
        return command;
    }
    
    public Boolean isExec() {
        return this.exec;
    }
}
