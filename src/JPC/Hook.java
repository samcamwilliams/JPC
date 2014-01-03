/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import MISP.LObj;

/**
 *
 * @author spine
 */
public class Hook {
    public enum Types { TRACK };
    private Boolean temporary = true;
    private Types type;
    private LObj cmd;

    public Hook(Types type, LObj cmd) {
        this.type = type;
        this.cmd = cmd;
    }
    
    public Hook(Types type, LObj cmd, Boolean temporary) {
        this.type = type;
        this.cmd = cmd;
        this.temporary = temporary;
    }
    
    public Boolean isActive(Boolean trackChange, long trackPos) {
        if(type == Types.TRACK && trackChange) return true;
        else return false;
    }
    
    public LObj getCommand() {
        return cmd;
    }
    
    public Boolean isTemporary() {
        return temporary;
    }
}
