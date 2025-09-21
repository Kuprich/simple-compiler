package org.coderun.compiler.service;

import org.coderun.compiler.dto.LanguageResponse;
import org.coderun.compiler.dto.ProgrammingLanguage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {
    public LanguageResponse getAvailableLanguages() {
        List<ProgrammingLanguage> languages = List.of(
                new ProgrammingLanguage("Java", "17")
        );

        return new LanguageResponse(languages);
    }
}
