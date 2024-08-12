package com.hatio.taskStream.service;

import com.hatio.taskStream.model.Project;

public interface MarkdownService {
    String generateMarkdownSummary(Project project);
}
