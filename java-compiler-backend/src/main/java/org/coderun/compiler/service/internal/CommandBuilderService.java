package org.coderun.compiler.service.internal;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CommandBuilderService {

    public String buildJavaCompileAndRunCommand(File hostDir) {
        String containerCodeDir = hostDir.getAbsolutePath();
        return String.format(
                "javac %s/*.java && java -cp %s Main",
                containerCodeDir,
                containerCodeDir
        );
    }

    public Bind createBind(File hostDir) {
        return new Bind(hostDir.getAbsolutePath(), new Volume(hostDir.getAbsolutePath()));
    }

//    private String extractClassName(String filename) {
//        return filename.endsWith(".java") ?
//                filename.substring(0, filename.length() - 5) : filename;
//    }
}