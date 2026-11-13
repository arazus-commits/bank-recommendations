package bank_recommendations;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public TopSavingRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        double savingSum = repository.getSumTransactionsAmount(userId, "SAVING");

        if (hasDebit && savingSum > 5000) {
            return Optional.of(new RecommendationDto("Top Saving", UUID.randomUUID(), "Рекомендация Top Saving"));
        }
        return Optional.empty();
    }
}