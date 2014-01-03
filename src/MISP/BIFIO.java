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
public class BIFIO {
    public static ArrayList<Variable> getAllVars() {
        ArrayList<Variable> bifs = new ArrayList<>();
        
        return bifs;
    }
    
    public static ArrayList<Test> getTests() {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(
            new Test() {
            @Override
                public Boolean run(Environment env) {
                    title = "Include the header";
                    LObj x = env.execute(new LObj("author"));
                    return MISPTests.compareLObj("secw", x);
                }
            });
        
        return tests;
    }
}
