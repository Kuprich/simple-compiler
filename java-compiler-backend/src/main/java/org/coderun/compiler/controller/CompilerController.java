package org.coderun.compiler.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.coderun.compiler.dto.request.CompileRequest;
import org.coderun.compiler.dto.response.CompileResponse;
import org.coderun.compiler.dto.request.StopExecutionRequest;
import org.coderun.compiler.service.CompilerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin(origins = "*")
public class CompilerController {

    private final CompilerService compilerService;

    public CompilerController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }

    @PostMapping("/run")
    @Operation(
            summary = "Compile and execute Java code",
            description = "Compiles Java source code and executes it in a Docker container"
    )
    public CompileResponse compileAndRun(@RequestBody CompileRequest request) {
        return compilerService.compileAndRun(request);
    }

    @PostMapping("/stopExecution")
    @Operation(
            summary = "",
            description = ""
    )
    public CompileResponse StopExecution(@RequestBody StopExecutionRequest request) {
        return compilerService.StopExecution(request);
    }
}
