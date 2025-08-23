package org.coderun.compiler.dto;

public class CompileRequest {
    private String filename;
    private String code;

    // getters/setters
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}


