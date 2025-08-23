package org.coderun.compiler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CompileResponse {

    @Schema(
            description = "Indicates whether compilation and execution were successful",
            example = "true"
    )
    private final boolean success;

    @Schema(
            description = "Output logs from compilation and execution process",
            example = "Hello World?\\n"
    )
    private final String logs;

    @Schema(
            description = "Docker container identifier used for execution",
            example = "87a925a635001e3043f526903c46f3d5816bfd735ab8c574adcd81e2b0043d2b"
    )
    private final String containerId;

    public CompileResponse(boolean success, String logs, String containerId) {
        this.success = success;
        this.logs = logs;
        this.containerId = containerId;
    }

}
