package com.lanhcare.service.impls;

import com.lanhcare.dto.icd.IcdEntityDTO;
import com.lanhcare.dto.icd.IcdTokenResponse;
import com.lanhcare.dto.icd.ReleaseDTO;
import com.lanhcare.entity.ICD11Chapter;
import com.lanhcare.entity.ICD11Status;
import com.lanhcare.repository.IcdChapterRepository;
import com.lanhcare.service.IcdApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IcdApiServiceImpl implements IcdApiService {
    private final IcdChapterRepository icdChapterRepository;

    @Value("${icd.client.id}")
    private String clientId;

    @Value("${icd.client.secret}")
    private String clientSecret;

    @Value("${icd.auth.server.url}")
    private String authServerUrl;

    @Value("${icd.base.url}")
    private String baseUrl;

    @Value("${icd.linearization.name}")
    private String linearizationName;

    @Value("${icd.release}")
    private String release;


    @Override
    public List<ICD11Chapter> seedICDData() {
        List<String> chapterCodes = getChapterCodes(fetchReleaseChapters());
        List<IcdEntityDTO> chapters = new ArrayList<>();
        List<ICD11Chapter> chapterEntities = new ArrayList<>();

        for (String chapterCode : chapterCodes) {
            IcdEntityDTO entityDTO = fetchIcdEntity(chapterCode);
            chapterEntities.add(mapToICD11Chapter(entityDTO));
            chapters.add(entityDTO);
        }

        return chapterEntities;
    }

    /// Lấy thông tin cơ bản của Linearization
    /// @return Thông tin chính là list các mã của chapter cấp cao nhất
    @Override
    public ReleaseDTO fetchReleaseChapters() {
        String token = getAccessToken().getAccessToken();
        String uri = String.format("%s/%s/%s", baseUrl, release, linearizationName);

        return WebClient.create()
                .get()
                .uri(uri)
                .header("API-Version", "v2")
                .header("Accept-Language", "en")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ReleaseDTO.class)
                .block();
    }

    /// Lấy danh sách các chapter code đã lọc từ danh sách uri
    @Override
    public List<String> getChapterCodes(ReleaseDTO releaseDTO) {
        return releaseDTO.getChild().stream()
                .map(this::extractIdFromUri)
                .toList();
    }

    /// Lấy Access Token hợp lệ từ ICD Access Management Server
    /// Token (ghi nhận ngày 12-07-2025) có thời hạn 1 tiếng
    /// @return Chuỗi access token hợp lệ và thời hạn kết thúc
    @Override
    public IcdTokenResponse getAccessToken() {
        // Yêu cầu lấy Token mới - Sử dụng WebClient
        Mono<IcdTokenResponse> icdResponseMono = WebClient.create().post()
                .uri(authServerUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                // Thêm credentials vào body
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(IcdTokenResponse.class);

        // Ép Mono về kết quả đồng bộ và trả ra
        return icdResponseMono.block();
    }

    /// Lấy chi tiết một Entity ICD-11 (Chapter, Group, hoặc Code)
    /// @param entityId ID của Entity cần lấy (ví dụ: 1435254666, hoặc ID của mã bệnh con)
    @Override
    public IcdEntityDTO fetchIcdEntity(String entityId) {
        String token = getAccessToken().getAccessToken();
        String uri = String.format("%s/%s/%s/%s", baseUrl, release, linearizationName, entityId);

        return WebClient.create()
                .get()
                .uri(uri)
                .header("API-Version", "v2")
                .header("Accept-Language", "en")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(IcdEntityDTO.class)
                .block();
    }

    /// Hàm hỗ trợ: Trích xuất ID từ URI (Ví dụ: .../entity/1170831944 -> 1170831944)
    private String extractIdFromUri(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    /// Hàm hỗ trợ: Chuyển đổi ICD entity sang ICD chapter (own database)
    private ICD11Chapter mapToICD11Chapter(IcdEntityDTO icdEntityDTO) {
        return ICD11Chapter.builder()
                .chapterUri(icdEntityDTO.getIcdUri())
                .chapterCode(icdEntityDTO.getIcdCode())
                .originalTitleEn(icdEntityDTO.getOriginalTitleEn().getValue())
                .releaseId(release)
                .status(ICD11Status.ACTIVE)
                .build();
    }

}
