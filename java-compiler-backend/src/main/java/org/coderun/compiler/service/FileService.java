package org.coderun.compiler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@Slf4j
public class FileService {

    private final File hostCodeDir;

    public FileService(@Value("${compiler.host.code.dir:auto}") String codeDirPath) {
        String finalPath;

        if ("auto".equals(codeDirPath)) {
            // auto detection code folder
            finalPath = isRunningInDocker() ? "/code" : "./code";
        } else {
            // use explicit path
            finalPath = codeDirPath;
        }

        this.hostCodeDir = initializeDirectory(finalPath);
    }

    public void saveSourceCode(String filename, String code) throws IOException {
        File javaFile = new File(hostCodeDir, filename);
        Files.writeString(javaFile.toPath(), code, StandardCharsets.UTF_8);
        log.debug("Source code saved to: {}", javaFile.getAbsolutePath());
        //return javaFile;
    }

    public File getHostCodeDir() {
        return hostCodeDir;
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
