/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MISP;

/**
 *
 * @author spine
 */
public class MISPException extends Exception {
    private String reason;
    private StackTrace trace;
    
    public MISPException(String reason) {
        this.reason = reason;
        this.trace = new StackTrace();
    }

    public String getReason() {
        return reason;
    }

    public StackTrace getTrace() {
        return trace;
    }

    public void setTrace(StackTrace trace) {
        this.trace = trace;
    }
    
}
