package com.rtvnewsnetwork.poll.controller;

import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.poll.model.PollModel;
import com.rtvnewsnetwork.poll.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PollController implements AuthDetailsHelper {

    private final PollService pollService;

    @PostMapping("/polls/response")
    public PollModel addPollResponse(
            @RequestParam String pollId,
            @RequestParam String selectedChoiceId
    ) {
        String userId = getUserId();
        return pollService.addChoices(userId, pollId, selectedChoiceId);
    }

    @GetMapping("/polls/feed")
    public List<PollModel> getPollFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = getUserId();
        return pollService.getPollFeed(userId, page, size);
    }

    @Operation(summary = "Endpoint to create poll from dashboard")
    @PostMapping("/poll")
    public PollModel savePodcast(@RequestBody PollModel pollModel) {
        try {
            return pollService.addPoll(pollModel);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data");
        }
    }
}
