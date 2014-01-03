/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

/**
 *
 * @author spine
 */
public class ParseError extends Exception {
    private char expected;

    public ParseError(char expected) {
        this.expected = expected;
    }

    public char getExpected() {
        return expected;
    }
}
