package org.example.gateiobot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramWebhookBot {


    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${BASE_URL}")
    private String baseUrl;
    @Value("${GATE_IO_API_KEY}")
    private String apiKey;
    @Value("${GATE_IO_USER_NAME}")
    private String userName;


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();
            List<InterestRate> method = getInterests(messageText);
            InterestRate interestRate = method.get(0);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Гнучка ставка: " + interestRate.interestRateHour + "/" + interestRate.interestRateYear);

            return message;
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    //"getIoScheduledBot"
    @Override
    public String getBotToken() {
        return apiKey;
    }
    //"7019743216:AAF0pv36ihEkQup0zIDTXe-IaMA16iuSubY"

    @Override
    public String getBotPath() {
        return "web-hook";
    }

    @PostConstruct
    public void initWebhook() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(baseUrl + "/web-hook");
        try {
            this.execute(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InterestRate> getInterests(String search) {

        String url = "https://www.gate.io/api/web/v1/uniloan/crypto-loan-market-list?page=1&limit=10&search_coin=" + search + "&fixed_type=0";

        HttpHeaders headers = new HttpHeaders();
        headers.add("authority", "www.gate.io");
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            return parseCryptoLoanMarketResponse(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InterestRate> parseCryptoLoanMarketResponse(String responseBody) throws IOException {
        List<InterestRate> interestRates = new ArrayList<>();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode listNode = rootNode.path("data").path("list");

        for (JsonNode itemNode : listNode) {
            double interestRateHourValue = itemNode.path("interest_rate_hour").asDouble();
            double interestRateYearValue = itemNode.path("interest_rate_year").asDouble();

            String interestRateHour = String.format("%.4f%%", interestRateHourValue * 100);
            String interestRateYear = String.format("%.2f%%", interestRateYearValue * 100);

            interestRates.add(new InterestRate(interestRateHour, interestRateYear));
        }
        return interestRates;
    }

    public record InterestRate(String interestRateHour, String interestRateYear) {
    }
}

//User(id=228899115, firstName=Vlad, isBot=false, lastName=null, userName=kudryavyy, languageCode=uk, canJoinGroups=null, canReadAllGroupMessages=null, supportInlineQueries=null, isPremium=null, addedToAttachmentMenu=null)