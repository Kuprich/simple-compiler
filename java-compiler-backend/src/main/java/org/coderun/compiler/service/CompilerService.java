package org.coderun.compiler.service;

import com.github.dockerjava.api.model.Bind;
import lombok.extern.slf4j.Slf4j;
import org.coderun.compiler.dto.request.CompileRequest;
import org.coderun.compiler.dto.response.CompileResponse;
import org.coderun.compiler.dto.request.StopExecutionRequest;
import org.coderun.compiler.service.internal.CommandBuilderService;
import org.coderun.compiler.service.internal.DockerService;
import org.coderun.compiler.service.internal.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

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

    public CompileResponse compileAndRun(CompileRequest request) {
        String containerId = null;
        try {
            // 1. Save code to file
            fileService.saveSourceCode(request.getFilename(), request.getCode());

            // 2. Prepare Docker image
            dockerService.pullImageIfNeeded(dockerImage);

            // 3. Create and run docker image
            String command = commandBuilder.buildJavaCompileAndRunCommand(request.getFilename());
            Bind bind = commandBuilder.createBind(fileService.getHostCodeDir());
            containerId = dockerService.createAndStartContainer(dockerImage, command, bind);

            // 4. Run with timeout
            int executionStatusCode = executeWithTimeout(containerId);

            // 5. Collect logs (using DockerService)
            String containerLogs = dockerService.getContainerLogs(containerId);

            return new CompileResponse(executionStatusCode == 0, containerLogs, containerId);

        } catch (Exception e) {
            log.error("Compilation failed for request: {}", request, e);
            return new CompileResponse(false, "Error: " + e.getMessage(), containerId);
        } finally {
            cleanupContainer(containerId);
        }
    }

    private void send(SseEmitter emitter, String message) throws Exception {
        emitter.send("data: " + message + "\n\n");
    }

    private void safeSend(SseEmitter emitter, String message) {
        try {
            send(emitter, message);
        } catch (Exception ignored) {}
    }



    public CompileResponse StopExecution(StopExecutionRequest request){
        try{
            dockerService.removeContainer(request.getContainerId());
        } catch (Exception e) {
            log.error("Error while stop container with id: {}", request.getContainerId());
            return new CompileResponse(false, "Failed to stop execution process", "");
        }
        return new CompileResponse(true, "Execution stoped", "");
    }

    private int executeWithTimeout(String containerId)  {
        String logs = "";
        return dockerService.waitForContainer(containerId, timeoutSeconds);
    }

    private void cleanupContainer(String containerId) {
        if (containerId != null) {
            dockerService.removeContainer(containerId);
        }
    }

}