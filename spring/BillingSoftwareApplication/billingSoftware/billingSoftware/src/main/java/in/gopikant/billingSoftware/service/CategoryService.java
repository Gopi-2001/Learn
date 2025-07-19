package in.gopikant.billingSoftware.service;

import in.gopikant.billingSoftware.io.CategoryRequest;
import in.gopikant.billingSoftware.io.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    CategoryResponse add(CategoryRequest request, MultipartFile uploadFile);

    List<CategoryResponse> read();

    void delete(String categoryId);

}
