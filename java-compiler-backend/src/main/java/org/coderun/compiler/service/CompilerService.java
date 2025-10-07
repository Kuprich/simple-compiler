package org.coderun.compiler.service;

import com.github.dockerjava.api.model.Bind;
import lombok.extern.slf4j.Slf4j;
import org.coderun.compiler.dto.request.compiler.*;
import org.coderun.compiler.dto.response.ApiResponse;
import org.coderun.compiler.service.internal.CommandBuilderService;
import org.coderun.compiler.service.internal.DockerService;
import org.coderun.compiler.service.internal.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class CompilerService {

    @Value("${compiler.java.image}")
    private String dockerImage;

    @Value("${compiler.timeout.seconds}")
    private int timeoutSeconds;

    @Autowired
    private FileService fileService;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private CommandBuilderService commandBuilder;

//    public CompileResponse compileAndRun(CompileRequest request) {
//        String containerId = null;
//        try {
//            // 1. Save code to file
//            fileService.saveSourceCode(request.getFilename(), request.getCode());
//
//            // 2. Prepare Docker image
//            dockerService.pullImageIfNeeded(dockerImage);
//
//            // 3. Create and run docker image
//            String command = commandBuilder.buildJavaCompileAndRunCommand(request.getFilename());
//            Bind bind = commandBuilder.createBind(fileService.getHostCodeDir());
//            containerId = dockerService.createAndStartContainer(dockerImage, command, bind);
//
//            // 4. Run with timeout
//            int executionStatusCode = executeWithTimeout_v1(containerId);
//
//            // 5. Collect logs (using DockerService)
//            String containerLogs = dockerService.getContainerLogs(containerId);
//
//            return new CompileResponse(executionStatusCode == 0, containerLogs, containerId);
//
//        } catch (Exception e) {
//            log.error("Compilation failed for request: {}", request, e);
//            return new CompileResponse(false, "Error: " + e.getMessage(), containerId);
//        } finally {
//            cleanupContainer(containerId);
//        }
//    }

    public ApiResponse<String> saveSourceCode(CompileRequest request) {
        //1. Save source code to file
        try {
            String codeDir = fileService.saveSourceCode(request.getFilename(), request.getCode());
            return ApiResponse.success(codeDir);
        } catch (IOException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<Void> pullImage() {
        // 2. Prepare Docker image
        try {
            dockerService.pullImageIfNeeded(dockerImage);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<String> prepareImageAndRun(PrepareRequest request) {
        // 3. Create and run docker image
        try {
            File hostDir = new File(request.getCodeDir());
            String command = commandBuilder.buildJavaCompileAndRunCommand(hostDir, "Main.java");
            Bind bind = commandBuilder.createBind(hostDir);
            String containerId = dockerService.createAndStartContainer(dockerImage, command, bind);
            return ApiResponse.success(containerId);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public int executeWithTimeout_v1(String containerId) {
        return dockerService.waitForContainer(containerId, timeoutSeconds);
    }

    public ApiResponse<Integer> executeWithTimeout(ExecuteRequest request) {
        // 4. Run with timeout
        try{
            int executionStatusCode = dockerService.waitForContainer(request.getContainerId(), timeoutSeconds);
            return ApiResponse.success(executionStatusCode);
        } catch (Exception e){
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<String> collectLogs(CollectLogsRequest request){
        // 5. Collect logs (using DockerService)
        try{
            String containerLogs = dockerService.getContainerLogs(request.getContainerId());
            return ApiResponse.success(containerLogs);
        }catch (Exception e){
            return ApiResponse.error(e.getMessage());
        }

    }

    private void send(SseEmitter emitter, String message) throws Exception {
        emitter.send("data: " + message + "\n\n");
    }

    private void safeSend(SseEmitter emitter, String message) {
        try {
            send(emitter, message);
        } catch (Exception ignored) {
        }
    }

    public ApiResponse<Void> StopExecution(StopExecutionRequest request) {
        try {
            if (request.getContainerId() != null) {
                dockerService.removeContainer(request.getContainerId());
            }
            return ApiResponse.success();
        } catch (Exception e) {
            log.error("Error while stop container with id: {}", request.getContainerId());
            return ApiResponse.error(e.getMessage());
        }
    }

}