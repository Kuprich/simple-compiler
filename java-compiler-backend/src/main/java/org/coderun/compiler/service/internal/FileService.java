package org.coderun.compiler.service.internal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Getter
@Service
@Slf4j
public class FileService {

    private final String hostCodeDir;
    public FileService(@Value("${compiler.host.code.dir:auto}") String codeDirPath) {
        String finalPath;

        if ("auto".equals(codeDirPath)) {
            // auto detection code folder
            finalPath = isRunningInDocker() ? "/code" : "./code";
        } else {
            // use explicit path
            finalPath = codeDirPath;
        }

        this.hostCodeDir = finalPath;
    }

    private String generatePrefix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public String saveSourceCode(String filename, String code) throws IOException {

        String projectName = "/project_" + generatePrefix();
        File projectDir = initializeDirectory(hostCodeDir + projectName);
        File javaFile = new File(projectDir, filename);
        Files.writeString(javaFile.toPath(), code, StandardCharsets.UTF_8);
        log.debug("Source code saved to: {}", javaFile.getAbsolutePath());
        return projectDir.getCanonicalPath();
    }

    private File initializeDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Failed to create directory: " + path);
        }
        log.info("Code directory initialized: {}", directory.getAbsolutePath());
        return directory;
    }

    private boolean isRunningInDocker() {
        try {
            return Files.exists(Paths.get("/.dockerenv"));
        } catch (Exception e) {
            log.debug("Docker detection failed, assuming not in Docker");
            return false;
        }
    }


}
