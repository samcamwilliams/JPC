/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author spine
 */
public class MISPTests {
    
    public int execute() {
        System.out.println("   === STARTING TESTS === ");
        String buffer;
        int i = 1;
        int failures = 0;
        for(Test t : allTests()) {
            Environment env = new Environment();
            Boolean res;
            
            try {
                res = t.run(env);
            } catch (MISPException ex) {
                res = false;
                Logger.getLogger(MISPTests.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            buffer = i + ": " + t.title + "... ";
            
            if(res) {
                buffer += "PASSED\n";
            }
            else {
                buffer += "FAILED\n";
                failures++;
            }
            System.out.print(buffer);
            i++;
        }
        i--;
        
        System.out.println("   === FINISHED TESTS === ");
        System.out.println((i - failures) + " successes, " + failures + " failures.");
        return failures;
    }
    
    private ArrayList<Test> allTests() {
        ArrayList<Test> tests = new ArrayList<>();
        
        
        // Execution Environment Tests
        tests.addAll(Environment.getTests());
        tests.addAll(LObj.getTests());
        
        // Built In Function Tests
        tests.addAll(BIFArith.getTests());
        tests.addAll(BIFComp.getTests());
        tests.addAll(BIFCtrl.getTests());
        tests.addAll(BIFLogic.getTests());
        tests.addAll(BIFList.getTests());
        tests.addAll(BIFIO.getTests());
        tests.addAll(BIFSystem.getTests());
        tests.addAll(LibraryTests.getTests());
        tests.addAll(BIFMPD.getTests());
        
        return tests;
    }
    
    public static Boolean compareLObj(Object o, LObj obj) {
        if(obj.isLiteral()) {
            
            if(obj.getObj().get().equals(o))
                return true;
            else
                return false;
        }
        else {
            if(obj.toString().equals(o))
                return true;
            else
                return false;
        }
    }
}
