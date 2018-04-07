package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.dto.VentConfirmation;
import com.github.filipmalczak.vent.service.TimestampService;
import com.github.filipmalczak.vent.web.request.RawVentRequest;
import com.github.filipmalczak.vent.web.response.VentConfirmationResponse;
import com.github.filipmalczak.vent.web.response.VentedObjectViewResponse;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static com.github.filipmalczak.vent.helper.Struct.map;
import static com.github.filipmalczak.vent.helper.Struct.pair;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static reactor.core.publisher.Mono.just;

@VentSpringTest
public class VentApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void contextLoads() {
	}

	@Test
	public void publishEmptyAndGetIt(){
		VentConfirmationResponse confirmation = client.post().
			uri("/vent/raw").
			body(just(new RawVentRequest(null, "CREATE", null)), RawVentRequest.class).
			exchange().
			expectBody(VentConfirmationResponse.class).
			returnResult().getResponseBody();
		assertNotNull(confirmation.getObjectId());
		assertEquals("CREATE", confirmation.getOperation());
		//todo figure out mocking timestamp service
		assertNotNull(confirmation.getTimestamp());

		VentedObjectViewResponse view = client.get().
			uri("/object/"+confirmation.getObjectId()).
			exchange().
			expectBody(VentedObjectViewResponse.class).
			returnResult().getResponseBody();
		assertNotNull(view);
		Map expected = map();
		assertEquals(view.getObject(), expected);
	}
}
