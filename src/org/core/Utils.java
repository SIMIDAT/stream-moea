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

import java.util.*;

public class Utils {
    /**
     * <p>
     * Assorted methods to manage several topics
     * </p>
     */


    /**
     * <p>
     * Gets an integer from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      Integer value of the token
     */
    public static int getParamInt (StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        return Integer.parseInt(val);
    }

    /**
     * <p>
     * Gets an float from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      Float value of the token
     */
    public static float getParamFloat (StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        return Float.parseFloat(val);
    }

    /**
     * <p>
     * Gets an String from param file, skiping "="
     * </p>
     * @param s     Token
     * @return      String value of the token
     */
    public static String getParamString(StringTokenizer s) {
        String contenido = "";
        String val = s.nextToken(); // skip "="
        do {
            if (!s.hasMoreTokens()) break;
            contenido += s.nextToken() + " ";
        } while(true);
        contenido = contenido.trim();
        return contenido;
    }

    /**
     * <p>
     * Gets the name for the file, eliminating "" and skiping "="
     * </p>
     * @param s     Token
     * @return      The name of the file
     */
    public static String getFileName(StringTokenizer s) {
        String val = s.nextToken(); // skip "="
        val = s.nextToken();
        val = val.replace('"',' ').trim();
        return val;  // Only takes first name, second is ignored
    }


    /**
     * <p>
     * Returns the position of the element at the vector, -1 if does not appear
     * </p>
     * @param vect_valores  Vector of values
     * @param valor         Value to seek
     * @return              Position of the value searched
     */
    public static int getposString (Vector vect_valores, String valor ) {
        for (int i=0;  i<vect_valores.size(); i++)
            if (vect_valores.elementAt(i).equals(valor))
                return (i);
        return (-1);
    }


    /**
     * <p>
     * Returns the minimum of two float values
     * </p>
     * @param x         A float
     * @param y         A float
     * @return          The minimum float in the comparison
     */
    public static float Minimum (float x, float y) {
        if (x<y)
            return (x);
        else
            return (y);
    }

    /**
     * <p>
     * Returns the maximum of two float values
     * </p>
     * @param x         A float
     * @param y         A float
     * @return          The maximum float in the comparison
     */
    public static float Maximum (float x, float y) {
        if (x>y)
            return (x);
        else
            return (y);
    }


    /**
     * <p>
     * Returns if the first float argument is better than the second
     * </p>
     * @param X     Some float
     * @param Y     Some float
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (float X, float Y) {
        if (X > Y) return true;
        else return false;
    }

    /**
     * <p>
     * Returns if the first integer argument is better than the second
     * </p>
     * @param X     Some integer
     * @param Y     Some integer
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (int X, int Y) {
        if (X > Y) return true;
        else return false;
    }

    /**
     * <p>
     * Returns if the first double argument is better than the second
     * </p>
     * @param X     Some double
     * @param Y     Some double
     * @return      True if X is better than Y and false in other way
     */
    public static boolean BETTER (double X, double Y) {
        if (X > Y) return true;
        else return false;
    }


    /**
     * <p>
     * C.A.R, Hoare Quick sort. Based on sort by interchange. Decreasing sort.
     * </p>
     * @param v             Vector to be sorted
     * @param izqinitial    Position to sort
     * @param right           Final position to sort
     * @param index        The indexes of the original vector
     */
    public static void OrDecIndex (double v[], int left, int right, int index[])  {
        int i,j,aux;
        double x,y;

        i = left;
        j = right;
        x = v[(left+right)/2];
        do {
            while (v[i]>x && i<right)
                i++;
            while (x>v[j] && j>left)
                j--;
            if (i<=j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                aux = index[i];
                index[i] = index[j];
                index[j] = aux;
                i++;
                j--;
            }
        } while(i<=j);
        if (left<j)
            OrDecIndex (v,left,j,index);
        if (i<right)
            OrDecIndex (v,i,right,index);

    }


    /**
     * <p>
     * C.A.R, Hoare Quick sort. Based on sort by interchange. Incresing sort.
     * </p>
     * @param v		Vector to be sorted
     * @param left	Initial position to sort
     * @param right	Final position to sort
     * @param index	The indexes of the original vector
     */
    public static void OrCrecIndex (double v[], int left, int right, int index[])  {
        int i,j,aux;
        double x,y;

        i = left;
        j = right;
        x = v[(left+right)/2];
        do {
            while (v[i]<x && i<right)
                i++;
            while (x<v[j] && j>left)
                j--;
            if (i<=j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                aux = index[i];
                index[i] = index[j];
                index[j] = aux;
                i++;
                j--;
            }
        } while(i<=j);
        if (left<j)
            OrCrecIndex (v,left,j,index);
        if (i<right)
            OrCrecIndex (v,i,right,index);
    }


    /**
     * <p>
     * Rounds the generated value for the semantics when necesary
     * </p>
     * @param val       The value to round
     * @param tope
     */
    public static float Assigned (float val, float tope) {
        if (val>-0.0001 && val<0.0001)
            return (0);
        if (val>tope-0.0001 && val<tope+0.0001)
            return (tope);
        return (val);
    }


}
