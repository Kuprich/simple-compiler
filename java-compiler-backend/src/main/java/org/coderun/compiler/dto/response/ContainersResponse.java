package org.coderun.compiler.dto.response;

import org.coderun.compiler.dto.ContainerInfo;

import java.util.List;

public record ContainersResponse(List<ContainerInfo> Infos){}