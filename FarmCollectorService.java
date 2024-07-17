package com.assement.farmcollector.service;

import com.assement.farmcollector.model.CropDto;
import com.assement.farmcollector.model.CropHarvestedDetailDto;
import com.assement.farmcollector.model.CropPlantationDetailDto;
import com.assement.farmcollector.model.FarmCropsDto;
import com.assement.farmcollector.repository.FarmCropsDetail;
import com.assement.farmcollector.repository.FarmCropsDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FarmCollectorService {

    @Autowired
    FarmCropsDetailRepository farmCropsDetailRepository;

    public void addPlantationDetail(String farm, String season, String crop,
                                    CropPlantationDetailDto cropPlantationDetailDto) {
        FarmCropsDetail farmCropsDetail = FarmCropsDetail.builder().farm(farm).season(season).crop(crop)
                .plantingAreaInAcres(cropPlantationDetailDto.getPlantingAreaInAcres()).expectedProductionInTon(cropPlantationDetailDto.getExpectedProductionInTon())
                .build();

        farmCropsDetailRepository.save(farmCropsDetail);
    }

    public void updatePlantationDetail(String farm, String season, String crop, CropPlantationDetailDto cropPlantationDetailDto) {
        Optional<FarmCropsDetail> farmCropsDetail = farmCropsDetailRepository.findById(FarmCropsDetail
                .CompositeKey.builder().farm(farm).crop(crop).season(season).build());
        if (farmCropsDetail.isPresent()) {
            FarmCropsDetail farmCropsDtl = farmCropsDetail.get();
            farmCropsDtl.setExpectedProductionInTon(cropPlantationDetailDto.getExpectedProductionInTon());
            farmCropsDtl.setPlantingAreaInAcres(cropPlantationDetailDto.getPlantingAreaInAcres());
            farmCropsDetailRepository.save(farmCropsDtl);
        }
    }

    public void updateHarvestationDetail(String farm, String season, String crop, CropHarvestedDetailDto cropHarvestedDetailDto) {
        Optional<FarmCropsDetail> farmCropsDetail = farmCropsDetailRepository.findById(FarmCropsDetail
                .CompositeKey.builder().farm(farm).crop(crop).season(season).build());
        if (farmCropsDetail.isPresent()) {
            FarmCropsDetail farmCropsDtl = farmCropsDetail.get();
            farmCropsDtl.setActualHarvestedInTon(cropHarvestedDetailDto.getActualHarvestedInTon());
            farmCropsDetailRepository.save(farmCropsDtl);
        }
    }

    public List<FarmCropsDto> searchFarmCropDetails(String season, String farm) {
        List<FarmCropsDto> farmCropsDtos = new ArrayList<>();
        if (farm == null) {
            List<FarmCropsDetail> farmCropsDetails = farmCropsDetailRepository.findBySeason(season);
            Map<String, List<FarmCropsDetail>> farmCropMap =
                    farmCropsDetails.stream().collect(Collectors.groupingBy(FarmCropsDetail::getFarm));
            farmCropMap.forEach((key, value) -> {
                List<CropDto> cropDtos = value.stream().map(c -> CropDto.builder().crop(c.getCrop())
                        .expectedProductionInTon(c.getExpectedProductionInTon())
                        .actualHarvestedInTon(c.getActualHarvestedInTon()).build()).collect(Collectors.toList());
                farmCropsDtos.add(FarmCropsDto.builder().farm(key).crops(cropDtos).build());
            });
            return farmCropsDtos;
        } else {
            List<FarmCropsDetail> farmCropsDetails = farmCropsDetailRepository.findByFarmAndSeason(farm, season);
            List<CropDto> cropDtos = farmCropsDetails.stream().map(c -> CropDto.builder().crop(c.getCrop())
                    .expectedProductionInTon(c.getExpectedProductionInTon())
                    .actualHarvestedInTon(c.getActualHarvestedInTon()).build()).collect(Collectors.toList());
            farmCropsDtos.add(FarmCropsDto.builder().farm(farm).crops(cropDtos).build());
        }
        return farmCropsDtos;
    }

}