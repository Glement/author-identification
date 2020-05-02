package com.maximsachok.authoridentification.textvectorization;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * Loads word2vec google model.
 * Using pre-trained Google news <a href="https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing">model</a>.
 */
public class WordModel {
    private static Word2Vec model = null;
    /**
     *
     * @return If resource can not be loaded, returns null. Else returns Word2Vec initialised model.
     * @see Word2Vec
     * .
     */
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
