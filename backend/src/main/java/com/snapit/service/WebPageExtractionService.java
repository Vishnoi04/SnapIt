package com.snapit.service;

public interface WebPageExtractionService {
    String extractTitle(String url);
    String extractDescription(String url);
    String extractText(String url);
}
