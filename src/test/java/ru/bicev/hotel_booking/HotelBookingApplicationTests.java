package ru.bicev.hotel_booking;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "booking.created", "payment.completed", "payment.failed" })
@ActiveProfiles("test")
@AutoConfigureMockMvc
class HotelBookingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private BlockingQueue<ConsumerRecord<String, String>> records;
	private UUID roomId = UUID.fromString("40ae849e-415e-4e72-9ffb-54d12e39ec78");

	@BeforeEach
	public void setup() {
		records = new LinkedBlockingQueue<>();
	}

	@KafkaListener(topics = "booking.created", groupId = "test-group")
	public void listenBookingCreated(ConsumerRecord<String, String> record) {
		records.add(record);
	}

	@Test
	public void testCreateBookingAndKafkaEvent() throws Exception {

		String json = """
				{
				    "userId": "2e7b2a6d-caf8-4c94-bb9f-347b204d2e29",
				    "roomId": "40ae849e-415e-4e72-9ffb-54d12e39ec78",
				    "checkIn": "2026-01-01",
				    "checkOut": "2026-01-05",
				    "amount": 100
				}
				""";

		mockMvc.perform(post("/api/booking")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.checkIn").value("2026-01-01"));

		ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
		assertThat(received.topic()).isEqualTo("booking.created");
		assertThat(received.value()).contains("bookingId");
	}

	@Test
	public void testGetBookingById() throws Exception {
		mockMvc.perform(get("/api/booking/853060ed-bb96-4e45-a80d-c85352aa54b8"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.checkIn").value("2026-01-01"));
	}

	@Test
	public void testDeleteBookingById() throws Exception {
		mockMvc.perform(delete("/api/booking/853060ed-bb96-4e45-a80d-c85352aa54b8"))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testCancelBookingById() throws Exception {
		mockMvc.perform(patch("/api/booking/22092c69-061b-43eb-bd88-293ab8b70251/cancel"))
				.andExpect(status().is4xxClientError());

	}

}
