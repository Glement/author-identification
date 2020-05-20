package com.maximsachok.authoridentification.utils;

import java.util.ArrayList;
import java.util.List;

public class DivideList {

    public static <T> List<List<T>> batchList(List<T> inputList, final int maxSize) {
        List<List<T>> sublists = new ArrayList<>();

        final int size = inputList.size();

        for (int i = 0; i < size; i += maxSize) {
            sublists.add(new ArrayList<>(inputList.subList(i, Math.min(size, i + maxSize))));
        }

        return sublists;
    }
}
