package org.bool.lunch.scalecube;

public class ProcessTerminatedException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private final String pid;
    
    private final int exitCode;
    
    public ProcessTerminatedException(String pid, int exitCode) {
        this(pid, exitCode, "Process " + pid + " terminated with code " + exitCode);
    }
    
    public ProcessTerminatedException(String pid, int exitCode, String message) {
        super(message);
        this.pid = pid;
        this.exitCode = exitCode;
    }
    
    public String getPid() {
        return pid;
    }

    public int getExitCode() {
        return exitCode;
    }
}
