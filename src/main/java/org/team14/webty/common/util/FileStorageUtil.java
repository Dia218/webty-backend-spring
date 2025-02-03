package org.team14.webty.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileStorageUtil {
	//private static final String RELATIVE_UPLOAD_DIR = "../webty-frontend-spring/public/uploads/";

	@Value("${upload.path}")
	private String uploadPath;

	// 절대경로 변환 메서드
	private String getAbsoluteUploadDir() {
		Path projectRoot = Paths.get("").toAbsolutePath(); // 현재 프로젝트 루트 경로 가져오기
		//return projectRoot.resolve(RELATIVE_UPLOAD_DIR).normalize().toString();
		return projectRoot.resolve(uploadPath).normalize().toString();
	}

	public String storeImageFile(MultipartFile file, String fileName) throws IOException {
		String baseUploadDir = getAbsoluteUploadDir();
		String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
		String uploadDir = Paths.get(baseUploadDir, dateFolder).toString(); // 날짜별 폴더 추가

		File directory = new File(uploadDir);
		if (!directory.exists() && !directory.mkdirs()) {
			log.error("디렉토리 생성 실패: {}", uploadDir);
			throw new IOException("디렉토리를 생성할 수 없습니다: " + uploadDir);
		}

		String originalFileName = file.getOriginalFilename();
		String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
		String newFileName = fileName + fileExtension;

		String filePath = Paths.get(uploadDir, newFileName).toString();
		file.transferTo(new File(filePath));

		log.info("파일이 저장되었습니다: {}", filePath);

		// 프론트엔드에서 접근할 수 있도록 상대 경로 반환
		return "/uploads/" + dateFolder + "/" + newFileName;
	}
}
