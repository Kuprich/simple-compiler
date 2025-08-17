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
    private final String hostCodeDir;
    private final String containerCodeDir = "/code"; // путь внутри контейнера песочницы

    public CompilerService() {
        // DOCKER_HOST из переменных окружения (или unix-сокет по умолчанию)
        String dockerHost = System.getenv().getOrDefault("DOCKER_HOST", "unix:///var/run/docker.sock");

        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(false)
                .build();

        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        this.docker = DockerClientImpl.getInstance(config, httpClient);

        // Директория для исходников — задаём через ENV (иначе /tmp/code)
        this.hostCodeDir = System.getenv().getOrDefault("CODE_DIR", "/tmp/code");
        File dir = new File(hostCodeDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Не удалось создать директорию " + hostCodeDir);
        }
        log.info("Директория для исходников: {}", hostCodeDir);
    }

    public CompileResponse compileAndRun(CompileRequest request) {
        String containerId = null;
        try {
            // сохраняем исходник в томе (общая директория)
            File javaFile = new File(hostCodeDir, request.getFilename());
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(request.getCode());
            }
            log.info("Создан файл {}", javaFile.getAbsolutePath());

            // гарантируем наличие образа
            docker.pullImageCmd(IMAGE).start().awaitCompletion();
            log.debug("Образ {} готов", IMAGE);

            // создаём контейнер, монтируем shared volume
            String runCmd = String.format(
                    "javac %s/%s && java -cp %s %s",
                    containerCodeDir,
                    request.getFilename(),
                    containerCodeDir,
                    className(request.getFilename())
            );

            CreateContainerResponse container = docker.createContainerCmd(IMAGE)
                    .withCmd("sh", "-c", runCmd)
                    .withBinds(new Bind(hostCodeDir, new Volume(containerCodeDir)))
                    .exec();

            containerId = container.getId();
            docker.startContainerCmd(containerId).exec();
            docker.waitContainerCmd(containerId).start().awaitStatusCode();

            // собираем логи
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
                } catch (Exception ignored) {}
            }
        }
    }

    private String className(String filename) {
        return filename.replace(".java", "");
    }
}