package bank_recommendations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

@Component
public class RecommendationBot extends TelegramLongPollingBot {

    private final RecommendationService recommendationService;
    private final RecommendationsRepository recommendationsRepository;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public RecommendationBot(
            RecommendationService recommendationService,
            RecommendationsRepository recommendationsRepository) {
        super(); // Убрали botToken отсюда, чтобы не было null
        this.recommendationService = recommendationService;
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (text.equals("/start")) {
            sendStart(chatId);
        } else if (text.startsWith("/recommend ")) {
            handleRecommend(chatId, text.substring("/recommend ".length()).trim());
        }
    }

    private void sendStart(long chatId) {
        String msg = "Привет! Я бот банковских рекомендаций.\n\n" +
                "Доступные команды:\n" +
                "/recommend <Имя Фамилия> — получить рекомендации.";
        sendMessage(chatId, msg);
    }

    private void handleRecommend(long chatId, String userName) {
        List<String> foundUsers = recommendationsRepository.findUserNames(userName);

        if (foundUsers.size() != 1) {
            sendMessage(chatId, "Пользователь не найден");
            return;
        }

        String fullName = foundUsers.get(0);
        UUID userId = recommendationsRepository.findUserIdByName(fullName);

        if (userId == null) {
            sendMessage(chatId, "Пользователь не найден");
            return;
        }

        List<RecommendationDto> recs = recommendationService.getRecommendations(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте ").append(fullName).append("\n\n");
        sb.append("Новые продукты для вас:\n");

        if (recs.isEmpty()) {
            sb.append("- Пока нет персональных рекомендаций");
        } else {
            for (RecommendationDto r : recs) {
                sb.append("- ").append(r.getName()).append(": ").append(r.getText()).append("\n");
            }
        }

        sendMessage(chatId, sb.toString());
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка Telegram: " + e.getMessage());
        }
    }
}