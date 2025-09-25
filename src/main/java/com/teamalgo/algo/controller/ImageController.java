package com.teamalgo.algo.controller;

import com.teamalgo.algo.dto.response.ImageUrlResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUrlResponse>> upload(@RequestPart("image") MultipartFile file) {
        String url = imageService.uploadImage(file);
        ImageUrlResponse response = new ImageUrlResponse(url);
        return ApiResponse.success(SuccessCode._OK, response);
    }

}
