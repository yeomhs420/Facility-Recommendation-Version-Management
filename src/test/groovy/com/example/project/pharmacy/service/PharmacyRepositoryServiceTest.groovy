package com.example.project.pharmacy.service

import com.example.project.AbstractIntegrationContainerBaseTest
import com.example.project.pharmacy.entity.Pharmacy
import com.example.project.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime


class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService;

    def setup(){
        pharmacyRepository.deleteAll()
    }

    def "pharmacyRepository update - dirty checking success"(){
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder().pharmacyAddress(inputAddress).pharmacyName(name).build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddress(entity.getId(), modifiedAddress)    // entity 의 변화가 생기면 DB에 반영하는 jpa dirty checking

        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == modifiedAddress

    }

    def "BaseTimeEntity 등록"(){
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder().pharmacyAddress(address).pharmacyName(name).build();

        when:
        pharmacyRepository.save(pharmacy)
        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getCreatedDate().isAfter(now)
    }
}
