package com.snapit.service.impl;

import com.snapit.exception.WebPageFetchException;
import com.snapit.service.WebPageExtractionService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebPageExtractionServiceImpl implements WebPageExtractionService {
    @Override
    public Document fetchWebPage(String url) {
        try{
           return Jsoup.connect(url).timeout(100000).get();
        }catch (IOException exception){
            throw new WebPageFetchException("Unable to fecth WebPage. Please check URl, try again later", exception);
        }
    }
//    @Override
//    public String extractTitle(String url) {
//        try{
//            Document document = Jsoup.connect(url).timeout(10000).get();
//            return document.title();
//        }catch (IOException e){
//           throw new RuntimeException("Failed to extract title from URL", e);
//        }
//    }
//
//    @Override
//    public String extractDescription(String url) {
//        try{
//            Document document = Jsoup.connect(url).timeout(10000).get();
//            return document.select("meta[name=description]").attr("content");
//        }catch (IOException e){
//            throw new RuntimeException("Failed to extract webpage description", e);
//        }
//    }
//
//    @Override
//    public String extractText(String url) {
//        try {
//            Document document = Jsoup.connect(url)
//                    .timeout(10000)
//                    .get();
//            document.select("script, style, noscript").remove();
//            return document.body().text();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to extract webpage text", e);
//        }
//    }
}
