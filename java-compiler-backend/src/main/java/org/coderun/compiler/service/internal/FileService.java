package org.coderun.compiler.service.internal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.coderun.compiler.dto.SourceCodeFile;
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

    public String saveFiles(SourceCodeFile[] files) throws IOException {
        String projectName = "/project_" + generatePrefix();
        File projectDir = initializeDirectory(hostCodeDir + projectName);

        for (SourceCodeFile file : files) {
            File javaFile = new File(projectDir, file.getFilename());
            Files.writeString(javaFile.toPath(), file.getCode(), StandardCharsets.UTF_8);
            log.debug("file \"{}\" saved to: {}", file.getFilename(), javaFile.getAbsolutePath());
        }
        return projectDir.getName();
    }

    public File getFullProjectDirectory(String projectDir) {
        return new File(this.hostCodeDir, projectDir);
    }

    public void removeSourceCode(String codeDir) throws IOException {
        File directory = getFullProjectDirectory(codeDir);
        if (directory.exists())
            FileUtils.deleteDirectory(directory);
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
