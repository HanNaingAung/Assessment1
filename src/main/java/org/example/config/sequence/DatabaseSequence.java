package org.example.config.sequence;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "item_sequence")
public class DatabaseSequence {

    private String id;
    private long seq;

    public DatabaseSequence() {}

    public DatabaseSequence(String id, long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
