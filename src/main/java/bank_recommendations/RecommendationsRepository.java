package bank_recommendations;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    private record CountKey(UUID userId,String productType){}
    private record SumKey(UUID userId,String productType,String transactionType){}

    private final Cache<CountKey,Integer> countCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    private final Cache<SumKey,Double> sumCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getTransactionCount(UUID userId, String productType) {
        CountKey key = new CountKey(userId, productType);
        Integer count = countCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?""";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, k.userId(), k.productType());
            return result != null ? result : 0;
        });
        return count != null ? count : 0;
    }

    public boolean hasProductType(UUID userId, String productType) {
        return getTransactionCount(userId, productType) > 0;
        }

    public double getSumTransactionsAmount(UUID userId, String productType, String transactionType) {
        SumKey key = new SumKey(userId, productType, transactionType);
        Double sum = sumCache.get(key, k -> {
            String sql = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t JOIN products p ON t.product_id = p.id WHERE t.user_id = ? AND p.type = ? AND t.type = ?";
            Double result = jdbcTemplate.queryForObject(sql, Double.class, k.userId(), k.productType(), k.transactionType());
            return result != null ? result : 0.0;
        });
        return sum != null ? sum : 0.0;
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
    public List<String> findUserNames(String namePattern) {
        String sql = """
            SELECT first_name || ' ' || last_name
            FROM users
            WHERE first_name LIKE ? OR last_name LIKE ?""";
        return jdbcTemplate.queryForList(sql, String.class, namePattern, namePattern);
    }

    public UUID findUserIdByName(String fullName) {
        String sql = """
            SELECT id FROM users
            WHERE first_name || ' ' || last_name = ?""";
        var ids = jdbcTemplate.queryForList(sql, UUID.class, fullName);
        return ids.isEmpty() ? null : ids.get(0);
    }
    public void clearCaches() {
        countCache.invalidateAll();
        sumCache.invalidateAll();
    }
}
