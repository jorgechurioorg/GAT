package com.leofanti.gat.utils;

import com.leofanti.gat.model.ExpReport;

import java.util.Comparator;

public class ComparatorCol3 implements Comparator<ExpReport>{
        @Override
        public int compare(ExpReport o1, ExpReport o2) {
            return o2.getCol3().compareToIgnoreCase(o1.getCol3());
        }
}

