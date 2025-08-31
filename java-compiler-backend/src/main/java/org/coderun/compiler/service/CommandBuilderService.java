package org.coderun.compiler.service;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CommandBuilderService {

    //private static final String CONTAINER_CODE_DIR = "/code";

    @Autowired
    FileService fileService;

    public String buildJavaCompileAndRunCommand(String filename) {
        String className = extractClassName(filename);
        String containerCodeDir = fileService.getHostCodeDir().getAbsolutePath();
        return String.format(
                "javac %s/%s && java -cp %s %s",
                containerCodeDir,
                filename,
                containerCodeDir,
                className
        );
    }

    public Bind createBind(File hostDir) {
        return new Bind(hostDir.getAbsolutePath(), new Volume(fileService.getHostCodeDir().getAbsolutePath()));
    }

    private String extractClassName(String filename) {
        return filename.endsWith(".java") ?
                filename.substring(0, filename.length() - 5) : filename;
    }
}