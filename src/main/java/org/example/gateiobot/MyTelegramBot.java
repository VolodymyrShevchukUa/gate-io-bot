package org.example.gateiobot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class MyTelegramBot extends TelegramWebhookBot {

    @Value("${ROOT_ID}")
    private String rootId;
    @Autowired
    BotService botService;

    @Value("${BASE_URL}")
    private String baseUrl;
    @Value("${GATE_IO_API_KEY}")
    private String apiKey;
    @Value("${GATE_IO_USER_NAME}")
    private String userName;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/setcoin")) {
                String chatId = update.getMessage().getChatId().toString();
                String messageText = update.getMessage().getText().split(" ")[1];
                List<InterestRate> interestRates = botService.setNewSeekingCoin(messageText);
                InterestRate interestRate = interestRates.get(0);
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                String messageResponse =
                        "<b>Встановлено нову монету :</b> " + messageText + "\n" +
                                "<b>Гнучка ставка:</b> " + interestRate.interestRateHour() + " / " + interestRate.interestRateYear() + "\n" +
                                "<b>7-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor7DayHour() + " / " + interestRate.cryptoLoanFixedRateFor7DayYear() + "\n" +
                                "<b>30-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor30DayHour() + " / " + interestRate.cryptoLoanFixedRateFor30DayYear() + "\n";
                message.setText(messageResponse);
                message.setChatId(chatId);
                return message;
            } else if (update.getMessage().getText().startsWith("/kurs")) {
                String chatId = update.getMessage().getChatId().toString();
                String[] strings = update.getMessage().getText().split(" ");
                List<InterestRate> interestRates = botService.setNewSeekingCoin(strings);
                InterestRate interestRate = interestRates.get(0);
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                String messageResponse =
                        "<b>Гнучка ставка:</b> " + interestRate.interestRateHour() + " / " + interestRate.interestRateYear() + "\n" +
                                "<b>7-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor7DayHour() + " / " + interestRate.cryptoLoanFixedRateFor7DayYear() + "\n" +
                                "<b>30-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor30DayHour() + " / " + interestRate.cryptoLoanFixedRateFor30DayYear() + "\n";
                message.setText(messageResponse);
                message.setChatId(chatId);
                return message;
            }
        } catch (RuntimeException e) {
            handleException(update, e);
        }
        return null;
    }

    private void handleException(Update update, RuntimeException e) {
        SendMessage message = new SendMessage();
        message.setParseMode("HTML");

        message.setText("Виникла помилка " + e.getMessage());
        message.setChatId(update.getMessage().getChatId());
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    private void sendUpdates() {
        String seekingCoin = botService.getSeekingCoin();
        List<InterestRate> interests = botService.getInterests(seekingCoin);
        InterestRate interestRate = interests.get(0);
        InterestRate currentInterest = botService.getCurrentInterest(seekingCoin);
        if (!Objects.equals(interestRate, currentInterest)) {
            SendMessage message = new SendMessage();
            message.setParseMode("HTML");
            message.setText("<b>Interest Rate Alert!</b>\n" +
                    "<b>Гнучка ставка:</b> " + interestRate.interestRateHour() + " / " + interestRate.interestRateYear() + "\n" +
                    "<b>7-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor7DayHour() + " / " + interestRate.cryptoLoanFixedRateFor7DayYear() + "\n" +
                    "<b>30-денна фіксована ставка:</b> " + interestRate.cryptoLoanFixedRateFor30DayHour() + " / " + interestRate.cryptoLoanFixedRateFor30DayYear() + "\n");
            message.setChatId(rootId);
            sendTelegramMessage(message);
        }
    }

    private void sendTelegramMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return apiKey;
    }

    @Override
    public String getBotPath() {
        return "web-hook";
    }

    @PostConstruct
    public void initWebhook() {
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(baseUrl + "/web-hook");
        BotCommand setCoinCommand = new BotCommand("setcoin", "Set the coin symbol");
        BotCommand kursCommand = new BotCommand("kurs", "Get interests for current coin, or you can ask another one");
        SetMyCommands commands = new SetMyCommands(Arrays.asList(setCoinCommand, kursCommand), null, null);

        try {
            this.execute(commands);
            this.execute(setWebhook);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
