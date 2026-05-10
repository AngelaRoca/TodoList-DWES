package com.Tarea_DWES_AngelaRocaBlanco.TodoList.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TagDTOs.TagRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TagDTOs.TagResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Tag;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> getAll() {
        return tagRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TagResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public TagResponse create(TagRequest req) {
        if (tagRepository.existsByName(req.getName()))
            throw new IllegalArgumentException("Ya existe un tag con ese nombre");
        Tag tag = Tag.builder().name(req.getName()).build();
        return toResponse(tagRepository.save(tag));
    }

    public TagResponse update(Long id, TagRequest req) {
        Tag tag = findOrThrow(id);
        tag.setName(req.getName());
        return toResponse(tagRepository.save(tag));
    }

    public void delete(Long id) {
        tagRepository.delete(findOrThrow(id));
    }

    public Tag findOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag con id " + id + " no encontrado"));
    }

    public TagResponse toResponse(Tag t) {
        TagResponse r = new TagResponse();
        r.setId(t.getId());
        r.setName(t.getName());
        return r;
    }
}
