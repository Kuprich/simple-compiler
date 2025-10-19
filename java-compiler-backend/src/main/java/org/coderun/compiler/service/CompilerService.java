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

    public ApiResponse<String> saveSourceCode(CompileRequest request) {
        //1. Save source code to file
        String codeDir = "";
        try {
            codeDir = fileService.saveSourceCode(request.getFilename(), request.getCode());
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
            File fullProjectDirectory = fileService.getFullProjectDirectory(request.getCodeDir());
            String command = commandBuilder.buildJavaCompileAndRunCommand(fullProjectDirectory, "Main.java");
            Bind bind = commandBuilder.createBind(fullProjectDirectory);
            String containerId = dockerService.createAndStartContainer(dockerImage, request.getCodeDir(), command, bind);
            return ApiResponse.success(containerId);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    public ApiResponse<Integer> executeWithTimeout(ExecuteRequest request) {
        // 4. Run with timeout
        try {
            int executionStatusCode = dockerService.waitForContainer(request.getContainerId(), timeoutSeconds);
            return ApiResponse.success(executionStatusCode);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<String> collectLogs(CollectLogsRequest request) {
        // 5. Collect logs (using DockerService)
        try {
            String containerLogs = dockerService.getContainerLogs(request.getContainerId());
            return ApiResponse.success(containerLogs);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

//    private void cleanup(String containerId) {
//        String containerName = dockerService.getContainerNameById(containerId);
//        try {
//            fileService.removeSourceCode(containerName);
//            dockerService.removeContainer(containerId);
//        } catch (IOException ignored) { }
//    }
//
//    private void removeSourceCode(String codeDir){
//        try {
//            fileService.removeSourceCode(codeDir);
//        } catch (IOException ignored) { }
//    }

    public ApiResponse<Void> cleanupExecution(CleanupExecutionRequest request) {

        if (!request.getCodeDir().isEmpty()){
            try {
                fileService.removeSourceCode(request.getCodeDir());
            } catch (IOException e) {
                log.error("Error while remove source code directory: {}", request.getCodeDir());
                return ApiResponse.error(e.getMessage());
            }
        }

        if (!request.getContainerId().isEmpty()){
            dockerService.removeContainer(request.getContainerId());
        }
        return ApiResponse.success();

    }

}