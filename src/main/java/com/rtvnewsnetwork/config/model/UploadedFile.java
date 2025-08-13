package com.rtvnewsnetwork.config.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rtvnewsnetwork.config.util.RelativePathDeserializer;
import com.rtvnewsnetwork.config.util.RelativePathSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class UploadedFile {

    @Id
    String id;
    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    @JsonProperty("url")
    @Field(name = "url")
    @JsonSerialize(using = RelativePathSerializer.class)
    @JsonDeserialize(using = RelativePathDeserializer.class)
    RelativePath serializableRelativePath;
    String relativePath;
    @CreatedDate
    Instant createdOn;

    public UploadedFile(String id, RelativePath serializableRelativePath, Instant createdOn) {
        this.id = id;
        this.serializableRelativePath = serializableRelativePath;
        this.relativePath = serializableRelativePath.path;
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelativePath getSerializableRelativePath() {
        return serializableRelativePath;
    }

    public void setSerializableRelativePath(
            RelativePath serializableRelativePath) {
        this.serializableRelativePath = serializableRelativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public static class RelativePath {

        String path;

        public RelativePath() {
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public RelativePath(String path) {
            this.path = path;
        }
    }

    public static class Reference {

        public Reference(String ref, RelativePath relativePath) {
            this.ref = ref;
            this.relativePath = relativePath;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public RelativePath getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(RelativePath relativePath) {
            this.relativePath = relativePath;
        }

        public Reference() {
        }

        @Field(targetType = FieldType.OBJECT_ID)
        @JsonIgnore
        String ref;

        @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
        @JsonProperty("path")
        @Field(name = "path")
        RelativePath relativePath;

        public Reference(UploadedFile uploadedFile) {
            this.ref = uploadedFile.getId();
            this.relativePath = new RelativePath(
                    uploadedFile.getSerializableRelativePath().getPath());
        }
    }
}

