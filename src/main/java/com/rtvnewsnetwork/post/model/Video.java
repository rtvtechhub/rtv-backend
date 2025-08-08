package com.rtvnewsnetwork.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private ExternalFile externalFile=null;
    private File file=null;
}
