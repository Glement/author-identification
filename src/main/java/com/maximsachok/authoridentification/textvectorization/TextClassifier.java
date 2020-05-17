package com.maximsachok.authoridentification.textvectorization;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.*;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;

public class TextClassifier{
    private ArrayList<Attribute> attributes;
    private ArrayList<String> classValues;
    private Instances trainingData;
    private Boolean upToDate = false;
    private StringToWordVector filter;
    private Classifier classifier;
    private static TextClassifier textClassifier = null;
    private static Boolean initialized = false;
    public static TextClassifier getTextClassifier(Classifier classifier){
        if(textClassifier==null){
            textClassifier = new TextClassifier(classifier);
            initialized = true;
        }
        return textClassifier;
    }
    public static TextClassifier updateClassifier(Classifier classifier){
        textClassifier = null;
        return getTextClassifier(classifier);
    }
    private TextClassifier(Classifier classifier){
        buildClassifier(classifier);
    }

    public static Boolean isInitialized() {
        return initialized;
    }

    private void buildClassifier(Classifier classifier){
        this.classifier = classifier;
        filter = new StringToWordVector();
        filter.setWordsToKeep(1000000);
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);
        filter.setMinTermFreq(1);
        filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL,StringToWordVector.TAGS_FILTER));
        NGramTokenizer t = new NGramTokenizer();
        t.setNGramMaxSize(3);
        t.setNGramMinSize(1);
        filter.setTokenizer(t);
        Stemmer s = new /*Iterated*/LovinsStemmer();
        filter.setStemmer(s);
        attributes = new ArrayList<>();
        attributes.add(new Attribute("text",(ArrayList<String>)null));
        classValues = new ArrayList<>();
    }

    public void addCategory(String category) {
        category = category.toLowerCase();
        classValues.add(category);
    }

    public void addCategoryAfterSetup(String category){
        category = category.toLowerCase();
        attributes.get(1).addStringValue(category);
        trainingData = new Instances("AuthorClassification", attributes, 100);
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
    }

    public void setupAfterCategorysAdded() {
        attributes.add(new Attribute("class", classValues));
        // Create dataset with initial capacity of 100, and set index of class.
        trainingData = new Instances("AuthorClassification", attributes, 100);
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
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
        }
    }

    public int getClassIndex(String name){
        return classValues.indexOf(name);
    }

    public double[] classifyMessage(String message) throws Exception {
        buildIfNeeded();
        Instances testset = trainingData.stringFreeStructure();
        Instance testInstance = makeInstance(message, testset);

        // Filter instance.
        filter.input(testInstance);
        Instance filteredInstance = filter.output();
        Classifier classifier1 = AbstractClassifier.makeCopy(classifier);
        return classifier1.distributionForInstance(filteredInstance);
    }
}