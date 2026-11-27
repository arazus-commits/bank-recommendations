package bank_recommendations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-контроллер для управления динамическими правилами рекомендаций.
 * <p>
 * Предоставляет эндпоинты по пути {@code /rule} для создания, просмотра и удаления динамических правил.
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleService ruleService;

    /**
     * Создаёт контроллер управления правилами.
     *
     * @param ruleService сервис для работы с динамическими правилами
     */
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Создаёт и сохраняет новое динамическое правило рекомендаций.
     *
     * @param ruleEntity данные создаваемого правила (продукт и список условий)
     * @return {@link ResponseEntity} с сохранённым правилом {@link RuleEntity}
     */
    @PostMapping
    public ResponseEntity<RuleEntity> createRule(@RequestBody RuleEntity ruleEntity) {
        return ResponseEntity.ok(ruleService.createRule(ruleEntity));
    }

    /**
     * Возвращает список всех сохранённых динамических правил.
     *
     * @return {@link ResponseEntity} с обёрткой {@link RuleListResponse}, содержащей список правил
     */
    @GetMapping
    public ResponseEntity<RuleListResponse> getAllRules() {
        return ResponseEntity.ok(new RuleListResponse(ruleService.getAllRules()));
    }

    /**
     * Удаляет динамическое правило по его идентификатору.
     *
     * @param id уникальный идентификатор удаляемого правила
     * @return {@link ResponseEntity} со статусом {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable("id") Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DTO-обёртка для ответа со списком динамических правил.
     */
    public static class RuleListResponse {
        private List<RuleEntity> data;

        /**
         * Создаёт объект ответа со списком правил.
         *
         * @param data список динамических правил
         */
        public RuleListResponse(List<RuleEntity> data) {
            this.data = data;
        }

        /**
         * Возвращает список динамических правил.
         *
         * @return список объектов {@link RuleEntity}
         */
        public List<RuleEntity> getData() {
            return data;
        }

        /**
         * Устанавливает список динамических правил.
         *
         * @param data список объектов {@link RuleEntity}
         */
        public void setData(List<RuleEntity> data) {
            this.data = data;
        }
    }
}