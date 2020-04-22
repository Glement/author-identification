package com.maximsachok.authoridentification;

import com.maximsachok.authoridentification.textvectorization.StopWords;
import com.maximsachok.authoridentification.textvectorization.WordModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthorIdentificationApplication {

	private static final Logger logger = LoggerFactory.getLogger(AuthorIdentificationApplication.class);

	public static void main(String[] args) {
		logger.info("Starting loading up model and stopwords.");
		if(WordModel.getWordModel()==null) {
			logger.info("Can't start due to model not being loaded.");
			return;
		}
		if(StopWords.getStopWords().size()==0) {
			logger.info("Can't start due to stopwords not being loaded.");
			return;
		}
		SpringApplication.run(AuthorIdentificationApplication.class, args);

	}
}
