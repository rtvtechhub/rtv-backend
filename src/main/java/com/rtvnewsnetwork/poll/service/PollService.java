package com.rtvnewsnetwork.poll.service;

import com.rtvnewsnetwork.poll.model.PollModel;

import java.util.List;
import java.util.Map;

public interface PollService {

    Map<String, Integer> calculatePercentage(String pollId);

    PollModel addChoices(String userId, String pollId, String choiceId);

    List<PollModel> getPollFeed(String userId, int page, int size);

    PollModel addPoll(PollModel pollModel);

}
