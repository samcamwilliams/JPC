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
public class LibraryTests {
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get from dict";
                    return MISPTests.compareLObj(6, env.execute(new LObj("(get 3 ((1 2) (2 4) (3 6)))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Get undefined from dict";
                    return MISPTests.compareLObj("undefined", env.execute(new LObj("(get 4 ((1 2) (2 4) (3 6)))")));
                }
            });
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Set value in dict";
                    return MISPTests.compareLObj("a", env.execute(new LObj("(get 2 (set 2 a ((1 2) (2 4) (3 6))))")));
                }
            });
        
        return tests;
    }
}
