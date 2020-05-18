package com.maximsachok.authoridentification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author identification.
 * The AuthorIdentification application
 * is an application that helps to determine an author for a given Project(text)
 * based on text similarity, using WEKA API with LibLINEAR algorithm.
 * This algorithm gives the most accuracy on the supplied data.
 * @author  Maxim Sachok
 * @version 1.0
 * @since   2020-05-018
 */
@SpringBootApplication
public class AuthorIdentificationApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthorIdentificationApplication.class, args);
	}
}
