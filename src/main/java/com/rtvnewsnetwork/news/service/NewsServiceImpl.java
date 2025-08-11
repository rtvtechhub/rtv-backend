package com.rtvnewsnetwork.news.service;

import com.rtvnewsnetwork.news.model.NewsModel;
import com.rtvnewsnetwork.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public NewsModel createNews(NewsModel news) {
        news.setCreatedAt(Instant.now());
        return newsRepository.save(news);
    }

    @Override
    public Page<NewsModel> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }


    @Override
    public NewsModel getNewsById(String id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    @Override
    public void deleteNews(String id) {
        newsRepository.deleteById(id);
    }

    @Override
    public Page<NewsModel> searchNews(String query, Pageable pageable) {
        return newsRepository.searchByTitleDescriptionOrStoryCards(query, pageable);
    }
}

