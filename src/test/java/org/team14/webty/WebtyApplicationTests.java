package org.team14.webty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") //해당 에너테이션이 있어야 h2 사용
class WebtyApplicationTests {

	@Test
	void contextLoads() throws InterruptedException {
		Thread.sleep(60000); //h2 콘솔 확인을 위한 대기
	}

}
