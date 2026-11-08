package bank_recommendations;

import java.util.UUID;

public class RecommendationDto {
    private String name;
    private UUID id;
    private String text;

    public RecommendationDto(String name, UUID id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    // Геттеры (нужны для того, чтобы Spring собрал JSON)
    public String getName() { return name; }
    public UUID getId() { return id; }
    public String getText() { return text; }
}