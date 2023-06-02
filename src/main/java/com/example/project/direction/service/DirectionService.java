package com.example.project.direction.service;

import com.example.project.api.dto.DocumentDto;
import com.example.project.api.service.KakaoCategorySearchService;
import com.example.project.direction.entity.Direction;
import com.example.project.direction.repository.DirectionRepository;
import com.example.project.pharmacy.dto.PharmacyDto;
import com.example.project.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3;  // 약국 최대 검색 갯수
    private static final double RADIUS_KM = 10.0; // 반경 10km

    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;
    private final KakaoCategorySearchService kakaoCategorySearchService;

    private final Base62Service base62Service;

    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList){
        if(CollectionUtils.isEmpty(directionList)) return Collections.emptyList();
        return directionRepository.saveAll(directionList);
    }

    @Transactional(readOnly = true)
    public String findDirectionUrlById(String encodedId) {

        Long decodedId = base62Service.decodeDirectionId(encodedId);
        Direction direction = directionRepository.findById(decodedId).orElse(null);

        String params = String.join(",", direction.getTargetPharmacyName(),
                String.valueOf(direction.getTargetLatitude()), String.valueOf(direction.getTargetLongitude()));
        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params)
                .toUriString();

        return result;
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto){ // 공공기관에서 가져온 약국데이터를 토대로 거리 계산 알고리즘을 통해 가까운 3개 약국 리스트 리턴

        if(Objects.isNull(documentDto)) return Collections.emptyList();

        return pharmacySearchService.searchPharmacyDtoList().stream()
                .map(pharmacyDto -> Direction.builder().inputAddress(documentDto.getAddressName())
                        .inputLatitude(documentDto.getLatitude())
                        .inputLongitude(documentDto.getLongitude())
                        .targetPharmacyName(pharmacyDto.getPharmacyName())
                        .targetAddress(pharmacyDto.getPharmacyAddress())
                        .targetLatitude(pharmacyDto.getLatitude())
                        .targetLongitude(pharmacyDto.getLongitude())
                        .distance(calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                pharmacyDto.getLatitude(), pharmacyDto.getLongitude()))
                        .build())
                .filter(direction -> direction.getDistance() < RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))   // 객체 리스트 정렬
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());// 거리계산 안고리즘을 이용하여, 고객과 약숙 사이의 거리를 계산하고 sort
    }


    // pharmacy search by category kakao api   (위의 메소드를 사용하면  공공데이터를 가져와 정적으로 사용하게 될 뿐더러 전국의 약국데이터가 너무 많기 때문에 비효율적)
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto, String category){

        if(Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        return kakaoCategorySearchService.requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM, category)
                .getDocumentList()
                .stream().map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.getAddressName())
                                .inputLatitude(inputDocumentDto.getLatitude())
                                .inputLongitude(inputDocumentDto.getLongitude())
                                .targetPharmacyName(resultDocumentDto.getPlaceName())
                                .targetAddress(resultDocumentDto.getAddressName())
                                .targetLatitude(resultDocumentDto.getLatitude())
                                .targetLongitude(resultDocumentDto.getLongitude())
                                .distance(resultDocumentDto.getDistance() * 0.001)  // km 단위
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
