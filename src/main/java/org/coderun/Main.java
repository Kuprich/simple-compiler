package org.coderun;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Запуск приложения");

        DockerClient docker = initDockerClient();

        Path codeDir = prepareSource();
        String containerId = runContainer(docker, codeDir);

        docker.waitContainerCmd(containerId).start().awaitStatusCode();
        log.info("Контейнер {} завершил работу", containerId);

        String logs = getContainerLogs(docker, containerId);
        log.info("Вывод контейнера:\n{}", logs);

        docker.removeContainerCmd(containerId).withForce(true).exec();
        log.info("Контейнер {} удалён", containerId);
    }

    private static DockerClient initDockerClient() {
        log.debug("Инициализация Docker клиента...");
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        DockerClient client = DockerClientImpl.getInstance(config, httpClient);
        log.info("Docker клиент инициализирован");
        return client;
    }

    private static Path prepareSource() throws Exception {
        log.debug("Создание временной директории и файла Hello.java...");

        String code = """
                public class Hello {
                    public static void main(String[] args) {
                        System.out.println("Hello from Docker Java compiler!");
                    }
                }
                """;

        Path tempDir = Files.createTempDirectory("code");
        File javaFile = new File(tempDir.toFile(), "Hello.java");
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(code);
        }

        log.info("Исходный файл {} создан", javaFile.getAbsolutePath());
        return tempDir;
    }

    private static String runContainer(DockerClient docker, Path codeDir) throws InterruptedException {
        log.debug("Загрузка образа openjdk:21 (если не скачан)...");
        docker.pullImageCmd("openjdk:21").start().awaitCompletion();
        log.info("Образ openjdk:21 готов");

        log.debug("Создание контейнера...");
        CreateContainerResponse container = docker.createContainerCmd("openjdk:21")
                .withCmd("sh", "-c", "javac /code/Hello.java && java -cp /code Hello")
                .withBinds(new Bind(codeDir.toAbsolutePath().toString(), new Volume("/code")))
                .exec();

        docker.startContainerCmd(container.getId()).exec();
        log.info("Контейнер {} запущен", container.getId());
        return container.getId();
    }

    private static String getContainerLogs(DockerClient docker, String containerId) throws InterruptedException {
        log.debug("Получение логов контейнера {}...", containerId);
        StringBuilder sb = new StringBuilder();
        docker.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        sb.append(new String(frame.getPayload()));
                    }
                }).awaitCompletion();
        log.debug("Логи контейнера {} получены ({} символов)", containerId, sb.length());
        return sb.toString();
    }
}