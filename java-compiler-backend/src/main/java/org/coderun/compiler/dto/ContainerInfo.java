package org.coderun.compiler.dto;

public record ContainerInfo(
        String id,
        String image,
        String[] names,
        String state,
        String status,
        String command
) {}
