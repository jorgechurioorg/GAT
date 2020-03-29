package com.leofanti.gat.utils;

import com.leofanti.gat.model.Labels;

import java.util.Comparator;

public class ComparatorLabelName implements Comparator<Labels> {

    @Override
    public int compare(Labels o1, Labels o2) {
        return o2.getName().compareToIgnoreCase(o1.getName());
    }
}

