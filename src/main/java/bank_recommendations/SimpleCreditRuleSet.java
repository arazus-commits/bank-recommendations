package bank_recommendations;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private static final UUID RULE_ID = UUID.fromString("ab138afb-8d6c-4e8f-9b0c-2a4c3d5e6f7a");
    private final RecommendationsRepository repository;

    public SimpleCreditRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = repository.hasProductType(userId, "CREDIT");
        double debitDeposit = repository.getSumTransactionsAmount(userId, "DEBIT", "DEPOSIT");
        double debitWithdraw = repository.getSumTransactionsAmount(userId, "DEBIT", "WITHDRAW");

        if (!hasCredit && debitDeposit > 100_000 && debitWithdraw > 100_000) {
            return Optional.of(new RecommendationDto("Simple Credit", RULE_ID, "Рекомендация Simple Credit"));
        }
        return Optional.empty();
    }
}