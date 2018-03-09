/*
 * The MIT License
 *
 * Copyright 2017 Angel Miguel Garcia Vico <agvico at ujaen.es>.
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
package moa.subgroupdiscovery.qualitymeasures;

import java.io.Serializable;
import org.core.exceptions.InvalidRangeInMeasureException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import moa.options.AbstractOptionHandler;

/**
 * Abstract class that represents an statistical quality measure
 *
 * @author Angel Miguel Garcia Vico <agvico at ujaen.es>
 */
public abstract class QualityMeasure extends AbstractOptionHandler implements Cloneable, Serializable, Comparable<QualityMeasure> {

    /**
     * @return the short_name
     */
    public String getShort_name() {
        return short_name;
    }

    /**
     * @return the table
     */
    public ContingencyTable getTable() {
        return table;
    }

    /**
     * The value of the quality measure
     */
    protected double value;

    /**
     * The name of the quality measure
     */
    protected String name;

    /**
     * The acronim of the quality measure
     */
    protected String short_name;
    
    /**
     * The contingencyTable from the values are calculated
     */
    protected ContingencyTable table;

    /**
     * It calculates the value of the given quality measure by means of the
     * given contingency table
     *
     * @param t
     * @return
     */
    public abstract double calculateValue(ContingencyTable t);

    /**
     * Return the last calculated value of the measure
     *
     * @return
     */
    public double getValue() {
        return value;
    }

    /**
     * It checks that the value of the measure is within the domain of the
     * measure
     *
     * @param value
     * @return
     */
    public abstract void validate() throws InvalidRangeInMeasureException;

    /**
     * Returns a copy of this object
     *
     * @return
     */
    @Override
    public abstract QualityMeasure clone();

    @Override
    public String toString() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat sixDecimals = new DecimalFormat("0.000000", symbols);
        return short_name + " = " + sixDecimals.format(value);
    }

    /**
     * Returns the full name of the quality measure
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return short_name;
    }

    @Override
    public abstract int compareTo(QualityMeasure o);

    
    
    
}
