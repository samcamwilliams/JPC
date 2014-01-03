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
public class BIFArith {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        bifs.add(new Variable(new BIFAdd()));
        bifs.add(new Variable(new BIFSub()));
        bifs.add(new Variable(new BIFDiv()));
        bifs.add(new Variable(new BIFMul()));
        bifs.add(new Variable(new BIFMod()));
        bifs.add(new Variable(new BIFAbs()));
        
        return bifs;
    }
    
    public static class BIFAdd extends BuiltIn {

        @Override
        public String name() {
            return "+";
        }

        @Override
        public LObj execute(Environment env) {
            Float x = 0f;

            for(Variable var: env.state)
                x += var.getValue().getObj().toFloat();
            
            return floatToLObj(x);
        }

    }
    
    public static class BIFMul extends BuiltIn {

        @Override
        public String name() {
            return "*";
        }

        @Override
        public LObj execute(Environment env) {
            Float x = env.state.get(0).getValue().getObj().toFloat();

            for(int i = 1; i < env.state.size(); i++) {
                x *= env.state.get(i).getValue().getObj().toFloat();
            }
            
            return floatToLObj(x);
        }

    }
    
    public static class BIFSub extends BuiltIn {

        @Override
        public String name() {
            return "-";
        }

        @Override
        public LObj execute(Environment env) {
            if(env.state.size() == 1) {
                return floatToLObj(-env.state.get(0).getValue().getObj().toFloat());
            }
            
            Float x = env.state.get(0).getValue().getObj().toFloat();

            for(int i = 1; i < env.state.size(); i++) {
                x -= env.state.get(i).getValue().getObj().toFloat();
            }
            
            return floatToLObj(x);
        }

    }
    
    public static class BIFDiv extends BuiltIn {

        @Override
        public String name() {
            return "/";
        }

        @Override
        public LObj execute(Environment env) {
            Float x = env.state.get(0).getValue().getObj().toFloat();

            for(int i = 1; i < env.state.size(); i++) {
                x /= env.state.get(i).getValue().getObj().toFloat();
            }
            
            return floatToLObj(x);
        }

    }
    
    public static class BIFMod extends BuiltIn {

        @Override
        public String name() {
            return "%";
        }

        @Override
        public LObj execute(Environment env) {
            Float x = env.state.get(0).getValue().getObj().toFloat();

            for(int i = 1; i < env.state.size(); i++) {
                x %= env.state.get(i).getValue().getObj().toFloat();
            }
            
            return floatToLObj(x);
        }

    }
    
    public static class BIFAbs extends BuiltIn {

        @Override
        public String name() {
            return "abs";
        }

        @Override
        public LObj execute(Environment env) {
            return floatToLObj(Math.abs(env.state.get(0).getValue().getObj().toFloat()));
        }

    }

    private static LObj floatToLObj(Float x) {
        if(isInt(x))
            return new LObj(toInt(x));
        else
            return new LObj(x);
    }
    
    private static Boolean isInt(Float i) {
        return i == ((float)Math.round(i));
    }
    
    private static int toInt(Float i) {
        return Math.round(i);
    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<Test>();
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Basic addition";
                    return MISPTests.compareLObj(3, env.execute(new LObj("(+ 1 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Nested addition";
                    return MISPTests.compareLObj(9, env.execute(new LObj("(+ 1 (+ 3 5))")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Subtraction of 2 values";
                    return MISPTests.compareLObj(26, env.execute(new LObj("(- 30 4)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Subtraction of many values";
                    return MISPTests.compareLObj(21, env.execute(new LObj("(- 30 4 2 2 1)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Subtraction of a float";
                    return MISPTests.compareLObj(1.5f, env.execute(new LObj("(- 3 1.5)")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Division of 2 integers";
                    return MISPTests.compareLObj(15, env.execute(new LObj("(/ 30 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Division of many integers";
                    return MISPTests.compareLObj(1, env.execute(new LObj("(/ 6 3 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Division creating a float";
                    return MISPTests.compareLObj(1.5f, env.execute(new LObj("(/ 3 2)")));
                }
            });
        
        
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Multiplication of 2 integers";
                    return MISPTests.compareLObj(60, env.execute(new LObj("(* 30 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Multiplication of many integers";
                    return MISPTests.compareLObj(120, env.execute(new LObj("(* 2 30 2)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Multiplication of a float";
                    return MISPTests.compareLObj(6, env.execute(new LObj("(* 1.2 5)")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Factorial example program";
                    LObj x = env.execute(new LObj("(factorial 5)"));
                    return MISPTests.compareLObj(120, x);
                }
            });
        return tests;
    }
    
}
