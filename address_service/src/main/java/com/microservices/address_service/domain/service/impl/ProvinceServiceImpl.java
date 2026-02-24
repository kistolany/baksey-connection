package com.microservices.address_service.domain.service.impl;
import com.microservices.address_service.appicatioin.request.ProvinceRequest;
import com.microservices.address_service.appicatioin.response.ProvinceResponse;
import com.microservices.address_service.domain.db_repo.ProvinceDomainRepo;
import com.microservices.address_service.domain.mapper.ProvinceMapper;
import com.microservices.address_service.domain.model.ProvinceModel;
import com.microservices.address_service.domain.service.ProvinceService;
import com.microservices.address_service.repository.ProvinceRepository;
import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import com.microservices.common_service.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceMapper provinceMapper;
    private final ProvinceDomainRepo provinceDomainRepo;
    private final ProvinceRepository provinceRepository;

    @Override
    public ResponseModel<List<ProvinceResponse>> findAll() {
        log.info("Start: Getting Order with body");

        // Call getAll repo and map to response
        List<ProvinceResponse> listResponse = provinceDomainRepo.findAll().stream().map(provinceMapper::tProvinceResponse).toList();

        log.info("Finish: Getting Order with body : {} with successfully", CommonUtils.toJsonString(listResponse));
        return ResponseModel.success(listResponse);
    }

    @Override
    public ResponseModel<ProvinceResponse> findById(UUID id) {
        log.info("Start: Getting Order with id: {}",id);

        // Validate existing province
        ProvinceModel model = provinceDomainRepo.findById(id).orElseThrow(() -> {
            log.error("Province id : {} is not found !", id);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Province id : %s is not found !", id));
        });

        log.info("Finish: Getting Order with id : {} with successfully",id);
        return ResponseModel.success(provinceMapper.tProvinceResponse(model));
    }

    @Override
    public ResponseModel<ProvinceResponse> findByCode(String code) {
        log.info("Start: Getting Order with code : {}",code);

        // Validate for existing code
        ProvinceModel model = provinceDomainRepo.findByCode(code).orElseThrow(() -> {
            log.error("Province code : {} is not found !", code);
            return new ApiException(ResponseConstants.ResponseStatus.NOT_FOUND, String.format("Province code : %s is not found !", code));
        });

        log.info("Finish: Getting Order with code : {} with successfully",code);
        return ResponseModel.success(provinceMapper.tProvinceResponse(model));
    }

    @Override
    public ResponseModel<ProvinceResponse> save(ProvinceRequest request) {

        log.info("Start: Creating Province with body : {}",
                CommonUtils.toJsonString(request));

        // Validate existing code
        if (provinceRepository.existsByCode(request.getCode())) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Province code : %s is already exist !", request.getCode())
            );
        }

        if (provinceRepository.existsByNameKh(request.getNameKh())) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Province nameKh : %s is already exist !", request.getNameKh())
            );
        }

        if (provinceRepository.existsByNameEn(request.getNameEn())) {
            throw new ApiException(
                    ResponseConstants.ResponseStatus.BAD_REQUEST,
                    String.format("Province nameEn : %s is already exist !", request.getNameEn())
            );
        }

        // Save province
        ProvinceModel savedModel = provinceDomainRepo.save(
                request.getNameEn(),
                request.getNameKh(),
                request.getCode()
        );

        log.info("Finish: Saved Province successfully");

        // ✅ Return response data
        return ResponseModel.success(
                provinceMapper.tProvinceResponse(savedModel)
        );
    }
}
