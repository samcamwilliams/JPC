/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import static MISP.BuiltIn.boolToLObj;
import java.util.ArrayList;

/**
 *
 * @author spine
 */
public class BIFComp {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<Variable>();
        bifs.add(new Variable(new BIFEq()));
        bifs.add(new Variable(new BIFGt()));
        bifs.add(new Variable(new BIFLt()));
        
        return bifs;
    }
    
    public static class BIFEq extends BuiltIn {

        @Override
        public String name() {
            return "=";
        }

        @Override
        public LObj execute(Environment env) {
            Boolean ret = true;
            LObj value = null;
            LObj test = null;
            
            for(Variable var: env.state) {
                if(value == null) {
                    value = var.getValue();
                }
                else {
                    test = var.getValue();
                    if(!test.toString().equals(value.toString())) {
                        ret = false;
                    }
                }
            }
            
            return boolToLObj(ret);
        }

    }
    
    public static class BIFGt extends BuiltIn {

        @Override
        public String name() {
            return ">";
        }

        @Override
        public LObj execute(Environment env) {
            Boolean ret = true;
            Float x = env.state.get(0).getValue().getObj().toFloat();
            Float y = env.state.get(1).getValue().getObj().toFloat();
            
            return boolToLObj(x > y);
        }

    }
    
    public static class BIFLt extends BuiltIn {

        @Override
        public String name() {
            return "<";
        }

        @Override
        public LObj execute(Environment env) {
            Boolean ret = true;
            Float x = env.state.get(0).getValue().getObj().toFloat();
            Float y = env.state.get(1).getValue().getObj().toFloat();
            
            return boolToLObj(x < y);
        }

    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Compare 2 of the same";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(= 5 5)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Compare 2 different";
                    return MISPTests.compareLObj("false", env.execute(new LObj("(= 5 5.1)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Compare many of the same";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(= 5 5 5 5 5)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Compare result of 3 function calls";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(= (* 2.5 2) (+ 2 3) (- 7 2))")));
                }
            });
        return tests;
    }
    
}
