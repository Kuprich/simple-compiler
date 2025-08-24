package org.coderun.compiler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Compilation request containing source code and metadata")
public class CompileRequest {

    @Schema(
            description = "Name of the source file with extension",
            example = "Main.java",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String filename;

    @Schema(
            description = "Java source code to compile and execute",
            example = "public class Main { public static void main(String[] args) { System.out.println(\"Hello World?\"); } }",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;

}


