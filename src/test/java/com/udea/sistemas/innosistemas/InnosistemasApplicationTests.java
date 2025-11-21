package com.udea.sistemas.innosistemas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class InnosistemasApplicationTests {

	@Test
	void contextLoads() {

	}
	@Test
	void applicationStarts() {
		InnosistemasApplication.main(new String[] {});
		assertTrue(true);
	}

}
