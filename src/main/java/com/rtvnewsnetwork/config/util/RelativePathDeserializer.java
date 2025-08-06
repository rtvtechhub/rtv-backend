package com.rtvnewsnetwork.config.util;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class RelativePathDeserializer extends JsonDeserializer<RelativePath> {

    private final String cloudfrontUrl;

    public RelativePathDeserializer(@Value("${cloudfront.url}") String cloudfrontUrl) {
        this.cloudfrontUrl = cloudfrontUrl;
    }


    @Override
    public RelativePath deserialize(JsonParser jsonParser,
                                    DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        return new RelativePath(jsonParser.getText().substring(cloudfrontUrl.length()));
    }
}