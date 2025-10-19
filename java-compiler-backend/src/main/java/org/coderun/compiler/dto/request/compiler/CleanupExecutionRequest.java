package org.coderun.compiler.dto.request.compiler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CleanupExecutionRequest {
    private String containerId;
    private String codeDir;
}