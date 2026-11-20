package bank_recommendations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;
    private final RuleStatRepository ruleStatRepository; // <-- Добавили репозиторий статистики (его напишет Максим)

    public RuleController(RuleService ruleService, RuleStatRepository ruleStatRepository) {
        this.ruleService = ruleService;
        this.ruleStatRepository = ruleStatRepository;
    }

    @PostMapping
    public ResponseEntity<RuleEntity> createRule(@RequestBody RuleEntity ruleEntity) {
        return ResponseEntity.ok(ruleService.createRule(ruleEntity));
    }

    @GetMapping
    public ResponseEntity<RuleListResponse> getAllRules() {
        return ResponseEntity.ok(new RuleListResponse(ruleService.getAllRules()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable("id") Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    // НОВЫЙ ЭНДПОИНТ: GET /rule/stats
    @GetMapping("/stats")
    public ResponseEntity<RuleStatsListResponse> getStats() {
        // Достаем всю статистику из БД и мапим в наш DTO
        List<RuleStatResponse> stats = ruleStatRepository.findAll().stream()
                .map(stat -> new RuleStatResponse(stat.getRule().getId(), stat.getCount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RuleStatsListResponse(stats));
    }

    public static class RuleListResponse {
        private List<RuleEntity> data;

        public RuleListResponse(List<RuleEntity> data) {
            this.data = data;
        }

        public List<RuleEntity> getData() {
            return data;
        }

        public void setData(List<RuleEntity> data) {
            this.data = data;
        }
    }
}