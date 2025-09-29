package org.coderun.compiler.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.coderun.compiler.dto.response.ContainersResponse;
import org.coderun.compiler.dto.response.LanguageResponse;
import org.coderun.compiler.service.internal.DockerService;
import org.coderun.compiler.service.CommonService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
@CrossOrigin(origins = "*")
public class CommonController {

    private final CommonService commonService;
    public CommonController(CommonService commonService, DockerService dockerService) {
        this.commonService = commonService;
    }

    @GetMapping("/languages")
    @Operation(
            summary = "Get languages available for compilation",
            description = "")
    public LanguageResponse getAvailableLanguages() {
        return commonService.getAvailableLanguages();
    }

    @GetMapping("/listAllContainers")
    @Operation(
            summary = "Show information about all docker containers",
            description = "")
    public ContainersResponse listAllContainers() {
        return commonService.listAllContainers();
    }


}
