package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.TrainerRequest;
import com.nadila.training_management_system_api.dto.response.TrainerResponse;
import com.nadila.training_management_system_api.entity.Trainer;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.TrainerRepository;
import com.nadila.training_management_system_api.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TrainerResponse create(TrainerRequest request) {
        Trainer trainer = modelMapper.map(request, Trainer.class);
        return toResponse(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public TrainerResponse update(Long trainerId, TrainerRequest request) {
        Trainer trainer = getEntity(trainerId);
        trainer.setFullName(request.getFullName());
        trainer.setType(request.getType());
        trainer.setSpecialization(request.getSpecialization());
        trainer.setOrganization(request.getOrganization());
        trainer.setEmail(request.getEmail());
        trainer.setPhone(request.getPhone());
        return toResponse(trainerRepository.save(trainer));
    }

    @Override
    public TrainerResponse getById(Long trainerId) {
        return toResponse(getEntity(trainerId));
    }

    @Override
    public List<TrainerResponse> getAll() {
        return trainerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long trainerId) {
        trainerRepository.delete(getEntity(trainerId));
    }

    private Trainer getEntity(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
    }

    private TrainerResponse toResponse(Trainer trainer) {
        return modelMapper.map(trainer, TrainerResponse.class);
    }
}
