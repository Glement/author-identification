package com.maximsachok.authoridentification.textvectorization;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

public class WordModel {
    private static Word2Vec model = null;
    public static Word2Vec getWordModel(){
        if(model == null)
        {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:/static/GoogleNews-vectors-negative300.bin");
            try{
                model = WordVectorSerializer.readBinaryModel(resource.getFile(),false,false);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return model;
    }
}
