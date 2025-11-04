package org.coderun.compiler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SourceCodeFile {
    private String filename;
    private String code;
}