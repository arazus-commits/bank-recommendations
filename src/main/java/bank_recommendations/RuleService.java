package bank_recommendations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public RuleEntity createRule(RuleEntity ruleEntity) {
        return ruleRepository.save(ruleEntity);
    }

    public List<RuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    @Transactional
    public void deleteRule(UUID productId) {
        ruleRepository.deleteByProductId(productId);
    }
}