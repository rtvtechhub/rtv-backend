package com.rtvnewsnetwork.config.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class RelativePathSerializer extends JsonSerializer<RelativePath> {

    private final String cloudfrontUrl;

    public RelativePathSerializer(@Value("${cloudfront.url}") String cloudfrontUrl) {
        this.cloudfrontUrl = cloudfrontUrl;
    }

    @Override
    public void serialize(RelativePath value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(cloudfrontUrl + value.getPath());
    }
}

