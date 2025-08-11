package com.rtvnewsnetwork.news.service;

import com.rtvnewsnetwork.news.model.NewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {

    NewsModel createNews(NewsModel news);

    Page<NewsModel> getAllNews(Pageable pageable);

    NewsModel getNewsById(String id);

    void deleteNews(String id);

    Page<NewsModel> searchNews(String query, Pageable pageable);
}
