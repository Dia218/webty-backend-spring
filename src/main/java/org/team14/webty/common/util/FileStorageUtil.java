package org.team14.webty.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileStorageUtil {
	private static final String DEFAULT_DIR = System.getProperty("user.dir") + "/src/main/resources/";

	public String storeImageFile(MultipartFile file, String fileName) throws IOException {
		String uploadDir = DEFAULT_DIR + "image/"
			+ LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
		File directory = new File(uploadDir);
		if (!directory.exists()) {
			if (!directory.mkdirs()) { // 생성 실패 시 예외 처리
				log.error("디렉토리 생성에 실패했습니다. 경로: {}", uploadDir);
				throw new IOException("디렉토리를 생성할 수 없습니다: " + uploadDir);
			}
			log.info("디렉토리가 생성되었습니다. 경로: {}", uploadDir);
		}
		String originalFileName = file.getOriginalFilename();
		String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
		String newFileName = fileName + fileExtension;

		String filePath = Paths.get(uploadDir, newFileName).toString();
		file.transferTo(new File(filePath));
		log.info("파일이 저장되었습니다: {}", filePath);

		return filePath;
	}
}
