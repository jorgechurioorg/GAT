package com.leofanti.gat.utils;

import com.leofanti.gat.model.ExpReport;
import com.leofanti.gat.model.Labels;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LabelChainedComparator implements Comparator<Labels> {

    private List<Comparator<Labels>> listComparators;

    @SafeVarargs
    public LabelChainedComparator(Comparator<Labels>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(Labels emp1, Labels emp2) {
        for (Comparator<Labels> comparator : listComparators) {
            int result = comparator.compare(emp1, emp2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }


}