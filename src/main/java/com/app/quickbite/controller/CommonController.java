package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * File upload and download
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    /**
     * The location where files will be stored, retrieved from the configuration file using the @Value annotation.
     */
    @Value("${quickbite.linux-path}")
    private String basePath;

    /**
     * File upload
     *
     * @param file The file to upload. This is a temporary file that needs to be moved to a specified directory;
     *             otherwise, it will be deleted.
     * @return A generic response object
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {  // !!! The parameter name must match the `name` in the frontend's request
        log.info("File upload: {}", file.toString());

        // Get the original file name
        String originalFilename = file.getOriginalFilename();
        // Get the file extension
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // Generate a unique file name using UUID
        String fileName = UUID.randomUUID() + suffix;
        // Create a directory if it doesn't exist
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // Transfer the file to the specified directory
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the file name
        return R.success(fileName);
    }

    /**
     * File download
     *
     * @param name     The name of the file to download
     * @param response The response to store the file
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fileInputStream = null;     // File input stream
        ServletOutputStream outputStream = null;    // Output stream

        try {
            // Input stream to read the file content
            fileInputStream = new FileInputStream(new File(basePath + name));
            // Output stream to write the file back to the browser for display
            outputStream = response.getOutputStream();
            // Read the file content and write it back to the browser
            byte[] bytes = new byte[1024];  // 1KB buffer
            int length;                     // Length of each read operation
            while ((length = fileInputStream.read(bytes)) != -1) {  // Read file content
                outputStream.write(bytes, 0, length);               // Write content to the browser
                outputStream.flush();                               // Flush the buffer
            }

            // Set the response header to tell the browser to open the file as an image
            response.setContentType("image/jpeg");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the streams
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
  