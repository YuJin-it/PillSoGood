package com.ssafy.project.global.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.ssafy.project.domain.lists.combinationProhibition.dto.CombinationProhibitionDTO;
import com.ssafy.project.domain.lists.medicineInformation.dto.MedicineInformationDTO;
import com.ssafy.project.domain.medicine.dto.MedicinePreviewDTO;
import com.ssafy.project.domain.medicine.entity.Category;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

    private final RestHighLevelClient client;

    @Autowired
    public ElasticsearchService(RestHighLevelClient client) {
        this.client = client;
    }

    // 알약 전체보기 검색
    public List<MedicinePreviewDTO> searchMedicinePreview(String prefix, List<Category> categories) {
        List<MedicinePreviewDTO> resultList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 알 수 없는 필드 무시
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);  // 필드명 매칭 설정

        try {
            // medicine_index 인덱스에서 모든 데이터를 조회
            SearchRequest searchRequest = new SearchRequest("medicine_index");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // BoolQuery를 사용하여 prefix 및 카테고리 조건 추가
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            // prefix가 있으면 prefix 조건 추가
            if (prefix != null) {
                boolQueryBuilder.must(QueryBuilders.prefixQuery("kor_name", prefix));
            }
            else {
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
            }

            // 카테고리가 있을 경우 카테고리 필터 추가
            if (categories != null && !categories.isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("category", categories.stream().map(Category::name).toArray(String[]::new)));
            }

            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.sort("id", SortOrder.ASC);  // id를 기준으로 오름차순 정렬
            searchSourceBuilder.size(20);  // 20개의 문서를 반환
            searchRequest.source(searchSourceBuilder);

            // Elasticsearch에서 검색 결과 가져오기
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                // 검색된 데이터를 MedicinePreviewDTO로 변환
                MedicinePreviewDTO medicinePreviewDTO = objectMapper.readValue(hit.getSourceAsString(), MedicinePreviewDTO.class);
                resultList.add(medicinePreviewDTO);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    // 약 정보
    public List<MedicineInformationDTO> searchMedicineInformation(int medicineId) {
        List<MedicineInformationDTO> resultList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 알 수 없는 필드 무시
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);  // 필드명 매칭 설정

        try {
            // medicine_index 인덱스에서 모든 데이터를 조회
            SearchRequest searchRequest = new SearchRequest("medicine_information_index");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // medicineId로 검색 조건 추가
            searchSourceBuilder.query(QueryBuilders.matchQuery("medicine_id", medicineId));

            searchSourceBuilder.sort("id", SortOrder.ASC);  // id를 기준으로 오름차순 정렬
            searchSourceBuilder.size(20);  // 최대 20개의 문서를 반환
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                // 검색된 데이터를 MedicineInformationDTO로 변환
                MedicineInformationDTO medicineInformationDTO = objectMapper.readValue(hit.getSourceAsString(), MedicineInformationDTO.class);
                resultList.add(medicineInformationDTO);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    // 임산부, 노인, 제한, 나이대별,
    public <T> T searchProhibition(Class<T> DTO, String index, int medicineId) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Elasticsearch 쿼리 생성
            SearchRequest searchRequest = new SearchRequest(index);  // 인덱스 이름 지정
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            searchSourceBuilder.query(QueryBuilders.matchQuery("medicine_id", medicineId));
            searchSourceBuilder.sort("id", SortOrder.ASC);
            searchRequest.source(searchSourceBuilder);

            // Elasticsearch에서 검색 결과 가져오기
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getHits().length > 0) {
                SearchHit hit = searchResponse.getHits().getHits()[0];  // 첫 번째 검색 결과
                return objectMapper.readValue(hit.getSourceAsString(), DTO);  // DTO로 변환하여 반환
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 금기 조합 조회
    public CombinationProhibitionDTO searchCombinationProhibition(int medicineIdA, int medicineIdB) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Elasticsearch 쿼리 생성
            SearchRequest searchRequest = new SearchRequest("combination_prohibition_index");  // 인덱스 이름 지정
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 두 필드에 대한 AND 조건을 설정하는 boolQuery
            searchSourceBuilder.query(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("medicine_id_a", medicineIdA))
                    .must(QueryBuilders.matchQuery("medicine_id_b", medicineIdB))
            );
            searchRequest.source(searchSourceBuilder);

            // Elasticsearch에서 검색 결과 가져오기
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getHits().length > 0) {
                SearchHit hit = searchResponse.getHits().getHits()[0];  // 첫 번째 검색 결과
                return objectMapper.readValue(hit.getSourceAsString(), CombinationProhibitionDTO.class);  // DTO로 변환하여 반환
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
