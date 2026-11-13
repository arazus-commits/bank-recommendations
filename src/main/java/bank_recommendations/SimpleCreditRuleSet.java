package bank_recommendations;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public SimpleCreditRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = repository.hasProductType(userId, "CREDIT");

        if (hasCredit) {
            return Optional.of(new RecommendationDto("Simple Credit", UUID.randomUUID(), "Рекомендация Simple Credit"));
        }
        return Optional.empty();
    }
}