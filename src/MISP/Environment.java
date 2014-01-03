/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author spine
 */
public final class Environment {
    public ArrayList<Variable> state;
    public StackTrace trace;
    
    public Environment() {
        ArrayList<Variable> vars = new ArrayList<>();
        vars.addAll(BIFArith.getAllVars());
        vars.addAll(BIFComp.getAllVars());
        vars.addAll(BIFLogic.getAllVars());
        vars.addAll(BIFCtrl.getAllVars());
        vars.addAll(BIFList.getAllVars());
        vars.addAll(BIFIO.getAllVars());
        vars.addAll(BIFMPD.getAllVars());
        vars.addAll(BIFSystem.getAllVars());
        this.state = vars;
        this.trace = new StackTrace();
        
        this.execute(new LObj("(include 'LIB/header.scm')"));
    }
    
    public void AddBIFs(ArrayList<Variable> bifs) {
        state.addAll(bifs);
    }
    
    public Environment(ArrayList<Variable> state) {
        ArrayList<Variable> vars = new ArrayList<>();
        for(Variable var: state) vars.add(var);
        this.state = vars;
        this.trace = new StackTrace();
    }
    
    public Environment(Environment env) {
        ArrayList<Variable> vars = new ArrayList<>();
        for(Variable var: env.state) vars.add(var);
        this.state = vars;
        this.trace = env.trace.clone();
    }
    
    public LObj execute(LObj obj) {
        try {
            return unsafe_execute(obj);
        } catch (MISPException ex) {
            ex.setTrace(trace);
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
            return new LObj("(exception '" + ex.getReason() + "')");
        }
    }
    
    public LObj unsafe_execute(LObj obj) throws MISPException {
        trace.add(obj);
        if(obj.isLiteral()) {
            obj = substituteVar(obj);
            if (obj.getObj().getType() == Literal.Types.BUILTIN) {
                if(!((BuiltIn)obj.getObj().get()).hasArgs())
                    return handle_execute(((BuiltIn)obj.getObj().get()));
                else
                    return obj;
            }
            else {
                return obj;
            }
        }
        else if(obj.getList().size() == 2) {
            if(obj.getList().get(0).toString().equals("include")) {
                return this.include((String) obj.getList().get(1).getObj().get());
            }
        }
        
        ArrayList<LObj> objs = new ArrayList<>();
        
        for(LObj o: obj.getList())
            objs.add(o);
        
        for(int i = 0; i < objs.size(); i++) {
            LObj tobj = substituteVar(objs.get(i));
            objs.set(i, tobj);
        }
        
        if(objs.isEmpty()) return obj;
        
        LObj head = objs.get(0);
        
        if(!head.isLiteral()) {
            LObj sec_head = head.getList().get(0);
            if(sec_head.isLiteral()) {
                if(sec_head.getObj().get().equals("lambda") && sec_head.getObj().getType() == Literal.Types.ATOM) {
                    ArrayList<LObj> lambda_objs = head.getList();
                    Environment lambda_env = new Environment(this);
                    
                    if(lambda_objs.get(1).isLiteral()) {
                        LObj arg = new LObj("()");
                        
                        for(int i = 0; i < objs.size() - 1; i++)
                            arg.addLObj(this.handle_execute(objs.get(i + 1)));
                        
                        int pos = findVarPos(lambda_env.state, lambda_objs.get(1).toString());
                        if(pos != -1) lambda_env.state.remove(pos);
                        
                        setVariable(lambda_env,
                            new Variable(lambda_objs.get(1).toString(), arg));
                    }
                    else {
                        ArrayList<LObj> var_names = lambda_objs.get(1).getList();

                        for(int i = 0; i < var_names.size(); i++) {
                            int pos = findVarPos(lambda_env.state, var_names.get(i).toString());
                            if(pos != -1) lambda_env.state.remove(pos);
                            setVariable(
                                lambda_env,
                                new Variable(
                                    var_names.get(i).toString(),
                                    this.handle_execute(objs.get(i + 1))
                                ));
                        }
                    }
                    
                    return lambda_env.handle_execute(lambda_objs.get(2));
                }
            }
        }
        else {
            if(head.getObj().getType() == Literal.Types.ATOM) {
                String atom = head.getObj().toString();
                
                if(atom.equals("define")) {
                    setVariable(new Variable(((String)objs.get(1).getObj().toString()), this.handle_execute(objs.get(2))));
                    return objs.get(1);
                }
                else if(atom.equals("begin")) {
                    // Imperatively handle_execute all expressions in sequence
                    Environment env = new Environment(this);
                    for(int i = 0; i < objs.size() - 1; i++) {
                        env.handle_execute(objs.get(i));
                    }
                    // Return the result of the last execution
                    return env.handle_execute(objs.get(objs.size() - 1));
                }
                else if(atom.equals("eval")) {
                    Environment env = new Environment(this);
                    return env.handle_execute(env.handle_execute(objs.get(1)));
                }
                else if(atom.equals("exception")) {
                    throw new MISPException(this.handle_execute(objs.get(1)).toUnwrappedString());
                }
            }
            else if(head.getObj().getType() == Literal.Types.BUILTIN) {
                BuiltIn bif = (BuiltIn)head.getObj().get();
                Environment env = new Environment(new ArrayList<Variable>());
                
                for(int i = 1; i < objs.size(); i++) {
                    if(bif.isConditional()) {
                        setVariable(env, new Variable(Integer.toString(i), objs.get(i)));
                    }
                    else {
                        setVariable(env, new Variable(Integer.toString(i), this.handle_execute(objs.get(i))));
                    }
                }
                
                return handle_execute(bif, env, new Environment(this));
            }
            // For records or something?
            return obj;
        }
        return obj;
    }

