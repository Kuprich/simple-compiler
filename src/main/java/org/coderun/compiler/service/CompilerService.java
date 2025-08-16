package org.coderun.compiler.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.coderun.compiler.dto.CompileRequest;
import org.coderun.compiler.dto.CompileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CompilerService {
    private static final Logger log = LoggerFactory.getLogger(CompilerService.class);
    public static final String IMAGE = "openjdk:17";

    private final DockerClient docker;

    public CompilerService() {
        //var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();
        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        this.docker = DockerClientImpl.getInstance(config, httpClient);
    }

    public CompileResponse compileAndRun(CompileRequest request) {
        String containerId = null;
        try {

            Path tempDir = Files.createTempDirectory("code");
            File javaFile = new File(tempDir.toFile(), request.getFilename());
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(request.getCode());
            }

            log.info("Создан файл {}", javaFile.getAbsolutePath());

            docker.pullImageCmd(IMAGE).start().awaitCompletion();
            log.debug("Образ " + IMAGE + " готов");

            CreateContainerResponse container = docker.createContainerCmd(IMAGE)
                    .withCmd("sh", "-c", "javac /code/" + request.getFilename() + " && java -cp /code " + className(request.getFilename()))
                    .withBinds(new Bind(tempDir.toAbsolutePath().toString(), new Volume("/code")))
                    .exec();

            containerId = container.getId();
            docker.startContainerCmd(containerId).exec();
            docker.waitContainerCmd(containerId).start().awaitStatusCode();

            StringBuilder logs = new StringBuilder();
            docker.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame frame) {
                            logs.append(new String(frame.getPayload()));
                        }
                    }).awaitCompletion();

            return new CompileResponse(true, logs.toString(), containerId);

        } catch (Exception e) {
            log.error("Ошибка при компиляции/запуске: {}", e.getMessage(), e);
            return new CompileResponse(false, e.getMessage(), containerId);
        } finally {
            if (containerId != null) {
                try {
                    docker.removeContainerCmd(containerId).withForce(true).exec();
                    log.debug("Контейнер {} удалён", containerId);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String className(String filename) {
        return filename.replace(".java", "");
    }
}