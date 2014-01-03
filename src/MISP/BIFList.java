/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author spine
 */
public class BIFList {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        bifs.add(new Variable(new BIFCons()));
        bifs.add(new Variable(new BIFMakeList()));
        bifs.add(new Variable(new BIFAppend()));
        bifs.add(new Variable(new BIFHead()));
        bifs.add(new Variable(new BIFTail()));
        bifs.add(new Variable(new BIFElement()));
        bifs.add(new Variable(new BIFLength()));
        bifs.add(new Variable(new BIFMap()));
        bifs.add(new Variable(new BIFFold()));
        bifs.add(new Variable(new BIFReverse()));
        
        return bifs;
    }
    
    public static class BIFMakeList extends BuiltIn {

        @Override
        public String name() {
            return "list";
        }

        @Override
        public LObj execute(Environment env) {
            ArrayList<LObj> objs = new ArrayList<>();
            
            for(Variable var: env.state)
                objs.add(var.getValue());
            
            return new LObj(objs);
        }
    }
    
    public static class BIFCons extends BuiltIn {

        @Override
        public String name() {
            return "cons";
        }

        @Override
        public LObj execute(Environment env) {
            ArrayList<LObj> objs = new ArrayList<>();
            
            for(Variable var: env.state)
                objs.add(var.getValue());
            
            return new LObj(objs);
        }
    }
    
    public static class BIFAppend extends BuiltIn {

        @Override
        public String name() {
            return "append";
        }

        @Override
        public LObj execute(Environment env) {
            ArrayList<LObj> objs = new ArrayList<>();
            
            for(Variable var: env.state)
                objs.addAll(var.getValue().getList());
            
            return new LObj(objs);
        }
    }
    
    public static class BIFHead extends BuiltIn {

        @Override
        public String name() {
            return "head";
        }

        @Override
        public LObj execute(Environment env) {
            return env.state.get(0).getValue().getList().get(0);
        }
    }
    
    public static class BIFTail extends BuiltIn {

        @Override
        public String name() {
            return "tail";
        }

        @Override
        public LObj execute(Environment env) {
            ArrayList<LObj> objs = new ArrayList<>();
            ArrayList<LObj> list = env.state.get(0).getValue().getList();
            for(int i = 1; i < list.size(); i++) {
                objs.add(list.get(i));
            }
           
            return new LObj(objs);
        }
    }
    
    public static class BIFElement extends BuiltIn {

        @Override
        public String name() {
            return "element";
        }

        @Override
        public LObj execute(Environment env) throws MISPException {
            int index = env.state.get(0).getValue().getObj().toInt();
            
            ArrayList<LObj> objs = env.state.get(1).getValue().getList();
            
            if(index > objs.size())
                throw new MISPException("Index "
                    + index + " out of bounds in list "
                    + env.state.get(1).getValue().toString()
                    + ".");
            
            return objs.get(index - 1);
        }
    }
    
    public static class BIFLength extends BuiltIn {

        @Override
        public String name() {
            return "length";
        }

        @Override
        public LObj execute(Environment env) {
            int size = env.state.get(0).getValue().getList().size();
            return new LObj(size);
        }
    }
    
    public static class BIFMap extends BuiltIn {

        @Override
        public String name() {
            return "map";
        }

        @Override
        public LObj execute(Environment env, Environment global) throws MISPException {
            LObj fun = env.state.get(0).getValue();
            ArrayList<LObj> list = env.state.get(1).getValue().getList();
            
            for(int i = 0; i < list.size(); i++) {
                ArrayList<LObj> y = new ArrayList<>();
                y.add(fun);
                y.add(list.get(i));
                list.set(i, global.handle_execute(new LObj(y)));
            }
            
            return new LObj(list);
        }
    }
    
    public static class BIFFold extends BuiltIn {

        @Override
        public String name() {
            return "fold";
        }

        @Override
        public LObj execute(Environment env, Environment global) throws MISPException {
            LObj fun = env.state.get(0).getValue();
            LObj acc = env.state.get(1).getValue();
            ArrayList<LObj> list = env.state.get(2).getValue().getList();
            
            for(int i = 0; i < list.size(); i++) {
                ArrayList<LObj> y = new ArrayList<>();
                y.add(fun);
                y.add(list.get(i));
                y.add(acc);
                acc = global.handle_execute(new LObj(y));
            }
            
            return acc;
        }
    }
    
    public static class BIFReverse extends BuiltIn {

        @Override
        public String name() {
            return "reverse";
        }

        @Override
        public LObj execute(Environment env, Environment global) {
            ArrayList<LObj> list = env.state.get(0).getValue().getList();
            
            Collections.reverse(list);
            
            return new LObj(list);
        }
    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Create simple list";
                    LObj x = env.execute(new LObj("(list a b)"));
                    return MISPTests.compareLObj("(a b)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Cons of 2 terms";
                    LObj x = env.execute(new LObj("(cons a b)"));
                    return MISPTests.compareLObj("(a b)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Cons of complex terms";
                    LObj x = env.execute(new LObj("(cons a (+ 1 2 3) (* 6 2))"));
                    return MISPTests.compareLObj("(a 6 12)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Cons of many complex terms";
                    LObj x = env.execute(new LObj("(cons a (1 2 3) (* 6 2) (* (/ 6 2) 2))"));
                    return MISPTests.compareLObj("(a (1 2 3) 12 6)", x);
                }
            });
        
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Append 2 simple lists";
                    LObj x = env.execute(new LObj("(append (1 2 3) (4 5 6))"));
                    return MISPTests.compareLObj("(1 2 3 4 5 6)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Append complex lists";
                    LObj x = env.execute(new LObj("(append (1 2 3) (4 5 6) (7 8 9 (5 5)))"));
                    return MISPTests.compareLObj("(1 2 3 4 5 6 7 8 9 (5 5))", x);
                }
            });
        
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Append 2 simple lists";
                    LObj x = env.execute(new LObj("(append (1 2 3) (4 5 6))"));
                    return MISPTests.compareLObj("(1 2 3 4 5 6)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Append complex lists";
                    LObj x = env.execute(new LObj("(append (1 2 3) (4 5 6) (7 8 9 (5 5)))"));
                    return MISPTests.compareLObj("(1 2 3 4 5 6 7 8 9 (5 5))", x);
                }
            });
        
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Head of a list";
                    LObj x = env.execute(new LObj("(head (1 2 3 4))"));
                    return MISPTests.compareLObj(1, x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Tail of a list";
                    LObj x = env.execute(new LObj("(tail (1 2 3))"));
                    return MISPTests.compareLObj("(2 3)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get an element of a list";
                    LObj x = env.execute(new LObj("(element 2 (1 2 3 4 5 6 7 8 9 10))"));
                    return MISPTests.compareLObj(2, x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get length of long list";
                    LObj x = env.execute(new LObj("(length (1 2 3 4 5 6 7 8 9 10))"));
                    return MISPTests.compareLObj(10, x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get length of short list";
                    LObj x = env.execute(new LObj("(length (1))"));
                    return MISPTests.compareLObj(1, x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get length of a zero length list";
                    LObj x = env.execute(new LObj("(length ())"));
                    return MISPTests.compareLObj(0, x);
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Perform simple map";
                    LObj x = env.execute(new LObj("(map (lambda (x) (* x 2)) (1 2 3 4))"));
                    return MISPTests.compareLObj("(2 4 6 8)", x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Perform map on stored procedure";
                    env.execute(new LObj("(define add20 (lambda (x) (+ 20 x)))"));
                    LObj x = env.execute(new LObj("(map add20 (1 2 3 4))"));
                    return MISPTests.compareLObj("(21 22 23 24)", x);
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Perform simple fold";
                    LObj x = env.execute(new LObj("(fold (lambda (x acc) (* x acc)) 1 (1 2 3 4))"));
                    return MISPTests.compareLObj(24, x);
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Perform fold on stored procedure";
                    LObj x = env.execute(new LObj("(fold + 0 (1 2 3 4))"));
                    return MISPTests.compareLObj(10, x);
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Reverse a simple list";
                    LObj x = env.execute(new LObj("(reverse (1 2 3))"));
                    return MISPTests.compareLObj("(3 2 1)", x);
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Run seq from inside the header";
                    LObj x = env.execute(new LObj("(seq 1 10)"));
                    return MISPTests.compareLObj("(1 2 3 4 5 6 7 8 9 10)", x);
                }
            });
        
        return tests;
    }
}
