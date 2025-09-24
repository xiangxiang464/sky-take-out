package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api("通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            String filename =file.getOriginalFilename();
            String extend = filename.substring(filename.lastIndexOf("."));
            String objectName = UUID.randomUUID() +extend;
            String uploadPath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(uploadPath);
        } catch (IOException e) {
            log.info("文件上传失败: {}",e);
            throw new RuntimeException(e);
        }

    }
}
