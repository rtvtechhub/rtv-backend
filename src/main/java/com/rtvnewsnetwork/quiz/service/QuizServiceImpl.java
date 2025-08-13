package com.rtvnewsnetwork.quiz.service;

import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;
import com.rtvnewsnetwork.config.kafka.KafkaTopicConfig;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.event.service.EventDataUtil;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.quiz.model.*;
import com.rtvnewsnetwork.quiz.repository.QuizAnswerRepository;
import com.rtvnewsnetwork.quiz.repository.QuizRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
    public class QuizServiceImpl implements QuizService, EventPublisher {


    private  QuizRepository quizRepository;
    private  QuizAnswerRepository quizAnswerRepository;
    private  EventService eventService;
//    private final BranchLinkService branchLinkService;

    public QuizServiceImpl(
            QuizRepository quizRepository,
            QuizAnswerRepository quizAnswerRepository,
            EventService eventService )
//            BranchLinkService branchLinkService
//    ) {
    {
        this.quizRepository = quizRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.eventService = eventService;
//        this.branchLinkService = branchLinkService;
    }

    @Override
    public QuizAnswer calculateQuizAnswers(String userId, QuizModel quizModel, String questionId, String optionId) {
        QuizAnswer answer = quizAnswerRepository.findByUserIdAndQuizId(userId, quizModel.getId());

        if (answer != null && answer.getSelectedAnswers().containsKey(questionId)) {
            return answer;
        }

        if (answer == null) {
            answer = new QuizAnswer(
                    null,
                    userId,
                    quizModel.getId(),
                    0,
                    quizModel.getQuestions().size(),
                    0,
                    false,
                    null,
                    new HashMap<>(),
                    null,
                    null
            );
        }

        for (QuizQuestion question : quizModel.getQuestions()) {
            String correctOption = question.getOptions().stream()
                    .filter(Options::isCorrectAnswer)
                    .map(Options::getId)
                    .findFirst()
                    .orElse(null);
            if (optionId.equals(correctOption)) {
                answer.setCorrectAnswers(answer.getCorrectAnswers() + 1);
            }
        }

        answer.getSelectedAnswers().put(questionId, optionId);
        answer.setTotalCoinsEarned(answer.getCorrectAnswers() * quizModel.getRewardCoinsPerQuestion());
        answer.setShowCoinWonScreen(answer.getTotalCoinsEarned() > 0);
        answer.setSuccessText(generateSuccessText(answer.getCorrectAnswers(), answer.getTotalQuestions()));


        if (answer.getSelectedAnswers().size() == quizModel.getQuestions().size()) {
            Map<String, Object> data = EventDataUtil.createQuizCompletionEventData(
                    userId,
                    answer.getTotalCoinsEarned()
            );
            produceEventToKafka(EventType.QUIZ_REWARD, userId, KafkaTopicConfig.COIN_UPDATES_CHANNEL, data);

            if (answer.getTotalCoinsEarned() > 0) {
                Map<String, Object> eventData = Map.of(
                        "title", "Congratulations! You’ve just won " + answer.getTotalCoinsEarned() + " coins!",
                        "description", "Want to win more coins? Keep engaging and play more quizzes!",
                        "path", "/quiz-list"
                );
                produceEventToKafka(EventType.COIN_CREDIT, userId, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);
            }
        }

        return quizAnswerRepository.save(answer);
    }

    private String generateSuccessText(int correctAnswers, int totalQuestions) {
        double percentage = ((double) correctAnswers / totalQuestions) * 100;
        return "You got " + percentage + "% right answers.";
    }

    @Override
    public List<QuizModel> findQuiz() {
        return quizRepository.findAll();
    }

    private List<QuizFeed> updateAnswers(List<QuizFeed> quizList) {
        for (QuizFeed quiz : quizList) {
            if (quiz.getAnswer() == null) {
                quiz.setAnswer(DefaultQuizFeedAnswer.INSTANCE);
            } else {
                QuizAnswer quizAnswer = quiz.getAnswer();
                for (QuizQuestion question : quiz.getQuestions()) {
                    String selectedAnswer = quizAnswer.getSelectedAnswers().get(question.getId());
                    for (Options option : question.getOptions()) {
                        if (option.getId().equals(selectedAnswer)) {
                            option.setAnswered(true);
                        }
                    }
                }
            }
        }
        return quizList;
    }

    @Override
    public List<QuizFeed> getQuizFeed(String userId, Pageable pageable) {
        List<QuizFeed> quizList = quizRepository.findQuiz(userId, pageable);
        if (userId != null && !quizList.isEmpty()) {
            return updateAnswers(quizList);
        } else {
            List<QuizModel> quizModelList = quizRepository.findAllByStatus(QuizStatus.ACTIVE, pageable);
            return quizModelList.stream()
                    .map(quiz -> new QuizFeed(quiz, DefaultQuizFeedAnswer.INSTANCE))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public QuizFeed submitQuiz(String userId, QuizSubmissionRequest quizSubmissionRequest) {
        QuizModel quizModel = quizRepository.findById(quizSubmissionRequest.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        QuizAnswer answer = calculateQuizAnswers(
                userId, quizModel, quizSubmissionRequest.getQuestionId(), quizSubmissionRequest.getOptionId()
        );
        QuizFeed quizFeed = new QuizFeed(quizModel, answer);
        List<QuizFeed> result = updateAnswers(List.of(quizFeed));
        return result.get(0);
    }

    @Override
    public QuizFeed getQuizById(String userId, String quizId) {
        QuizFeed quizFeed = quizRepository.findQuizById(userId, quizId);
        if (userId != null && quizFeed != null) {
            return updateAnswers(List.of(quizFeed)).get(0);
        } else {
            QuizModel quizModel = quizRepository.findById(quizId)
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));
            return new QuizFeed(quizModel, DefaultQuizFeedAnswer.INSTANCE);
        }
    }

    @Override
    public Map<String, Object> getTopUsers(int limit, String userId, String filterType) {
        Map.Entry<FilterEnum, Map.Entry<Date, Date>> filterData = getFilterEnumAndDateRange(filterType);
        FilterEnum filterEnum = filterData.getKey();
        Date startDate = filterData.getValue().getKey();
        Date endDate = filterData.getValue().getValue();

        List<UserStatsWithUsernameDto> topUsers = quizAnswerRepository.findTopUsers(limit, startDate, endDate);
        Long totalUsersCount = quizAnswerRepository.countUniqueUsers(startDate, endDate);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("topUsers", topUsers);

        if (userId != null) {
            UserStatsWithUsernameDto specificUser = quizAnswerRepository.findUserRankById(userId, startDate, endDate);
            if (specificUser != null) {
                resultMap.putAll(specificUser.toMap());
            } else {
                resultMap.put("userId", null);
            }
        }

        resultMap.put("totalUsersCount", totalUsersCount);
        return resultMap;
    }

    @Override
    public Map.Entry<FilterEnum, Map.Entry<Date, Date>> getFilterEnumAndDateRange(String filterType) {
        FilterEnum filterEnum;
        try {
            filterEnum = FilterEnum.valueOf(filterType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter type. Use 'WEEK' or 'MONTH'.");
        }

        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();

        switch (filterEnum) {
            case WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1);
            case MONTH -> calendar.add(Calendar.MONTH, -1);
        }

        Date startDate = calendar.getTime();

        return Map.entry(filterEnum, Map.entry(startDate, endDate));
    }

    @Override
    public QuizModel createQuiz(QuizModel quizModel) {
        QuizModel response = quizRepository.save(quizModel);

        Map<String, Object> eventData = Map.of(
                "title", "🔥New Quiz Alert!🚀",
                "description", response.getQuestions().get(0).getQuestion(),
                "path", "/quiz-list"
        );

        produceEventToKafka(EventType.NEW_POST, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);

//        String deepLink = branchLinkService.createBranchLink(response.getId(), "quiz");
//        if (deepLink != null) {
//            quizModel.setShareUrl(deepLink);
//        } else {
//            System.out.println("Failed to create deep link for post ID: " + response.getId());
//        }

        quizModel.setId(response.getId());
        quizRepository.save(quizModel);
        return quizModel;
    }

    @Override
    public QuizModel deleteQuiz(String id) {
        QuizModel quizModel = quizRepository.findById(id).orElseThrow(()->new RuntimeException("Quiz not found with id: "+id));
        quizRepository.deleteById(id);
        return quizModel;
    }

    @Override
    public void deleteQuizzes(List<String> quizIds) {
        quizRepository.deleteAllById(quizIds);
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }
}

