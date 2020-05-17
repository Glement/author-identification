package com.maximsachok.authoridentification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author identification.
 * The AuthorIdentification application
 * is an application that helps to determine an author for a given Project(text)
 * based on texts semantic similarity and word use (TF-IDF).
 * This application is using dl4j <a href="https://deeplearning4j.org">deeplearning4j</a> Word2Vec implementation library for converting words in to vectors of 300 dimensions.
 * Using pre-trained Google news <a href="https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit">model</a>.
 * Data can be downloaded from <a href="https://drive.google.com/file/d/1Iak9LpvmD5uU4jlDvT4d9dKv1vFaxTAw/view">here</a>, put it in resources folder.
 * @author  Maxim Sachok
 * @version 1.0
 * @since   2020-05-02
 */
@SpringBootApplication
public class AuthorIdentificationApplication {

	private static final Logger logger = LoggerFactory.getLogger(AuthorIdentificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AuthorIdentificationApplication.class, args);

	}
}
