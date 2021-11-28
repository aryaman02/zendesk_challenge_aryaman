package net.aryaman.challenge;

import java.util.List;

public class Ticket {
    private long id;
    private String timeOfCreation;
    private String timeOfUpdate;
    private String subject;
    private String description;
    private List<String> tags;

    public Ticket(long id, String timeOfCreation, String timeOfUpdate, String subject, String description,
        List<String> tags) {
        this.id = id;
        this.timeOfCreation = timeOfCreation;
        this.timeOfUpdate = timeOfUpdate;
        this.subject = subject;
        this.description = description;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(String timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public String getTimeOfUpdate() {
        return timeOfUpdate;
    }

    public void setTimeOfUpdate(String timeOfUpdate) {
        this.timeOfUpdate = timeOfUpdate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
