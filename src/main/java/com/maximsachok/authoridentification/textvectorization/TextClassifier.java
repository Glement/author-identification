package com.maximsachok.authoridentification.textvectorization;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibLINEAR;
import weka.core.*;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.NullStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.ArrayList;


/**
 * Classifier made for convenient use of WEKA API.
 * Creates model for given categories(authors) and data per author(projects).
 */
public class TextClassifier implements Serializable {
    private ArrayList<Attribute> attributes;
    private ArrayList<String> classValues;
    private Instances trainingData;
    private Boolean upToDate = false;
    private StringToWordVector filter;
    private Classifier classifier;
    private Boolean initialized = false;
    public TextClassifier (){
        buildClassifier();
    }

    public Boolean isInitialized() {
        return initialized;
    }

    private void buildClassifier(){
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

    public void addCategory(String category) {
        category = category.toLowerCase();
        classValues.add(category);
    }

    public void setupAfterCategorysAdded() {
        attributes.add(new Attribute("@@class@@", classValues));
        // Create dataset with initial capacity of 100, and set index of class.
        trainingData = new Instances("AuthorClassification", attributes, 100);
        trainingData.setClassIndex(1);
    }

    public void addData(String message, String classValue) throws IllegalStateException {
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
    public void buildIfNeeded() throws Exception {
        if (!upToDate) {
            // Initialize filter and tell it about the input format.
            filter.setInputFormat(trainingData);
            Instances filteredData = Filter.useFilter(trainingData, filter);
            // Rebuild classifier.
            classifier.buildClassifier(filteredData);
            upToDate = true;
            initialized = true;
        }
    }

    public int getClassIndex(String name){
        return classValues.indexOf(name);
    }

    public double[] classifyMessage(String message) throws Exception {
        buildIfNeeded();
        Instances testSet = trainingData.stringFreeStructure();
        Instance testInstance = makeInstance(message, testSet);

        // Filter instance.
        filter.input(testInstance);
        Instance filteredInstance = filter.output();
        return classifier.distributionForInstance(filteredInstance);
    }
}