package org.coderun.compiler.dto.response;

import org.coderun.compiler.dto.ProgrammingLanguage;

import java.util.List;

public record LanguageResponse(List<ProgrammingLanguage> languages) {
}

