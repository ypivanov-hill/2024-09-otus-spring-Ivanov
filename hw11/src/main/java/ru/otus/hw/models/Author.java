package ru.otus.hw.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Author {

    //@BsonId
    @Id
    private String id;

    private String fullName;

    public Author (String fullName) {
        this.fullName = fullName;
    }

}
