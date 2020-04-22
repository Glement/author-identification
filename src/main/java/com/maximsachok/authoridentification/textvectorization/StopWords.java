package com.maximsachok.authoridentification.textvectorization;


import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StopWords {
    private static List<String> words = null;
    public static List<String> getStopWords() {
        if(words == null)
            ReadWords();
        return words;
    }
    private static void ReadWords() {
        try {
            words = new ArrayList<>();
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:/static/stopwords.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String line = null;

            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
