package org.coderun.compiler.controller;

import org.coderun.compiler.dto.CompileRequest;
import org.coderun.compiler.dto.CompileResponse;
import org.coderun.compiler.service.CompilerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin(origins = "*")
public class CompilerController {

    private final CompilerService service;

    public CompilerController(CompilerService service) {
        this.service = service;
    }

    @PostMapping("/run")
    public CompileResponse compileAndRun(@RequestBody CompileRequest request) {
        return service.compileAndRun(request);
    }
}
