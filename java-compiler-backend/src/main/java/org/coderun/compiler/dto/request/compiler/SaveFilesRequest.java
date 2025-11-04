package org.coderun.compiler.dto.request.compiler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Compilation request containing source code and metadata")
public class SaveSourceCodeRequest {
    private String filename;
    private String code;
}


