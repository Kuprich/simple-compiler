package org.coderun.compiler.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StopExecutionRequest {
    private String containerId;
}