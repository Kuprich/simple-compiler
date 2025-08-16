package org.coderun.compiler.dto;

public class CompileResponse {
    private boolean success;
    private String logs;
    private String containerId;

    public CompileResponse(boolean success, String logs, String containerId) {
        this.success = success;
        this.logs = logs;
        this.containerId = containerId;
    }

    // getters
    public boolean isSuccess() { return success; }
    public String getLogs() { return logs; }
    public String getContainerId() { return containerId; }
}
