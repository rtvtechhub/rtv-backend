package com.rtvnewsnetwork.poll.repository;

import com.rtvnewsnetwork.poll.model.PollResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PollResponseRepository extends MongoRepository<PollResponse, String> {

    List<PollResponse> findAllByPollId(String pollId);

    List<PollResponse> findByCreatedAtBetween(Instant startDate, Instant endDate);
}

