package com.maximsachok.authoridentification.textvectorization;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads word2vec google model.
 * Using pre-trained Google news <a href="https://drive.google.com/file/d/0B7XkCwpI5KDYNlNUTTlSS21pQmM/edit?usp=sharing">model</a>.
 */
public class WordModel {
    private static WordVectors model = null;
    /**
     *
     * @return If resource can not be loaded, returns null. Else returns WordVectors initialised model.
     */
    public static WordVectors getWordModel(){
        if(model == null)
        {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:/static/GoogleNews-vectors-negative300.bin");
            try{
                model = WordVectorSerializer.loadStaticModel(resource.getInputStream());
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return model;
    }
}
