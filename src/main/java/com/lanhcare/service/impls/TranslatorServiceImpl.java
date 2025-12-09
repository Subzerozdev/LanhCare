package com.lanhcare.service.impls;

import com.lanhcare.dto.translator.TranslatorRequest;
import com.lanhcare.dto.translator.TranslatorResponse;
import com.lanhcare.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TranslatorServiceImpl implements TranslatorService {
    @Value("${translate.endpoint}")
    private String endpoint;

    @Value("${translate.api.key}")
    private String apiKey;

    /// Dịch chuỗi thành tiếng Việt
    /// <a href="https://translate-api.com/">Translate API</a>
    /// @param text Chuỗi tiếng anh cần được dịch
     /// @return Chuỗi đã dịch thành tiếng Việt và các thông tin khác
    @Override
    public TranslatorResponse translateToVietnamese(String text) {
        TranslatorRequest request = TranslatorRequest.builder()
                .text(text)
                .targetLanguage("Vietnamese")
                .build();

        Mono<TranslatorResponse> translatedText = WebClient.create().post()
                .uri(endpoint)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TranslatorResponse.class);

        return translatedText.block();
    }
}
