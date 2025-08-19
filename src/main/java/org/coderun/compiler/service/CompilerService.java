package org.coderun.compiler.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import org.coderun.compiler.dto.CompileRequest;
import org.coderun.compiler.dto.CompileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.*;

@Service
public class CompilerService {
    private static final Logger log = LoggerFactory.getLogger(CompilerService.class);

    @Value("${compiler.java.image}")
    private String image;

    @Value("${compiler.timeout.seconds}")
    private int timeoutSeconds;

    @Autowired
    private DockerClient docker;

    private final String hostCodeDir;


    public CompilerService() {

        // Directory where source code will be stored on host
        this.hostCodeDir = System.getenv().getOrDefault("CODE_DIR", "/tmp/code");
        File dir = new File(hostCodeDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create directory " + hostCodeDir);
        }
        log.info("Source code directory: {}", hostCodeDir);
    }

    public CompileResponse compileAndRun(CompileRequest request) {
        String containerId = null;
        try {
            // Save the source code to a file
            File javaFile = new File(hostCodeDir, request.getFilename());
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(request.getCode());
            }
            log.info("Created file {}", javaFile.getAbsolutePath());

            // Pull the Docker image if not present
            docker.pullImageCmd(image).start().awaitCompletion();
            log.debug("Image {} is ready", image);

            // Path inside the container sandbox
            String containerCodeDir = "/code";
            String runCmd = String.format(
                    "javac %s/%s && java -cp %s %s",
                    containerCodeDir,
                    request.getFilename(),
                    containerCodeDir,
                    className(request.getFilename())
            );

            // Create and start the container
            CreateContainerResponse container = docker.createContainerCmd(image)
                    .withCmd("sh", "-c", runCmd)
                    .withBinds(new Bind(hostCodeDir, new Volume(containerCodeDir)))
                    .exec();

            containerId = container.getId();
            final String finalContainerId = containerId;
            docker.startContainerCmd(containerId).exec();

            // Set up timeout mechanism
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Integer> future = executor.submit(() ->
                    docker.waitContainerCmd(finalContainerId).start().awaitStatusCode()
            );

            int exitCode;
            StringBuilder logs = new StringBuilder();

            try {
                exitCode = future.get(timeoutSeconds, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.warn("Execution timeout reached, stopping container {}", containerId);
                docker.stopContainerCmd(containerId).withTimeout(1).exec();
                exitCode = 124;  // Standard timeout exit code
                logs.append("Execution timed out after ").append(timeoutSeconds).append("s\n");
            } finally {
                executor.shutdownNow();
            }

            // Collect container logs
            docker.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame frame) {
                            logs.append(new String(frame.getPayload()));
                        }
                    }).awaitCompletion();

            boolean success = exitCode == 0;
            return new CompileResponse(success, logs.toString(), containerId);

        } catch (Exception e) {
            log.error("Error during compilation/execution: {}", e.getMessage(), e);
            return new CompileResponse(false, e.getMessage(), containerId);
        } finally {
            // Clean up the container
            if (containerId != null) {
                try {
                    docker.removeContainerCmd(containerId).withForce(true).exec();
                    log.debug("Container {} removed", containerId);
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Extracts class name from filename by removing .java extension
     */
    private String className(String filename) {
        return filename.replace(".java", "");
    }
}