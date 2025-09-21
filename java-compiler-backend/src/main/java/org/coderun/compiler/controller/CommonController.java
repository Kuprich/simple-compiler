package org.coderun.compiler.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.coderun.compiler.dto.CompileRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@CrossOrigin(origins = "*")
public class CommonController {
    @PostMapping("/languages")
    @Operation(
            summary = "Get languages available for compilation",
            description = "")
    public String compileAndRun(@RequestBody CompileRequest request) {
        return null;
    }
}
