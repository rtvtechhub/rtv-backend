package com.rtvnewsnetwork.news.repository;

import com.rtvnewsnetwork.news.model.NewsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NewsRepository extends MongoRepository<NewsModel, String> {

    @Query("{ '$or': [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } }, " +
            "{ 'storyCards.title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'storyCards.description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<NewsModel> searchByTitleDescriptionOrStoryCards(String query, Pageable pageable);
}
