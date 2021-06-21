package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Product;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class ProductProducer {
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	private String createdProductTopic = "saved-product-topic";

	public Disposable sendSavedProductTopic(Product data) {
		return Mono.just(data)
				.map(o->kafkaTemplate.send(createdProductTopic, data))
				.subscribe();
	}
}
