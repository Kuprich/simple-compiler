package org.coderun.compiler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.coderun.compiler.dto.CompileRequest;
import org.coderun.compiler.dto.CompileResponse;
import org.coderun.compiler.service.CompilerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin(origins = "*")
@Tag(name = "Compiler", description = "Java code compilation and execution API")
public class CompilerController {

    private final CompilerService service;

    public CompilerController(CompilerService service) {
        this.service = service;
    }

    @PostMapping("/run")
    @Operation(
            summary = "Compile and execute Java code",
            description = "Compiles Java source code and executes it in a Docker container",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful compilation and execution"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public CompileResponse compileAndRun(@RequestBody CompileRequest request) {
        return service.compileAndRun(request);
    }
}
