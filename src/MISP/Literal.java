/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

/**
 *
 * @author spine
 */
public class Literal {
    public enum Types {
        STRING,
        INT,
        FLOAT,
        ATOM,
        BUILTIN,
        JAVA
    }
    private Types type;
    private Object obj;
    private Boolean constant = false;
    
    public Literal(BuiltIn BIF) {
        this.type = Types.BUILTIN;
        this.obj = BIF;
    }
    
    public Literal(Literal.Types type, Object obj) {
        this.type = type;
        
        if(type == Types.ATOM || type == Types.BUILTIN || type == Types.JAVA) {
            this.obj = obj;
        }
        else if(type == Types.STRING) {
            String str = (String) obj;
            str = str.subSequence(1, str.length()-1).toString();
            this.obj = str;
        }
        else if(type == Types.INT) {
            this.obj = Integer.toString(((int) obj));
        }
        else if(type == Types.FLOAT) {
            this.obj = Float.toString(((Float) obj));
        }
    }
    
    public Literal(String str) {
        if(str.matches("^[0-9]+$")) {
            type = Types.INT;
        }
        else if(str.matches("^[0-9\\.]+$")) {
            type = Types.FLOAT;
        }
        else if(str.startsWith("\"") || str.startsWith("'")) {
            type = Types.STRING;
            str = str.subSequence(1, str.length()-1).toString();
        }
        else if(!str.contains(" ")){
            type = Types.ATOM;
            if(str.startsWith("@")) {
                constant = true;
                str = str.substring(1);
            }
        }
        obj = str;
    }
    
    public Object get() {
        if(type == Types.ATOM || type == Types.STRING || type == Types.BUILTIN || type == Types.JAVA) {
            return obj;
        }
        else if(type == Types.INT) {
            return Integer.parseInt(obj.toString());
        }
        else if(type == Types.FLOAT) {
            return Float.parseFloat(obj.toString());
        }
        return null;
    }
    
    public Types getType() {
        return this.type;
    }

    @Override
    public String toString() {
        if(type == Types.ATOM)
            return (String) obj;
        if(type == Types.STRING)
            return "\"" + ((String) obj) + "\"";
        if(type == Types.BUILTIN)
            return "BIF(" + ((BuiltIn) obj).name() + ")";
        if(type == Types.JAVA)
            return "JAVA(" + obj.toString() + ")";
        if(type == Types.INT)
            return (String) obj;
        if(type == Types.FLOAT)
            return (String) obj;
        
        return obj.toString();
    }

    public String toUnwrappedString() {
        return (String) obj;
    }
    
    public Float toFloat() {
        if(type == Types.INT) {
            return ((float) Integer.parseInt(obj.toString()));
        }
        else if(type == Types.FLOAT) {
            return Float.parseFloat(obj.toString());
        }
        return null;
    }
    
    public int toInt() {
        return Integer.parseInt(obj.toString());
    }
    
    public Boolean isConstant() {
        return this.constant;
    }
}
