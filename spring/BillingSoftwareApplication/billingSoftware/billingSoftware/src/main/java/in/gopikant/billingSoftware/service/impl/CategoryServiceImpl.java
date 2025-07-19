package in.gopikant.billingSoftware.service.impl;

import in.gopikant.billingSoftware.entity.CategoryEntity;
import in.gopikant.billingSoftware.io.CategoryRequest;
import in.gopikant.billingSoftware.io.CategoryResponse;
import in.gopikant.billingSoftware.repository.CategoryRepository;
import in.gopikant.billingSoftware.repository.ItemRepository;
import in.gopikant.billingSoftware.service.CategoryService;
import in.gopikant.billingSoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    // Injecting CategoryRepository
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile uploadFile) {

        String imgUrl = null;
        try {
            String fileName = UUID.randomUUID().toString() + "." + StringUtils.getFilenameExtension(uploadFile.getOriginalFilename());
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(uploadFile.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            imgUrl = "http://localhost:8080/api/v1.0/uploads/"+fileName;

            // Below code upload files to AWS
            //imgUrl = fileUploadService.uploadFile(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImgUrl(imgUrl);
        // We can save it to DataBase using CategoryRepositoy.save()
        // It will automatically Generate ID for the Primary Field
        CategoryEntity savedCategory = categoryRepository.save(newCategory);
        
        return ConvertToResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> read(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> ConvertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

        if(existingCategory.getImgUrl() != null){
            String imgUrl = existingCategory.getImgUrl();
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName);

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Below code deleteFile from AWS
            //fileUploadService.deleteFile(existingCategory.getImgUrl());
        }

        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse ConvertToResponse(CategoryEntity savedCategory) {
        Integer itemsCount = itemRepository.countByCategoryId(savedCategory.getId());
        return CategoryResponse.builder()
                .categoryId(savedCategory.getCategoryId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .bgColor(savedCategory.getBgColor())
                .imgUrl(savedCategory.getImgUrl())
                .createdAt(savedCategory.getCreatedAt())
                .updatedAt(savedCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();

        // Converted CategoryRequest to CategoryEntity using builder
        // Once we get the Category Entity We can store it to the Database
        // Before this we need to generate the image and store it to the AWS S3 bucket (Storage in AWS)
    }
}
