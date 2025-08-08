package com.rtvnewsnetwork.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollQuestion {

    private String id = new ObjectId().toString();

    private String title;

    private List<Choice> options;
}
