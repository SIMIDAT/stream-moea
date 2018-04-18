/* 
 * The MIT License
 *
 * Copyright 2018 Ángel Miguel García Vico.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.core;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import moa.subgroupdiscovery.qualitymeasures.QualityMeasure;

/**
 * A Class to load the classes of the quality measures
 *
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public class ClassLoader {

    /**
     * The names of the class of each quality measure that should be used.
     *
     * If you want to add new measures, add them to the
     * moa.subgroupdiscovery.qualitymeasures package and after that, add the
     * name of the class here in order to be used by the algorithm
     */
    private static String[] measureClassNames = {"AUC",
        "Accuracy",
        "Confidence",
        "Coverage",
        "FPR",
        "GMean",
        "GrowthRate",
        "IsGrowthRate",
        "Jaccard",
        "SuppDiff",
        "Support",
        "TNR",
        "TPR",
        "WRAcc",
        "WRAccNorm"};

    /**
     * Returns the classes that represents the quality measures that are
     * available on the framework.
     *
     * This measures are found on the src/moa/subgroupdiscovery/qualitymeasures
     * folder under the package "qualitymeasures".
     *
     * @return An ArrayList, with all the QualityMeasure classes of the
     * measures.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static ArrayList<QualityMeasure> getClasses() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // Sort the array of class names 
        Arrays.sort(measureClassNames, String.CASE_INSENSITIVE_ORDER);
        ArrayList<QualityMeasure> measures = new ArrayList<>();
        for (String i : measureClassNames) {
            Class a = Class.forName(QualityMeasure.class.getPackage().getName() + "." + i);
            Object instance = a.newInstance();
            measures.add((QualityMeasure) instance);
        }
        return measures;
    }
}
