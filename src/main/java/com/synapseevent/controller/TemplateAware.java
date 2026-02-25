package com.synapseevent.controller;

import com.synapseevent.entities.EventTemplate;

public interface TemplateAware {
    void applyTemplate(EventTemplate template);
}