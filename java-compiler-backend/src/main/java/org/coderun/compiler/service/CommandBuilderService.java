package org.coderun.compiler.service;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CommandBuilderService {

    private static final String CONTAINER_CODE_DIR = "/code";

    public String buildJavaCompileAndRunCommand(String filename) {
        String className = extractClassName(filename);
        return String.format(
                "javac %s/%s && java -cp %s %s",
                CONTAINER_CODE_DIR,
                filename,
                CONTAINER_CODE_DIR,
                className
        );
    }

    public Bind createBind(File hostDir) {
        return new Bind(hostDir.getAbsolutePath(), new Volume(CONTAINER_CODE_DIR));
    }

    private String extractClassName(String filename) {
        return filename.endsWith(".java") ?
                filename.substring(0, filename.length() - 5) : filename;
    }
}