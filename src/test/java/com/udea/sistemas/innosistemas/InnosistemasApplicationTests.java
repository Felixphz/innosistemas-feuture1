package com.udea.sistemas.innosistemas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InnosistemasApplicationTests {

	@Test
	void contextLoads() {
		// This test will pass if the application context loads successfully
	}

	@Test
	void applicationStarts() {
		// Additional test to ensure the application can start
		InnosistemasApplication.main(new String[] {});
	}

}
