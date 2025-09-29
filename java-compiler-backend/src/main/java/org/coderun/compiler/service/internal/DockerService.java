package org.coderun.compiler.service.internal;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import lombok.extern.slf4j.Slf4j;
import org.coderun.compiler.dto.ContainerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    /**
     * Pull Docker image if it's not already available locally.
     */
    public void pullImageIfNeeded(String image) {
        try {
            log.debug("Pulling Docker image: {}", image);
            dockerClient.pullImageCmd(image).start().awaitCompletion();
            log.debug("Docker image {} is ready", image);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Image pull was interrupted", e);
        } catch (DockerException e) {
            throw new RuntimeException("Failed to pull image: " + image, e);
        }
    }

    /**
     * Create and start a container with given image, command and volume binding.
     */
    public String createAndStartContainer(String image, String command, Bind bind) {
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                    .withCmd("sh", "-c", command) // NOTE: Ensure 'command' is safe
                    .withHostConfig(
                            HostConfig.newHostConfig()
                                    .withBinds(bind)
                    )
                    .exec();

            String containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();
            log.debug("Container {} started with command: {}", containerId, command);

            return containerId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create/start container with image " + image, e);
        }
    }

    /**
     * Wait until the container finishes execution or timeout occurs.
     */
    public int waitForContainer(String containerId, int timeoutSeconds) {
        return dockerClient.waitContainerCmd(containerId)
                    .start()
                    .awaitStatusCode(timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Collect logs from stdout and stderr of the container.
     */
    public String getContainerLogs(String containerId) {
        StringBuilder logsBuilder = new StringBuilder();
        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new LogCollectorCallback(logsBuilder))
                    .awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Log collection was interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get logs from container " + containerId, e);
        }
        return logsBuilder.toString();
    }

    /**
     * Force remove container.
     */
    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
            log.debug("Container {} removed", containerId);
        } catch (Exception e) {
            log.warn("Failed to remove container {}", containerId, e);
        }
    }

    /**
     * Show information about all docker containers
     */
    public List<ContainerInfo> listAllContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true) // only running
                .exec()
                .stream()
                .map(c -> new ContainerInfo(
                        c.getId(),
                        c.getImage(),
                        c.getNames(),
                        c.getState(),
                        c.getStatus(),
                        c.getCommand()
                ))
                .toList();
    }

    /**
     * Internal class to collect logs from Docker stream.
     */
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

