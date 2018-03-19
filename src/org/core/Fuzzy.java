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

public class Fuzzy {
    /**
     * <p>
     * Values for a fuzzy set definition
     * </p>
     */

     private float x0,x1,x3;
     private float y;


    /**
     * <p>
     * Methods to get the value of x0
     * </p>
     * @return          Value of x0
     */
    public float getX0 () {
        return x0;
    }

    /**
     * <p>
     * Methods to get the value of x1
     * </p>
     * @return          Value of x1
     */
    public float getX1 () {
        return x1;
    }

    /**
     * <p>
     * Methods to get the value of x3
     * </p>
     * @return          Value of x3
     */
    public float getX3 () {
        return x3;
    }


    /**
     * <p>
     * Method to set the values of x0, x1 y x3
     * </p>
     * @param vx0       Value for x0
     * @param vx1       Value for x1
     * @param vx3       Value for x3
     * @param vy        Value for y
     */
    public void setVal (float vx0, float vx1, float vx3, float vy) {
        x0 = vx0;
        x1 = vx1;
        x3 = vx3;
        y  = vy;
    }


    /**
     * <p>
     * Returns the belonging degree
     * </p>
     * @param X             Value to obtain the belonging degree
     * @return              Belonging degree
     */
    public float Fuzzy (float X) {
        if ((X<=x0) || (X>=x3))  // If value of X is not into range x0..x3
            return (0);          // then pert. degree = 0
        if (X<x1)
            return ((X-x0)*(y/(x1-x0)));
        if (X>x1)
            return ((x3-X)*(y/(x3-x1)));
        return (y);
    }
    public float FuzzyT (float X, double tun_param) {

        float ax0,ax1,ax3;
        float atun_param = (float) tun_param;

        ax0 = x0+(((x3-x0)/2)*atun_param);
        ax1 = x1+(((x3-x0)/2)*atun_param);
        ax3 = x3+(((x3-x0)/2)*atun_param);


        if ((X<=ax0) || (X>=ax3))  // If value of X is not into range x0..x3
            return (0);          // then pert. degree = 0
        if (X<x1)
            return ((X-ax0)*(y/(ax1-ax0)));
        if (X>x1)
            return ((ax3-X)*(y/(ax3-ax1)));
        return (y);
    }


    /**
     * <p>
     * Creates a new instance of Fuzzy
     * </p>
     */
    public Fuzzy() {
    }

}
