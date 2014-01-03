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
public class BIFCtrl {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<Variable>();
        bifs.add(new Variable(new BIFIf()));
        
        return bifs;
    }
    
    public static class BIFIf extends BuiltIn {

        @Override
        public String name() {
            return "if";
        }

        @Override
        public Boolean isConditional() {
            return true;
        }
        
        @Override
        public LObj execute(Environment env, Environment global) throws MISPException {
            if(LObjToBool(global.handle_execute(env.state.get(0).getValue())))
                return global.handle_execute(env.state.get(1).getValue());
            else
                return global.handle_execute(env.state.get(2).getValue());
        }

    }
    
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<Test>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Use if on literal true";
                    return MISPTests.compareLObj(1, env.execute(new LObj("(if true 1 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Use if on equality";
                    return MISPTests.compareLObj(2, env.execute(new LObj("(if (= 1 1) 2 3)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Use if on equality and execute expression";
                    LObj x = env.execute(new LObj("(if (= 1 1) (- 3 1) 3)"));
                    return MISPTests.compareLObj(2, x);
                }
            });
        
        return tests;
    }
}
