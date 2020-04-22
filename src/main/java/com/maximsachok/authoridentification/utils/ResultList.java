package com.maximsachok.authoridentification.utils;

import com.maximsachok.authoridentification.dto.Result;

import java.util.ArrayList;
import java.util.Comparator;

public class ResultList {
    private ArrayList<Result> resultList = new ArrayList<>();
    private Comparator<Result> comparator;
    public void addItem(Result reuslt){
        resultList.add(reuslt);
        resultList.sort(comparator);
        if(resultList.size()>10)
            resultList.remove(10);
    }

    public void setComparator(Comparator<Result> comparator) {
        this.comparator = comparator;
    }

    public ArrayList<Result> getResultList() {
        return resultList;
    }
}
