package com.rtvnewsnetwork.quiz.controller;

import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.quiz.model.FilterEnum;
import com.rtvnewsnetwork.quiz.model.QuizFeed;
import com.rtvnewsnetwork.quiz.model.QuizModel;
import com.rtvnewsnetwork.quiz.model.QuizSubmissionRequest;
import com.rtvnewsnetwork.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class QuizController implements AuthDetailsHelper {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/quiz/feed")
    public List<QuizFeed> findQuizFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Extract user ID from the security context
        String userId = getUserId();

        // Construct the quiz feed
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return quizService.getQuizFeed(userId, pageable);
    }

    @PostMapping("/api/quiz/feed/answer")
    public QuizFeed submitQuizAnswer(@RequestBody QuizSubmissionRequest request) {
        String userId = getUserId();
        return quizService.submitQuiz(userId, request);
    }

    @GetMapping("/api/quiz/feed/{id}")
    public QuizFeed getQuizById(@PathVariable("id") String id) {
        String userId = getUserId();
        return quizService.getQuizById(userId, id);
    }

    @GetMapping("/api/quiz/topfans")
    public Map<String, Object> getTopUsers(
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            @RequestParam(name = "filterType", defaultValue = "week") String filterType) {

        String userId = getUserId();

        try {
            FilterEnum.valueOf(filterType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter type. Use 'WEEK' or 'MONTH'.");
        }

        return quizService.getTopUsers(limit, userId, filterType);
    }

    @PostMapping("/api/quiz")
    public QuizModel createQuiz(@RequestBody QuizModel quizModel) {
        try {
            return quizService.createQuiz(quizModel);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data");
        }
    }

}

