package ru.practicum.service.compilation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilationsByPublicUser(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findCompilationsByPinned(pinned, pageable);
        }

        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationByIdByPublicUser(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Собрание с id = " + id + " не было найдено!"));

        return CompilationMapper.toCompilationDto(compilation);
    }
}
