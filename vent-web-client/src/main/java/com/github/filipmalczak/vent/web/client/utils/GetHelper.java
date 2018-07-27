package com.github.filipmalczak.vent.web.client.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetHelper {
    @Getter private WebClient webClient;

    public <T> T getAs(String url, Class<T> clazz){
        return webClient.
            get().uri(url).
            exchange().
            flatMap(resp ->
                resp.bodyToMono(clazz)
            ).block();
    }

    public static GetHelper over(WebClient webClient){
        return new GetHelper(webClient);
    }
}
