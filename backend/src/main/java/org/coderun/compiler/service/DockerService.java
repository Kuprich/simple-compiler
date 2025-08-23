package org.coderun.compiler.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Service
@Slf4j
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    public void pullImageIfNeeded(String image) throws InterruptedException {
        log.debug("Pulling Docker image: {}", image);
        dockerClient.pullImageCmd(image).start().awaitCompletion();
        log.debug("Docker image {} is ready", image);
    }

    public String createAndStartContainer(String image, String command, Bind bind) {
        CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withCmd("sh", "-c", command)
                .withBinds(bind)
                .exec();

        String containerId = container.getId();
        dockerClient.startContainerCmd(containerId).exec();
        log.debug("Container {} started with command: {}", containerId, command);

        return containerId;
    }

    public int waitForContainer(String containerId, int timeoutSeconds)
            throws InterruptedException, ExecutionException, TimeoutException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> future = executor.submit(() ->
                    dockerClient.waitContainerCmd(containerId).start().awaitStatusCode()
            );
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    public String getContainerLogs(String containerId) throws InterruptedException {
        StringBuilder logsBuilder = new StringBuilder();

        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .exec(new LogCollectorCallback(logsBuilder))
                .awaitCompletion();

        return logsBuilder.toString();
    }

    public void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).withTimeout(1).exec();
            log.debug("Container {} stopped", containerId);
        } catch (Exception e) {
            log.warn("Failed to stop container {}", containerId, e);
        }
    }

    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            log.debug("Container {} removed", containerId);
        } catch (Exception e) {
            log.warn("Failed to remove container {}", containerId, e);
        }
    }

    // Внутренний класс для сбора логов
    private static class LogCollectorCallback extends ResultCallback.Adapter<Frame> {
        private final StringBuilder logsBuilder;

        public LogCollectorCallback(StringBuilder logsBuilder) {
            this.logsBuilder = logsBuilder;
        }

        @Override
        public void onNext(Frame frame) {
            logsBuilder.append(new String(frame.getPayload(), StandardCharsets.UTF_8));
        }
    }
}