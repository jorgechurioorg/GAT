package com.leofanti.gat.utils;

import com.leofanti.gat.model.ExpReport;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Report4StrColChainedComparator implements Comparator<ExpReport> {

    private List<Comparator<ExpReport>> listComparators;

    @SafeVarargs
    public Report4StrColChainedComparator(Comparator<ExpReport>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(ExpReport emp1, ExpReport emp2) {
        for (Comparator<ExpReport> comparator : listComparators) {
            int result = comparator.compare(emp1, emp2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }


}