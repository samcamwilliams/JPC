/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

/**
 *
 * @author spine
 */
public class BuiltIn {
    public String name() {
        return null;
    }
    public LObj execute(Environment env) throws MISPException {
        return null;
    }
    public LObj execute(Environment env, Environment global) throws MISPException {
        return execute(env);
    }
    public Boolean isConditional() {
        return false;
    }
    public Boolean hasArgs() {
        return true;
    }
    
    public static LObj boolToLObj(Boolean x) {
        if(x)
            return new LObj("true");
        else
            return new LObj("false");
    }
    
    public static Boolean LObjToBool(LObj x) {
        String o = (String) x.getObj().get();
        return o.equals("true");
    }
    
    public static LObj stringToAtom(String x) {
        return new LObj(x);
    }
}
