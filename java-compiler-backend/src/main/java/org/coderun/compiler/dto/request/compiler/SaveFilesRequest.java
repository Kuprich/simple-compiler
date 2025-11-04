package org.coderun.compiler.dto.request.compiler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.coderun.compiler.dto.SourceCodeFile;

@Setter
@Getter
@Schema(description = "Compilation request containing source code and metadata")
public class SaveFilesRequest {
    private SourceCodeFile[] files;
}


