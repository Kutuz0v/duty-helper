package scpc.dutyhelper.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableAsync
public class ConcurrentConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .interceptors(new HostHeaderInterceptor())
                .build();
    }

    private static class HostHeaderInterceptor implements ClientHttpRequestInterceptor {
        @NotNull
        @Override
        public ClientHttpResponse intercept(HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            URI uri = request.getURI();
            headers.set(HttpHeaders.HOST, uri.getHost());
            headers.setAccept(List.of(MediaType.ALL));
            headers.set(HttpHeaders.USER_AGENT, "PostmanRuntime/7.31.0");
            return execution.execute(request, body);
        }
    }
}
