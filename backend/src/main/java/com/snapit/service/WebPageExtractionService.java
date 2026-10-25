package com.snapit.service;

<<<<<<< HEAD
import org.jsoup.nodes.Document;

public interface WebPageExtractionService {
//    String extractTitle(String url);
//    String extractDescription(String url);
//    String extractText(String url);
    Document fetchWebPage(String url);
=======
public interface WebPageExtractionService {
    String extractTitle(String url);
    String extractDescription(String url);
    String extractText(String url);
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
}
