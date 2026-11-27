package bank_recommendations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST-контроллер для служебного управления и мониторинга сервиса.
 * <p>
 * Предоставляет эндпоинты по пути {@code /management} для получения информации о сборке
 * и принудительного сброса кешей транзакций.
 */
@RestController
@RequestMapping("/management")
public class ManagementController {

    // Делаем поле необязательным, чтобы приложение запускалось даже без сгенерированной версии
    @Autowired(required = false)
    private BuildProperties buildProperties;

    private final RecommendationsRepository recommendationsRepository;

    /**
     * Создаёт контроллер управления сервисом.
     *
     * @param recommendationsRepository репозиторий транзакций, содержащий управляемый кеш
     */
    public ManagementController(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    /**
     * Возвращает имя и версию текущего экземпляра приложения.
     * <p>
     * Использует {@link BuildProperties}, если информация о сборке доступна; иначе возвращает значения по умолчанию.
     *
     * @return {@link ResponseEntity} с картой {@code name} и {@code version}
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        Map<String, String> info = new HashMap<>();

        if (buildProperties != null) {
            info.put("name", buildProperties.getArtifact());
            info.put("version", buildProperties.getVersion());
        } else {
            info.put("name", "bank-recommendations");
            info.put("version", "1.0.0");
        }

        return ResponseEntity.ok(info);
    }

    /**
     * Принудительно очищает кеши агрегаций и счётчиков транзакций в {@link RecommendationsRepository}.
     *
     * @return {@link ResponseEntity} со статусом {@code 200 OK}
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        recommendationsRepository.clearCaches();
        return ResponseEntity.ok().build();
    }
}