    public LObj handle_execute(BuiltIn builtIn) throws MISPException {
        try {
            return builtIn.execute(this);
        } catch (MISPException ex) {
            ex.setTrace(trace);
            throw ex;
        }
    }

    public LObj handle_execute(BuiltIn builtIn, Environment vars, Environment global) throws MISPException {
        try {
            return builtIn.execute(vars, global);
        } catch (MISPException ex) {
            ex.setTrace(trace);
            throw ex;
        }
    }

    public LObj handle_execute(LObj obj) throws MISPException {
        try {
            return unsafe_execute(obj);
        } catch (MISPException ex) {
            ex.setTrace(trace);
            throw ex;
        }
    }
    
    private void setVariable(Variable var) {
        setVariable(this, var);
    }

    static private void setVariable(Environment env, Variable variable) {
        int index = findVarPos(env.state, variable.getName());
        
        if(index != -1) {
            env.state.remove(index);
        }
        
        env.state.add(variable);
    }
    
    static private int findVarPos(ArrayList<Variable> vars, String name) {
        int pos = -1;
        for(int i = 0; i < vars.size(); i++)
            if(vars.get(i).getName().equals(name)) pos = i;
        return pos;
    }
    
    private LObj substituteVar(LObj obj) {
        if(obj.isLiteral()) {
            Literal x = obj.getObj();
            if(x.getType() == Literal.Types.ATOM && !x.isConstant()) {
                Variable var = this.getVariable(x.toString());
                if(var != null && var.getValue() != obj)
                    return substituteVar(var.getValue());
            }
        }

        return obj;
    }
            
    
    public Variable getVariable(String name) {
        for(Variable var: state) {
            if(var.getName().equals(name)) {
                return var;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        String out = "[";
        
        for(Variable var: state)
            out += var.toString() + ", ";
        
        if(this.state.size() > 0)
            out = out.substring(0, out.length()-2);
        
        out += "]";
        return out;
    }
    
    public ArrayList<Variable> getState() {
        return state;
    }
    
    public LObj include(String location) {
        String text = "";
        
        try {
            Path path = Paths.get(location);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            for(String line: lines) text += line + "\n";

        } catch (IOException ex) {
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<LObj> list = (new LObj("(" + text + ")")).getList();

        ArrayList<LObj> responses = new ArrayList<>();

        for(LObj cmd: list) responses.add(this.execute(cmd));

        return new LObj(responses);
    }
    
    public static ArrayList<Test> getTests() {
        
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Assign and recall a variable";
                    env.execute(new LObj("(define x 3)"));
                    return MISPTests.compareLObj(3, env.execute(new LObj("x")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Assign and use a variable";
                    env.execute(new LObj("(define x 3)"));
                    return MISPTests.compareLObj(6, env.execute(new LObj("(+ 3 x)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Use begin to execute imperative statements";
                    return MISPTests.compareLObj(6, env.execute(new LObj("(begin (define x 3) (+ 3 x))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Define and use lambda expression";
                    return MISPTests.compareLObj(52, env.execute(new LObj("((lambda (x) (+ x 42)) 10)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Define, store, then use lambda expression";
                    env.execute(new LObj("(define add42 (lambda (x) (+ x 42)))"));
                    return MISPTests.compareLObj(49, env.execute(new LObj("(add42 7)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Store lambda, recall in another lambda";
                    env.execute(new LObj("(define add42 (lambda (x) (+ x 42)))"));
                    env.execute(new LObj("(define add50 (lambda (y) (+ y (add42 8))))"));
                    return MISPTests.compareLObj(57, env.execute(new LObj("(add50 7)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Use lambda not yet stored";
                    env.execute(new LObj("(define add50 (lambda (y) (+ y (add42 8))))"));
                    env.execute(new LObj("(define add42 (lambda (x) (+ x 42)))"));
                    return MISPTests.compareLObj(57, env.execute(new LObj("(add50 7)")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Variables are correctly scoped";
                    env.execute(new LObj("(define add2 (lambda (x) (+ x 2)))"));
                    env.execute(new LObj("(define add1 (lambda (x) (+ x 1)))"));
                    return MISPTests.compareLObj(63, env.execute(new LObj("(+ (add1 10) (add2 50))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Shadowed variable uses local, not global";
                    env.execute(new LObj("(define x 20)"));
                    env.execute(new LObj("(define add1 (lambda (x) (+ x 1)))"));
                    return MISPTests.compareLObj(2, env.execute(new LObj("(add1 1)"))) && MISPTests.compareLObj(20, env.execute(new LObj("x")));
                }
            });
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Recursive addition";
                    env.execute(new LObj("(define recAdd (lambda (y) (if (= y 1000) y (recAdd (+ 1 y)))))"));
                   return MISPTests.compareLObj(1000, env.execute(new LObj("(recAdd 0)")));
                }
            });
        /*tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Recursive cons";
                    env.execute(new LObj("(define recAdd (lambda (y) (if (= y 2000) y (recAdd (+ 1 y)))))"));
                   return MISPTests.compareLObj(2000, env.execute(new LObj("(recAdd 0)")));
                }
            });*/
        return tests;
    }

    public void resetTrace() {
        trace = new StackTrace();
    }
    
}
