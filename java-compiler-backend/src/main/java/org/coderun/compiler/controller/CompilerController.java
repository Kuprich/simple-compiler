package org.coderun.compiler.controller;

import org.coderun.compiler.dto.request.compiler.*;
import org.coderun.compiler.dto.response.ApiResponse;
import org.coderun.compiler.service.CompilerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@CrossOrigin(origins = "*")
public class CompilerController {

    private final CompilerService compilerService;

    public CompilerController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }

    // 1 -> save code to dir
    @PostMapping("/save")
    public ApiResponse<String> saveFiles(@RequestBody SaveFilesRequest request) {
        return compilerService.saveFiles(request);
    }

    // 2 -> pull docker image
    @GetMapping("/pullImage")
    public ApiResponse<String> pullImage() {
        return compilerService.pullImage();
    }

    // 3 -> prepare and start image
    @PostMapping("/prepare")
    public ApiResponse<String> prepareImageAndRun(@RequestBody PrepareRequest request) {
        return compilerService.prepareImageAndRun(request);
    }

    // 4 -> execute container with timeout
    @PostMapping("/execute")
    public ApiResponse<Integer> executeWithTimeout(@RequestBody ExecuteRequest request) {
        return compilerService.executeWithTimeout(request);
    }

    // 5 -> collect logs
    @PostMapping("/collectLogs")
    public ApiResponse<String> collectLogs(@RequestBody CollectLogsRequest request) {
        return compilerService.collectLogs(request);
    }

    // 6 -> cleanup execution
    @PostMapping("/cleanup")
    public ApiResponse<Void> cleanupExecution(@RequestBody CleanupExecutionRequest request) {
        return compilerService.cleanupExecution(request);
    }
}


