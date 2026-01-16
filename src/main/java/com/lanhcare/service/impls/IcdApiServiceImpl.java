package com.lanhcare.service.impls;

import com.lanhcare.dto.icd.IcdEntityDTO;
import com.lanhcare.dto.icd.IcdEntityRequest;
import com.lanhcare.dto.icd.IcdTokenResponse;
import com.lanhcare.dto.icd.ReleaseDTO;
import com.lanhcare.entity.ICD11Chapter;
import com.lanhcare.entity.ICD11Code;
import com.lanhcare.enums.ICD11Status;
import com.lanhcare.exception.exps.ICD11Exception;
import com.lanhcare.repository.ICD11CodeRepository;
import com.lanhcare.repository.IcdChapterRepository;
import com.lanhcare.service.IcdApiService;
import com.lanhcare.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IcdApiServiceImpl implements IcdApiService {
    private final IcdChapterRepository icdChapterRepository;
    private final ICD11CodeRepository icd11CodeRepository;

    @Value("${icd.client.id}")
    private String clientId;

    @Value("${icd.client.secret}")
    private String clientSecret;

    @Value("${icd.auth.server.url}")
    private String authServerUrl;

    @Value("${icd.base.url}")
    private String baseUrl;

    @Value("${icd.chapter.base.url}")
    private String chapterBaseUrl;

    @Value("${icd.linearization.name}")
    private String linearizationName;

    @Value("${icd.release}")
    private String release;

    private String cachedToken;

    private String getValidToken() {
        if (cachedToken == null) {
            cachedToken = getAccessToken().getToken();
        }
        return cachedToken;
    }

    @Override
    public void seedChaptersData() {
        List<String> chapterCodes = getChapterCodes(fetchReleaseChapters());
        List<ICD11Chapter> chapters = chapterCodes.stream()
                .map(chapterCode -> {
                    IcdEntityDTO entityDTO = fetchIcdEntity(chapterCode);
                    return mapToICD11Chapter(entityDTO);
                }).toList();

        icdChapterRepository.saveAll(chapters);
    }

    @Override
    public void seedSampleCodesData() {
        Map<String, List<String>> codes = getSampleData();
        List<ICD11Code> savedCodes = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : codes.entrySet()) {
            String chapterUri = String.format("%s/%s/%s/%s", chapterBaseUrl, release, linearizationName, entry.getKey());
            ICD11Chapter chapterEntity = getChapterById(chapterUri);

            List<String> sampleCodes = entry.getValue();

            for (String sampleCode : sampleCodes) {
                IcdEntityDTO entityDTO = fetchIcdEntity(sampleCode);
                ICD11Code convertEntity = mapToICD11Code(entityDTO, chapterEntity, null);
                savedCodes.add(convertEntity);
            }
        }

        icd11CodeRepository.saveAll(savedCodes);

    }

    @Override
    public void seedICDCode(IcdEntityRequest request) {
        IcdEntityDTO entityDTO = fetchIcdEntity(request.getCode());
        ICD11Chapter chapter = getChapterById(request.getChapterId());
        ICD11Code convertEntity = mapToICD11Code(entityDTO, chapter, null);
        icd11CodeRepository.save(convertEntity);
    }

    @Override
    public ICD11Chapter getChapterById(String id) {
        return icdChapterRepository.findById(id)
                .orElseThrow(() -> new ICD11Exception("Chapter not found with id " + id));
    }

    @Override
    public List<ICD11Chapter> seedICDData() {
        List<String> chapterCodes = getChapterCodes(fetchReleaseChapters());
        List<ICD11Chapter> chapters = new ArrayList<>();

        for (String chapterCode : chapterCodes) {
            IcdEntityDTO entityDTO = fetchIcdEntity(chapterCode);
            ICD11Chapter convertEntity = mapToICD11Chapter(entityDTO);
            convertEntity.getCodes().clear();
            convertEntity.getCodes().addAll(
                    seedICDChildren(entityDTO.getChildren(), convertEntity, null)
            );
            chapters.add(convertEntity);
        }

        icdChapterRepository.saveAll(chapters);
        return chapters;
    }

    private List<ICD11Code> seedICDChildren(List<String> children, ICD11Chapter chapter, ICD11Code parentCode) {
        if (children == null || children.isEmpty()) {
            return List.of();
        }

        List<ICD11Code> codes = new ArrayList<>();
        for (String code : children) {
            String codeID = extractIdFromUri(code);

            if (ValidationUtils.isValidIcdCode(codeID)) {
                IcdEntityDTO entityDTO = fetchIcdEntity(codeID);
                ICD11Code convertEntity = mapToICD11Code(entityDTO, chapter, parentCode);
                convertEntity.getChildren().clear();
                convertEntity.getChildren().addAll(
                        seedICDChildren(entityDTO.getChildren(), null, convertEntity)
                );
                codes.add(convertEntity);
            }
        }

        return codes;
    }

    /// Lấy thông tin cơ bản của Linearization
    /// @return Thông tin chính là list các mã của chapter cấp cao nhất
    @Override
    public ReleaseDTO fetchReleaseChapters() {
        String token = getValidToken();
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
        String token = getValidToken();
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
        return uri.substring(uri.lastIndexOf('/') + 1).trim();
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

    /// Hàm hỗ trợ: Chuyển đổi ICD entity sang ICD code (own database)
    private ICD11Code mapToICD11Code(IcdEntityDTO icdEntityDTO, ICD11Chapter icd11Chapter, ICD11Code parent) {
        return ICD11Code.builder()
                .icdUri(icdEntityDTO.getIcdUri())
                .chapter(icd11Chapter)
                .parent(parent)
                .originalTitleEn(icdEntityDTO.getOriginalTitleEn() != null ? icdEntityDTO.getOriginalTitleEn().getValue() : "N/A")
                .definitionEn(icdEntityDTO.getDefinitionEn() != null ? icdEntityDTO.getDefinitionEn().getValue() : "N/A")
                .longDefinitionEn(icdEntityDTO.getLongDefinitionEn() != null ? icdEntityDTO.getLongDefinitionEn().getValue() : "N/A")
                .classKind(icdEntityDTO.getClassKind())
                .icdCode(icdEntityDTO.getIcdCode())
                .status(ICD11Status.ACTIVE)
                .build();
    }

    private Map<String, List<String>> getSampleData() {
        Map<String, List<String>> codes = new HashMap<>();

        codes.put("426429380", List.of(
                // Tăng huyết áp
                "761947693",
                // Nhồi máu cơ tim
                "718946808", "1334938734", "1221742343",
                // Suy tim
                "2136808878", "437702716", "1754821737", "1594578199", "1992125824"
        ));

        codes.put("21500692", List.of(
                // Đái tháo đường type 1, type 2
                "1651053999", "119724091",
                // Mỡ trong máu
                "163750325", "1820945441", "405028062", "1599779547"
        ));

        codes.put("1296093776", List.of(
                // Đột quỵ
                "873092535", "636274910", "1363253283", "826335789",
                // Đau nửa đầu
                "2048783472", "525744634", "1336990680"
        ));

        codes.put("1435254666", List.of(
                // Viêm gan B
                "352087872", "1337277167"
        ));

        codes.put("868865918", List.of(
                // Cận thị
                "1666440799"
        ));

        codes.put("30659757", List.of(
                // Thoái hóa khớp
                "1196073446", "647013871"
        ));

        codes.put("1954798891", List.of(
                // Viêm khớp dạng thấp, Sốc phản vệ
                "576319925", "1095261642"
        ));

        codes.put("1473673350", List.of(
                // Sỏi thận
                "389168514"
        ));

        codes.put("1218729044", List.of(
                // Rối loạn tiền đình
                "1300772836"
        ));

        codes.put("1630407678", List.of(
                // Ung thư Thực quản
                "1417891145"
        ));

        codes.put("1766440644", List.of(
                // Rối loạn đông máu
                "337607970"
        ));

        codes.put("274880002", List.of(
                // Rối loạn nhịp sinh học
                "1996513332"
        ));

        codes.put("334423054", List.of(
                // Rối loạn lo âu
                "1712535455"
        ));

        codes.put("1639304259", List.of(
                // Mụn trứng cá
                "1892393023"
        ));

        codes.put("1256772020", List.of(
                // Viêm loét dạ dày - tá tràng
                "1437411258", "553678663",
                // Trào ngược dạ dày thực quản
                "417695496"
        ));

        codes.put("197934298", List.of(
                // Hen phế quản (Suyễn)
                "1870104478", "1461326813",
                // Viêm xoang
                "704335372", "1565925107", "14936621", "509821856", "1836987572"
        ));

        return codes;
    }
}
