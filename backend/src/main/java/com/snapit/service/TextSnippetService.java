package com.snapit.service;

import com.snapit.dto.request.TextSnippetRequest;
import com.snapit.dto.response.SavedItemsResponse;

public interface TextSnippetService {
    SavedItemsResponse saveTextSnippet(TextSnippetRequest textSnippetRequest);
}
