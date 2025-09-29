package org.coderun.compiler.service;

import org.coderun.compiler.dto.ContainerInfo;
import org.coderun.compiler.dto.response.ContainersResponse;
import org.coderun.compiler.dto.response.LanguageResponse;
import org.coderun.compiler.dto.ProgrammingLanguage;
import org.coderun.compiler.service.internal.DockerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonService {

    private final DockerService dockerService;

    public CommonService(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    public LanguageResponse getAvailableLanguages() {
        List<ProgrammingLanguage> languages = List.of(
                new ProgrammingLanguage("Java", "17")
        );
        return new LanguageResponse(languages);
    }

    public ContainersResponse listAllContainers() {
        List<ContainerInfo> infos = dockerService.listAllContainers();
        return new ContainersResponse(infos);
    }
}
