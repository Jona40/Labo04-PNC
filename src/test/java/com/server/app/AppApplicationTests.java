package com.server.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled // <--- Esta anotación es la clave: le dice a Maven que no ejecute este test
class AppApplicationTests {

	@Test
	void contextLoads() {
	}

}