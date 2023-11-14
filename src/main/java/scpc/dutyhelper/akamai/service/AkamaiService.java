package scpc.dutyhelper.akamai.service;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.akamai.model.AkamaiStatistic;
import scpc.dutyhelper.akamai.repository.AkamaiStatisticRepository;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.util.AdminNotifier;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class AkamaiService {
    private final AkamaiStatisticRepository repository;
    private final AdminNotifier adminNotifier;
    private final TelegramService telegramService;

    @Value("${akamai.host}")
    private String host;
    @Value("${akamai.config-id}")
    private String configId;
    @Value("${akamai.access-token}")
    private String accessToken;
    @Value("${akamai.client-token}")
    private String clientToken;
    @Value("${akamai.client-secret}")
    private String clientSecret;
    @Value("${akamai.limit-max-impact-hit-sec}")
    private Integer LIMIT_MAX_IMPACT_TO_NOTIFY;


    /**
     * Temporary disabled (Akamai service is not available)
     */
    //@Scheduled(fixedRate = 60_000)
    public void monitorAkamai() {
        LocalDateTime from = LocalDateTime.now().minusMinutes(2);
        LocalDateTime to = LocalDateTime.now().minusMinutes(1);

        String response = callAkamaiApiTimeBasedMode(
                from.atZone(ZoneId.systemDefault()).toEpochSecond(),
                to.atZone(ZoneId.systemDefault()).toEpochSecond()
        );

        if (response == null) {
            adminNotifier.notifyAdmin("Акамай не повернув даних, ймовірна помилка на сервері!");
            return;
        }

        int count = (int) response.lines().count();
        int hitSec = count / 60;
        AkamaiStatistic akamaiStatistic = repository.save(AkamaiStatistic.builder()
                .id(0L)
                .fromTime(from)
                .toTime(to)
                .hitSec(hitSec)
                .count(count)
                .size(response.getBytes().length)
                .build());

        if (akamaiStatistic.getHitSec() > LIMIT_MAX_IMPACT_TO_NOTIFY) {
            StringBuilder message = new StringBuilder("‼️Akamai фіксує атаку " + hitSec + " зап/сек");
            if (hitSec > 6_500)
                message.append(" або більше!");
            else message.append("!");
            message.append("\nЦе сповіщення тільки повідомляє про атаку, тому не гарантує точності параметрів атаки. ");
            message.append("Для більш точної інформації рекомендується використати сервіс Akamai.");
            telegramService.sendMessageForAll(message.toString());
        }

    }

    public String callAkamaiApiTimeBasedMode(Long from, Long to) {
        ClientCredential credential = ClientCredential.builder()
                .accessToken(accessToken)
                .clientToken(clientToken)
                .clientSecret(clientSecret)
                .host(host)
                .build();

        String query = String.format("from=%s&to=%s&limit=%s", from, to, 400_000);

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
                .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
                .build()
        ) {
            URI uri = new URI("https", host, "/siem/v1/configs/" + configId, query, null);
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                String errorMessage = String.format("Запит до Akamai повернув код: %d\nResponse: %s", statusCode, EntityUtils.toString(response.getEntity()));
                throw new RuntimeException(errorMessage);
            }

            return EntityUtils.toString(response.getEntity());
        } catch (IOException | URISyntaxException | RuntimeException e) {
            adminNotifier.notifyAdmin(e.getMessage());
            log.error(e.getMessage());
        }

        return null;
    }

}
