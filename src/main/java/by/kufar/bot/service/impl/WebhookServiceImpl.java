package by.kufar.bot.service.impl;

import by.kufar.bot.service.WebhookService;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {
    private static final String FIND_ACTUAL_WEBHOOK_URL = "https://api.telegram.org/bot{token}/getWebhookInfo";
    private static final String REGISTER_NEW_WEBHOOK_URL = "https://api.telegram.org/bot{token}/setWebhook?url={url}";


    private final RestTemplate restTemplate;

    @Override
    public void registerWebhook(String token, String path) {
        ResponseEntity<ResponseStatus> response = restTemplate.getForEntity(REGISTER_NEW_WEBHOOK_URL, ResponseStatus.class, token, path);
        if (!validateResponseStatus(response)) {
            throw new RuntimeException("Can't find actual webhook url");
        }
    }

    @Override
    public String findActualWebhook(String token) {
        ResponseEntity<GetWebhookResponse> response = restTemplate.getForEntity(FIND_ACTUAL_WEBHOOK_URL, GetWebhookResponse.class, token);
        if (validateResponseStatus(response)) {
            GetWebhookResponse webhookResponse = response.getBody();
            return webhookResponse.getWebhook().getUrl();
        } else {
            throw new RuntimeException("Can't find actual webhook url");
        }
    }

    private boolean validateResponseStatus(ResponseEntity<? extends ResponseStatus> response) {
        boolean valid = false;
        if (response.getStatusCode() == HttpStatus.OK) {
            ResponseStatus body = response.getBody();
            if (body != null) {
                valid = body.isOk();
            }
        }
        return valid;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ResponseStatus {
        private boolean ok;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GetWebhookResponse extends ResponseStatus {
        @JsonAlias("result")
        private WebhookUrl webhook;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class WebhookUrl {
        private String url;
    }
}
