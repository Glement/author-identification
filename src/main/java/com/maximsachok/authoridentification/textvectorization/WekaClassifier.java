package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.utils.DivideTextInToSentences;
import org.apache.commons.lang3.tuple.ImmutablePair;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibLINEAR;
import weka.core.*;
import weka.core.stemmers.NullStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.*;


/**
 * Classifier made for convenient use of WEKA API.
 * Creates model for given categories(authors) and data per author(projects).
 */
public class WekaClassifier implements AuthorClassifier {
    private ArrayList<Attribute> attributes;
    private ArrayList<String> classValues;
    private Boolean upToDate = false;
    private Instances trainingData;
    private static Boolean initializing = false;
    private StringToWordVector filter;
    private Classifier classifier;
    private static Boolean initialized = false;
    private static WekaClassifier wekaClassifier=null;

    public WekaClassifier(){
        setupClassifier();
    }

    private void setupClassifier(){
        LibLINEAR liblinear = new LibLINEAR();
        liblinear.setSVMType(new SelectedTag(0, LibLINEAR.TAGS_SVMTYPE));
        liblinear.setProbabilityEstimates(true);
        liblinear.setBias(1); // default value
        classifier = liblinear;
        filter = new StringToWordVector();
        filter.setWordsToKeep(500000);
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);
        filter.setMinTermFreq(1);
        filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL,StringToWordVector.TAGS_FILTER));
        NGramTokenizer t = new NGramTokenizer();
        t.setNGramMaxSize(3);
        t.setNGramMinSize(2);
        filter.setTokenizer(t);
        Stemmer s = new NullStemmer();
        filter.setStemmer(s);
        attributes = new ArrayList<>();
        attributes.add(new Attribute("text",(ArrayList<String>)null));
        classValues = new ArrayList<>();
        initialized = false;
    }

    private void addCategory(String category) {
        category = category.toLowerCase();
        classValues.add(category);
    }

    private void setupAfterCategorysAdded() {
        attributes.add(new Attribute("@@class@@", classValues));
        // Create dataset with initial capacity of 100, and set index of class.
        trainingData = new Instances("AuthorClassification", attributes, 100);
        trainingData.setClassIndex(1);
    }

    private void addData(String message, String classValue) throws IllegalStateException {
        message = message.toLowerCase();
        classValue = classValue.toLowerCase();
        // Make message into instance.
        Instance instance = makeInstance(message, trainingData);
        // Set class value for instance.
        instance.setClassValue(classValue);
        // Add instance to training data.
        trainingData.add(instance);
        upToDate = false;
    }

    private Instance makeInstance(String text, Instances data) {
        // Create instance of length two.
        Instance instance = new DenseInstance(2);
        // Set value for message attribute
        Attribute messageAtt = data.attribute("text");
        instance.setValue(messageAtt, messageAtt.addStringValue(text));
        // Give instance access to attribute information from the dataset.
        instance.setDataset(data);
        return instance;
    }

    /**
     * Check whether classifier and filter are up to date. Build i necessary.
     * @throws Exception
     */
    private void buildIfNeeded() throws Exception {
        if(trainingData.size()<=1)
            return;
        if (!upToDate) {
            // Initialize filter and tell it about the input format.
            filter.setInputFormat(trainingData);
            Instances filteredData = Filter.useFilter(trainingData, filter);
            // Rebuild classifier.
            classifier.buildClassifier(filteredData);
            upToDate = true;
        }
    }

    private double[] classifyMessage(String message) throws Exception {
        buildIfNeeded();
        Instances testSet = trainingData.stringFreeStructure();
        Instance testInstance = makeInstance(message, testSet);

        // Filter instance.
        filter.input(testInstance);
        Instance filteredInstance = filter.output();
        return classifier.distributionForInstance(filteredInstance);
    }

    public void initClassifier(List<Author> authors) {
        for(Author author : authors){
            if(!author.getAuthorProjects().isEmpty())
                addCategory(author.getExpertidtk().toString());
        }
        setupAfterCategorysAdded();
        for(Author author : authors){
            for(AuthorProject authorProject : author.getAuthorProjects()){
                for(String sentence : DivideTextInToSentences.Divide(authorProject.getProject().asString())){
                    addData(sentence, author.getExpertidtk().toString());
                }
            }
        }
        try{
            buildIfNeeded();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ImmutablePair<Double,String>>  classifyText(String text) {
        List<ImmutablePair<Double,String>> result = new ArrayList<>();
        try{
            buildIfNeeded();
            double [] probabilities = classifyMessage(text);
            for(int i=0; i<probabilities.length;i++){
                result.add(new ImmutablePair<Double, String>(probabilities[i],classValues.get(i)));
            }
            result.sort((ImmutablePair<Double, String> a, ImmutablePair<Double, String> b) ->{
                if(b.getKey()>a.getKey())
                    return 1;
                if(a.getKey().equals(b.getKey()))
                    return 0;
                return -1;
            });
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        if(result.size()<=10)
            return result;
        return result.subList(0,10);
    }

    @Override
    public double testClassifier(){
        double result = 0;
        try{
            Evaluation eval = new Evaluation(trainingData);
            eval.crossValidateModel(classifier,trainingData,10,new Random(1));
            result = eval.correct()/trainingData.size();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}