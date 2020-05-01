package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.util.*;

public class TextVectorization {
    public double[] vectoriseProject(ProjectDto project)
    {
        Collection<String> tokens = FilterText.filter(project.getDescEn()+" "+project.getKeywords()+" "+project.getNameEn());
        Word2Vec model = WordModel.getWordModel();
        double[] result = new double[300];
        Arrays.fill(result,0d);
        int i = 0;
        for(String word : tokens){
            if (i==0) {
                result = model.getWordVector(word);
            }
            else if (model.getWordVector(word)!=null){
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
