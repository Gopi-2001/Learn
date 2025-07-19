package in.gopikant.billingSoftware.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    // UploadFile will return the url as string and will accept image as multipart parameter
    String uploadFile(MultipartFile file) throws IOException;

    boolean deleteFile(String imgUrl);
}
