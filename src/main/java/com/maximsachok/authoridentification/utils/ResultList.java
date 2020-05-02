package com.maximsachok.authoridentification.utils;

import com.maximsachok.authoridentification.dto.Result;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Custom list of
 * @see Result
 * with maximum size of 10. Sorted by given comparator.
 */
public class ResultList {
    private ArrayList<Result> resultList = new ArrayList<>();
    private Comparator<Result> comparator;
    /**
     *Adds new item and then sorts the list with given comparator.
     * If the size is bigger then 10 after adding, removes the last item in the list.
     * @param result Result
     */
    public void addItem(Result result){
        resultList.add(result);
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
