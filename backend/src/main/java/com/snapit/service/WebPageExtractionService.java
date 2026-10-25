package com.snapit.service;


import org.jsoup.nodes.Document;

public interface WebPageExtractionService {
    //    String extractTitle(String url);
//    String extractDescription(String url);
//    String extractText(String url);
    Document fetchWebPage(String url);
}

