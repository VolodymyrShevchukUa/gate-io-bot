package org.example.gateiobot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String seekingCoin = "MNT";
    private final Map<String, InterestRate> interestMap = new HashMap<>();

    public List<InterestRate> getInterests(String search) {

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

    public List<InterestRate> setNewSeekingCoin(String newCoin) {
        List<InterestRate> interests = getInterests(newCoin);
        InterestRate interestRate = interests.get(0);
        interestMap.put(newCoin, interestRate);
        this.seekingCoin = newCoin;
        return interests;
    }

    public List<InterestRate> setNewSeekingCoin(String[] seekingCoin) {
        return getInterests(seekingCoin.length == 2 ? seekingCoin[1] : this.seekingCoin);
    }

    public String getSeekingCoin() {
        return seekingCoin;
    }

    public InterestRate getCurrentInterest(String key) {
        return interestMap.get(key);
    }

    public void setNewInterests(String key, InterestRate interestRate) {
        this.interestMap.put(key, interestRate);
    }

    public List<InterestRate> parseCryptoLoanMarketResponse(String responseBody) throws IOException {
        List<InterestRate> interestRates = new ArrayList<>();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode listNode = rootNode.path("data").path("list");

        for (JsonNode itemNode : listNode) {
            InterestRate loanData = new InterestRate(
                    itemNode.path("asset").asText(),
                    itemNode.path("borrowed_amount").asText(),
                    itemNode.path("borrowed_amount_usdt").asText(),
                    itemNode.path("borrow_available").asText(),
                    itemNode.path("borrow_available_usdt").asText(),

                    formatAsPercentageSmall(itemNode.path("interest_rate_hour").asDouble()),
                    formatAsPercentage(itemNode.path("interest_rate_year").asDouble()),
                    formatAsPercentageSmall(itemNode.path("cryptoLoanFixedRateFor7DayHour").asDouble()),
                    formatAsPercentage(itemNode.path("cryptoLoanFixedRateFor7DayYear").asDouble()),
                    formatAsPercentageSmall(itemNode.path("cryptoLoanFixedRateFor30DayHour").asDouble()),
                    formatAsPercentage(itemNode.path("cryptoLoanFixedRateFor30DayYear").asDouble())
            );


            interestRates.add(loanData);
        }
        return interestRates;
    }

    private static String formatAsPercentage(double rate) {
        return String.format("%.2f%%", rate * 100);
    }

    private static String formatAsPercentageSmall(double rate) {
        return String.format("%.6f%%", rate * 100);
    }
}
