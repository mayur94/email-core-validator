package com.email.core.validator.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.email.core.validator.config.MessagingConfig;
import com.email.core.validator.config.RestTemplateConfig;
import com.email.core.validator.model.EmailRequest;
import com.email.core.validator.model.EmailResponse;

import lombok.extern.slf4j.Slf4j;


@Component
@EnableAsync
@Slf4j
public class EmailCoreValidator {

	@Autowired
	RestTemplateConfig restTemplate;
	
	@Autowired
	public RabbitTemplate template;
	
	
	  @RabbitListener(queues = MessagingConfig.INPUT_QUEUE) 
	  @Async
	  public void consumeMessageFromInputQueue(EmailRequest emailRequest) throws URISyntaxException {
		  	log.info("Email id retrived"+emailRequest.getEmail());
			URI uri = new URI("http://ec2-3-87-204-188.compute-1.amazonaws.com:9000/validate");
				EmailResponse response = restTemplate.getRestTemplate().postForObject(uri, emailRequest,
						EmailResponse.class);
				template.convertAndSend("", MessagingConfig.OUTPUT_QUEUE, response);
			log.info("Gateway reponse for retrived email id"+response.getInput()+" Is reachable"+response.getIs_reachable());
			}

		
	 
	
}
