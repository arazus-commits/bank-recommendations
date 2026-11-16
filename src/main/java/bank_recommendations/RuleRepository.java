package bank_recommendations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
    void deleteByProductId(UUID productId);
}