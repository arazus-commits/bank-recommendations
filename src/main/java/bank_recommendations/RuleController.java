package bank_recommendations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
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