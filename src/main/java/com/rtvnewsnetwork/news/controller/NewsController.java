package com.rtvnewsnetwork.news.controller;

import com.rtvnewsnetwork.news.model.NewsModel;
import com.rtvnewsnetwork.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * Create a news item
     */
    @PostMapping
    public NewsModel createNews(@RequestBody NewsModel news) {
        return newsService.createNews(news);
    }

    /**
     * Get all news with pagination
     */
    @GetMapping
    public Page<NewsModel> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return newsService.getAllNews(pageable);
    }


    /**
     * Get a news item by ID
     */
    @GetMapping("/{id}")
    public NewsModel getNewsById(@PathVariable String id) {
        return newsService.getNewsById(id);
    }

    /**
     * Delete a news item by ID
     */
    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteNews(id);
    }

    /**
     * Search news by title, description, or story card content
     */
    @GetMapping("/search")
    public Page<NewsModel> searchNews(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return newsService.searchNews(q, pageable);
    }
}

