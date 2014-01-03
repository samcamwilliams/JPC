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
public class BIFLogic {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        bifs.add(new Variable(new BIFNot()));
        bifs.add(new Variable(new BIFOr()));
        bifs.add(new Variable(new BIFAnd()));
        bifs.add(new Variable(new BIFXor()));
        
        return bifs;
    }
    
    public static class BIFNot extends BuiltIn {

        @Override
        public String name() {
            return "not";
        }

        @Override
        public LObj execute(Environment env) {
            return boolToLObj(
                    !LObjToBool(
                        env.state.get(0).getValue()));
        }
    }
    
    public static class BIFAnd extends BuiltIn {

        @Override
        public String name() {
            return "and";
        }

        @Override
        public LObj execute(Environment env) {
            if(LObjToBool(env.state.get(0).getValue()) &&
                    LObjToBool(env.state.get(1).getValue())) {
                return boolToLObj(true);
            }
            else {
                return boolToLObj(false);
            }
        }
    }
    
    public static class BIFOr extends BuiltIn {

        @Override
        public String name() {
            return "or";
        }

        @Override
        public LObj execute(Environment env) {
            if(LObjToBool(env.state.get(0).getValue()) ||
                    LObjToBool(env.state.get(1).getValue())) {
                return boolToLObj(true);
            }
            else {
                return boolToLObj(false);
            }
        }
    }
    
    public static class BIFXor extends BuiltIn {

        @Override
        public String name() {
            return "xor";
        }

        @Override
        public LObj execute(Environment env) {
            if(LObjToBool(env.state.get(0).getValue()) ^
                    LObjToBool(env.state.get(1).getValue())) {
                return boolToLObj(true);
            }
            else {
                return boolToLObj(false);
            }
        }
    }
    
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<Test>();
        

        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Not of true";
                    return MISPTests.compareLObj("false", env.execute(new LObj("(not true)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Not of false";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(not false)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Not of execution";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(not (= 120 70))")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Or of true true";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(or true true)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Or of true false";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(or true false)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Or of false false";
                    return MISPTests.compareLObj("false", env.execute(new LObj("(or false false)")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Xor of true true";
                    return MISPTests.compareLObj("false", env.execute(new LObj("(xor true true)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Xor of true false";
                    return MISPTests.compareLObj("true", env.execute(new LObj("(xor true false)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Xor of false false";
                    return MISPTests.compareLObj("false", env.execute(new LObj("(xor false false)")));
                }
            });
        return tests;
    }
}
