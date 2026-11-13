package bank_recommendations;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID RULE_ID = UUID.fromString("59efc529-2dc8-4682-8a15-4a4c4d6c6e37");
    private final RecommendationsRepository repository;

    public TopSavingRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        double debitSum = repository.getSumTransactionsAmount(userId, "DEBIT", "DEPOSIT");
        double savingSum = repository.getSumTransactionsAmount(userId, "SAVING", "DEPOSIT");

        if (hasDebit && (debitSum >= 50_000 || savingSum >= 50_000)) {
            return Optional.of(new RecommendationDto("Top Saving", RULE_ID, "Рекомендация Top Saving"));
        }
        return Optional.empty();
    }
}