package com.rtvnewsnetwork.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalFile {
    private String url;
    private VideoContentType type;
}
