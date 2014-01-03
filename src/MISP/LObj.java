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
public class LObj {
    private ArrayList<LObj> list;
    private Literal obj;
    private char expected;
    public LObj() {
        
    }
    
    public LObj(Boolean isList) {
        if(isList)
            list = new ArrayList();
    }
    
    public LObj(String input) {
        if(input.startsWith("(")) {
            try {
                ArrayList<String> elements = extractElements(input);
                list = new ArrayList();
                
                for(String el: elements) list.add(new LObj(el));
            } catch (ParseError ex) {
                this.expected = ex.getExpected();
            }
            
        }
        else if (isValidLiteral(input)) {
            obj = new Literal(input);
        }
    }

    public static String escape(String in) {
        if(in == null) return "(null)";
        
        String out = "";
        char[] chars = in.toCharArray();
        
        for(int i = 0; i < chars.length; i++) {
            if (chars[i] == '"' || chars[i] == '\'') {
                out += "\\";
            }
            out += chars[i];
        }
        
        return out;
    }
    
    public Boolean isSane() {
        return list != null || obj != null;
    }
    
    public Boolean isValidLiteral(String in) {
        Boolean string = false;
        char[] chars = in.toCharArray();
        
        for(int i = 0; i < chars.length; i++) {
            if(!string && (chars[i] == ' ' || chars[i] == '\t' || chars[i] == '\n'))
                return false;
            if(chars[i] == '"' || chars[i] == '\'') {
                if(i > 0) {
                    if(chars[i-1] != '\\') {
                        string = !string;
                    }
                }
                else {
                    string = true;
                }
            }
        }
        
        return true;
    }
    public LObj(int val) {
        obj = new Literal(Literal.Types.INT, val);
    }

    public LObj(float val) {
        obj = new Literal(Literal.Types.FLOAT, val);
    }

    public LObj(BuiltIn bif) {
        obj = new Literal(Literal.Types.BUILTIN, bif);
    }

    public LObj(ArrayList<LObj> objs) {
        list = objs;
    }

    public LObj(Object jobj) {
        obj = new Literal(Literal.Types.JAVA, jobj);
    }
    
    @Override
    public String toString() {
        if(this.isLiteral())
            return obj.toString();
        
        String out = "(";
        
        for(LObj obj: list) out += obj.toString() + " ";
        
        out = out.trim();
        out += ")";
        
        return out;
    }
    
    public String toUnwrappedString() {
        if(this.isLiteral())
            return obj.toUnwrappedString();
        
        String out = "(";
        
        for(LObj obj: list) {
            out += obj.toString() + " ";
        }
        
        out = out.trim();
        out += ")";
        
        return out;
    }
    
    private ArrayList<String> extractElements(String input) throws ParseError {
        input = input.substring(1);
        ArrayList<String> els = new ArrayList();
        char[] chars = input.toCharArray();
        String string = null;
        char string_char = 0;
        Boolean comment = false;
        String el = "";
        int depth = 0;
        
        for(int i = 0; i < chars.length; i++) {
            if(string == null) {
                if(depth == 0) {
                    switch (chars[i]) {
                        case ';':
                            comment = true;
                            break;
                        case '\"':
                        case '\'':
                            if(!comment)
                                string = "";
                                string_char = chars[i];
                                string += chars[i];
                            break;
                        case '\n':
                            comment = false;
                        case '\t':
                        case ' ':
                            if(!el.equals("") && !comment)
                                els.add(el);
                            el = "";
                            break;
                        case ')':
                            if(!el.equals("") && !comment)
                                els.add(el);
                            return els;
                        case '(':
                            if(!comment)
                                depth++;
                        default:
                            if(!comment)
                                el += chars[i];
                            break;
                    }
                }
                else {
                    if(chars[i] == '(' && !comment) {
                        depth++;
                    }
                    else if(chars[i] == ')' && !comment) {
                        depth--;
                    }
                    else if((chars[i] == '\"' || chars[i] == '\'') && !comment) {
                        string = "";
                        string_char = chars[i];
                    }
                    if(!comment) el += chars[i];
                }
            }
            else {
                if(chars[i] == '\\' && chars[i+1] == string_char) {
                    string += "\\";
                    string += string_char;
                    i++;
                }
                else if(chars[i] == string_char) {
                    string += chars[i];
                    if(depth == 0) els.add(string);
                    else el += string;
                    string = null;
                }
                else {
                    string += chars[i];
                }
            }
        }
        
        if(string != null) throw new ParseError('"');
        if(depth != -1) throw new ParseError(')');
        
        return null;
    }

    public Boolean isLiteral() {
        if(this.obj != null)
            return true;
        else
            return false;
    }
    
    public ArrayList<LObj> getList() {
        return list;
    }

    public Literal getObj() {
        return obj;
    }
    
    public void setObj(Literal obj) {
        list = null;
        this.obj = obj;
    }
    
    public void setList(ArrayList<LObj> obj) {
        obj = null;
        this.list = obj;
    }
    
    public void addLObj(LObj obj) {
        this.list.add(obj);
    }
    
    public char getExpected() {
        return expected;
    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<Test>();
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Parse literal integer";
                    return MISPTests.compareLObj(123, env.execute(new LObj("123")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Parse literal float";
                    return MISPTests.compareLObj(123.4f, env.execute(new LObj("123.4")));
                }
            });
        
        return tests;
    }
}
