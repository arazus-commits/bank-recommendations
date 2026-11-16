package bank_recommendations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasProductType(UUID userId, String productType) {
        String sql = "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id WHERE t.user_id = ? AND p.type = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType);
        return count != null && count > 0;
    }

    public double getSumTransactionsAmount(UUID userId, String productType, String transactionType) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id WHERE t.user_id = ? AND p.type = ? AND t.type = ?";
        Double sum = jdbcTemplate.queryForObject(sql, Double.class, userId, productType, transactionType);
        return sum != null ? sum : 0.0;
    }

        public long getTransactionCount(UUID userId, String productType) {
        String sql = "SELECT COUNT(*) FROM transactions t JOIN products p ON t.product_id = p.id WHERE t.user_id = ? AND p.type = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
        return count != null ? count : 0L;
    }

    public boolean compareTransactionSum(UUID userId, String productType, String transactionType, String operator, int threshold) {
        double sum = getSumTransactionsAmount(userId, productType, transactionType);
        return switch (operator) {
            case ">" -> sum > threshold;
            case "<" -> sum < threshold;
            case "=" -> sum == threshold;
            case ">=" -> sum >= threshold;
            case "<=" -> sum <= threshold;
            default -> false;
        };
    }

    public boolean compareDepositWithdraw(UUID userId, String productType, String operator) {
        double deposit = getSumTransactionsAmount(userId, productType, "DEPOSIT");
        double withdraw = getSumTransactionsAmount(userId, productType, "WITHDRAW");
        return switch (operator) {
            case ">" -> deposit > withdraw;
            case "<" -> deposit < withdraw;
            case "=" -> deposit == withdraw;
            case ">=" -> deposit >= withdraw;
            case "<=" -> deposit <= withdraw;
            default -> false;
        };
    }
}