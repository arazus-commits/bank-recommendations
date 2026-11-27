package bank_recommendations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-контроллер для получения статистики срабатываний динамических правил.
 * <p>
 * Обрабатывает запросы по пути {@code /rule/stats}.
 */
@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    private final RuleStatisticsRepository statisticsRepository;

    /**
     * Создаёт контроллер статистики правил.
     *
     * @param statisticsRepository репозиторий статистики срабатываний динамических правил
     */
    public RuleStatsController(RuleStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * Возвращает статистику срабатываний по всем динамическим правилам.
     *
     * @return {@link ResponseEntity} с объектом {@link RuleStatsResponse}, содержащим список счётчиков по правилам
     */
    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getStats() {
        List<RuleStatistics> stats = statisticsRepository.findAllWithRules();

        List<RuleStatsItem> items = stats.stream()
                .map(s -> new RuleStatsItem(s.getRuleId(), s.getCount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RuleStatsResponse(items));
    }

    /**
     * DTO-обёртка для ответа со статистикой срабатываний правил.
     */
    public static class RuleStatsResponse {
        private List<RuleStatsItem> stats;

        /**
         * Создаёт объект ответа со статистикой.
         *
         * @param stats список элементов статистики по правилам
         */
        public RuleStatsResponse(List<RuleStatsItem> stats) {
            this.stats = stats;
        }

        /**
         * Возвращает список элементов статистики срабатываний правил.
         *
         * @return список объектов {@link RuleStatsItem}
         */
        public List<RuleStatsItem> getStats() {
            return stats;
        }
    }

    /**
     * Элемент статистики срабатываний конкретного динамического правила.
     */
    public static class RuleStatsItem {
        private Long rule_id;
        private Long count;

        /**
         * Создаёт запись статистики по правилу.
         *
         * @param rule_id идентификатор динамического правила
         * @param count   количество срабатываний правила
         */
        public RuleStatsItem(Long rule_id, Long count) {
            this.rule_id = rule_id;
            this.count = count;
        }

        /**
         * Возвращает идентификатор динамического правила.
         *
         * @return идентификатор правила
         */
        public Long getRule_id() {
            return rule_id;
        }

        /**
         * Возвращает количество срабатываний правила.
         *
         * @return количество срабатываний
         */
        public Long getCount() {
            return count;
        }
    }
}