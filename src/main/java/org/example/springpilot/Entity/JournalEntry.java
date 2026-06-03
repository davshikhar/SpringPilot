package org.example.springpilot.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.example.springpilot.Sentiment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @NonNull
    private String title;

    private String content;

    private LocalDateTime date;

    private Sentiment sentiment;

    //getter and setters removed due to the functionality of Lombok
}
