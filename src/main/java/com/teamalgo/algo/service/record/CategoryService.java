package com.teamalgo.algo.service.record;

import com.teamalgo.algo.domain.category.Category;
import com.teamalgo.algo.dto.CategoryDTO;
import com.teamalgo.algo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getCategories () {
        return categoryRepository.findAllByOrderById().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .toList();
    }

}
