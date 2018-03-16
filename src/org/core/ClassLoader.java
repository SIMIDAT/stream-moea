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
     * The package name. (Change if necessary)
     */
    private static String pkg = "moa.subgroupdiscovery.qualitymeasures";
    
    
    /**
     * Returns the classes that represents the quality measures that are available on the framework.
     * 
     * This measures are found on the src/moa/subgroupdiscovery/qualitymeasures folder under the package
     * "qualitymeasures".
     * 
     * @return An ArrayList, with all the QualityMeasure classes of the measures.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException 
     */
    public static ArrayList<QualityMeasure> getClasses() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
   
        File root = new File("src/" + pkg.replace('.', '/'));
        ArrayList<QualityMeasure> measures = new ArrayList<QualityMeasure>();
        File[] listFiles = root.listFiles();
        Arrays.sort(listFiles);
        for (File i : listFiles) {
            if(! i.getName().contains("Exception") && ! i.getName().contains("ClassLoader") && ! i.getName().contains("NULL") && ! i.getName().contains("ContingencyTable") && ! i.getName().contains("QualityMeasure")){
                String fullyName = pkg + "." + i.getName().replaceAll(".java", "");
                Class a = Class.forName(fullyName);
                Object instance = a.newInstance();
                measures.add((QualityMeasure) instance);
            }
        }
        return measures;
    }
}
