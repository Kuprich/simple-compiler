package org.coderun.compiler.service;

import com.github.dockerjava.api.model.Bind;
import lombok.extern.slf4j.Slf4j;
import org.coderun.compiler.dto.CompileRequest;
import org.coderun.compiler.dto.CompileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class CompilerService {

    private static final int TIMEOUT_EXIT_CODE = 124;

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
            ContainerExecutionResult result = executeWithTimeout(containerId);

            // 5. Collect logs (using DockerService)
            String containerLogs = dockerService.getContainerLogs(containerId);
            String allLogs = result.logs() + containerLogs;

            return new CompileResponse(result.exitCode() == 0, allLogs, containerId);

        } catch (Exception e) {
            log.error("Compilation failed for request: {}", request, e);
            return new CompileResponse(false, "Error: " + e.getMessage(), containerId);
        } finally {
            cleanupContainer(containerId);
        }
    }

    private ContainerExecutionResult executeWithTimeout(String containerId) {
        StringBuilder timeoutLogs = new StringBuilder();
        int exitCode;

        try {
            exitCode = dockerService.waitForContainer(containerId, timeoutSeconds);
        } catch (TimeoutException e) {
            log.warn("Timeout for container {}", containerId);
            dockerService.stopContainer(containerId);
            timeoutLogs.append("Execution timed out after ").append(timeoutSeconds).append("s\n");
            exitCode = TIMEOUT_EXIT_CODE;
        } catch (InterruptedException e) {
            log.error("Container execution interrupted: {}", containerId, e);
            Thread.currentThread().interrupt();
            exitCode = 1;
        } catch (ExecutionException e) {
            log.error("Container execution failed: {}", containerId, e);
            exitCode = 1;
        }

        return new ContainerExecutionResult(exitCode, timeoutLogs.toString());
    }

    private void cleanupContainer(String containerId) {
        if (containerId != null) {
            dockerService.removeContainer(containerId);
        }
    }

    private record ContainerExecutionResult(int exitCode, String logs) {}
}