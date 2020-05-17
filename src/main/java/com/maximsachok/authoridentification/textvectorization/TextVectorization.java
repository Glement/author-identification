package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.util.*;

/**
 * Class that vectorizes given projects in to vectors or maps.
 * @see Project
 * @see ProjectDto
 */
public class TextVectorization {

    /**
     * Maps given project to a map with key as word and vaule as number of occurrences in project.
     * @param project Project to vectorize
     * @return Map with key as word and value as number of occurrences of given key in project.
     */
    public Map<String,Double> mapProject(ProjectDto project) {
        Map<String,Double> mappedProject = new HashMap<>();
        Collection<String> tokens = FilterText.filter(project.asString());
        for(String word : tokens){
            if(mappedProject.containsKey(word))
                mappedProject.put(word,mappedProject.get(word)+1);
            else
                mappedProject.put(word, 1d);
        }
        return mappedProject;
    }

    /**
     * Calculates the tf-idf for each word for a given list of projects
     * @param projects List of  projects
     * @return Map with key as word and value as its tf-idf for a given list of projects.
     */
    public Map<String,Double> calculateTfIdfForAuthor(List<Project> projects) {
        Vector<Map<String,Double>> mappedProjects = new Vector<>();
        Map<String,Double> result = new HashMap<>();
        for(Project project : projects){
            ProjectDto projectDto = new ProjectDto();
            projectDto.setDescEn(project.getDescEn());
            projectDto.setKeywords(project.getKeywords());
            projectDto.setNameEn(project.getNameEn());
            mappedProjects.add(mapProject(projectDto));
        }
        for(Map<String,Double> map : mappedProjects){
            for(String key : map.keySet()){
                double documentFrequency = 0;
                for(Map<String,Double> it : mappedProjects){
                    if(it.containsKey(key)){
                        ++documentFrequency;
                    }
                }
                if(result.containsKey(key)){
                    result.put(key,result.get(key)+map.get(key)*Math.log(projects.size()/(1d+documentFrequency)));
                }
                else{
                    result.put(key,map.get(key)*Math.log(projects.size()/(1d+documentFrequency)));
                }
            }
        }
        for(String key : result.keySet()){
            double occurrences = 0;
            for(Map<String,Double> map : mappedProjects){
                if(map.containsKey(key))
                    ++occurrences;
            }
            result.put(key,result.get(key)/occurrences);
        }
        return result;
    }

    /**
     * Calculates projects tf-idf where idf is took from a given List of projects.
     * @param document Document(Project) for which to calculate tf-idf
     * @param projects List of projects to calculate idf for a given document
     * @return Map with key as word and value as its tf-idf for a given document with given list of projects.
     */
    public Map<String, Double> calculateTfIdfForProject(Map<String,Double> document, List<Project> projects){
        Vector<Map<String,Double>> mappedProjects = new Vector<>();
        Map<String,Double> result = new HashMap<>();
        for(Project project : projects){
            ProjectDto projectDto = new ProjectDto();
            projectDto.setDescEn(project.getDescEn());
            projectDto.setKeywords(project.getKeywords());
            projectDto.setNameEn(project.getNameEn());
            mappedProjects.add(mapProject(projectDto));
        }
        for(String key : document.keySet()){
            double  documentFrequency = 0;
            for(Map<String,Double> map : mappedProjects){
                if(map.containsKey(key)){
                    ++documentFrequency;
                }
            }
            result.put(key,document.get(key)*Math.log(projects.size()/(1d+documentFrequency)));
        }
        return result;
    }
}
