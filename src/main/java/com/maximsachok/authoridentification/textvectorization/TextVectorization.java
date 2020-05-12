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
     * Vectorizes given project in to double array of a size 300 using word2vec model.
     * @see WordModel
     * @param project Project for which to find author.
     * @return simple double array of size 300
     */
    public double[] vectoriseProject(ProjectDto project)
    {
        Collection<String> tokens = FilterText.filter(project.getDescEn()+" "+project.getKeywords()+" "+project.getNameEn());
        WordVectors model = WordModel.getWordModel();
        double[] result = new double[300];
        Arrays.fill(result,0d);
        int i = 0;
        for(String word : tokens){
            if (i==0) {
                if (model.hasWord(word))
                    result = model.getWordVector(word);
                else
                    continue;
            }
            else if (model.hasWord(word)){
                sumTwoArrays(result,model.getWordVector(word));
            }
            else{
                continue;
            }
            ++i;
        }
        if(i>0)
            divideArrayByNumber(result,i);
        return result;
    }

    /**
     * Vectorizes list of projects to double array of size 300
     * @param projects List of projects to vectorize
     * @return double array of size 300
     */
    public  double[] vectoriseProjects(List<Project> projects){
        StringBuilder allProject = new StringBuilder();
        for(Project project : projects)
        {
            allProject.append(" ").append(project.getDescEn()).append(" ").append(project.getNameEn()).append(" ").append(project.getKeywords());
        }
        ProjectDto project = new ProjectDto();
        project.setDescEn(allProject.toString());
        return vectoriseProject(project);
    }

    private void sumTwoArrays(double[] first, double[] second){
        for(int i = 0; i<first.length;i++)
            first[i] = first[i]+second[i];
    }

    private void divideArrayByNumber(double[] array, double number){
        for(int i = 0; i<array.length;i++)
            array[i] = array[i]/number;
    }

    /**
     * Maps given project to a map with key as word and vaule as number of occurrences in project.
     * @param project Project to vectorize
     * @return Map with key as word and value as number of occurrences of given key in project.
     */
    public Map<String,Double> mapProject(ProjectDto project) {
        Map<String,Double> mappedProject = new HashMap<>();
        Collection<String> tokens = FilterText.filter(project.getDescEn()+" "+project.getKeywords()+" "+project.getNameEn());
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
