package com.snapit.service;

import com.snapit.dto.response.SavedItemsResponse;

public interface WebPageService {
    SavedItemsResponse saveWebPage(String url);
}
