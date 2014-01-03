/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

/**
 *
 * @author spine
 */
public class Variable {
    private String name;
    private LObj value;

    public Variable(String name, LObj obj) {
        this.name = name;
        this.value = obj;
    }
    
    public Variable(BuiltIn bif) {
        this.name = bif.name();
        this.value = new LObj(bif);
    }
    
    public String getName() {
        return name;
    }

    public LObj getValue() {
        return value;
    }

    public void setValue(LObj value) {
        this.value = value;
    }
    
    public String toString() {
        return "{" + getName() + ": " + getValue().toString() + "}";
    }
}
