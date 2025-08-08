package com.rtvnewsnetwork.poll.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    private String id = new ObjectId().toString();

    private String label;

    private Integer optionPercentage;

    private Boolean isCorrectChoice;

    @Transient
    @JsonProperty("isMarked")
    private boolean marked = false;
}

