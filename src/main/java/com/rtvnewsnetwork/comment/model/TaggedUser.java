package com.rtvnewsnetwork.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaggedUser {
    private String userId;
    private String name;
}

