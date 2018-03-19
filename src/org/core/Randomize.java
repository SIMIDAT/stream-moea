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

public class Randomize {
	private static long Seed;
	private static MTwister generador = new MTwister();
	public static void setSeed (long semilla) {
		Seed = semilla;
		generador.init_genrand(Seed);
	}
	/** Rand computes a psuedo-random float value between 0 and 1, excluding 1 
	 * @return A uniform-distributed real value in [0,1) 
	 */
	public static double Rand () {
		return (generador.genrand_res53());
	}
	/** RandOpen computes a psuedo-random float value between 0 and 1, excluding 0 and 1  
	 * @return A uniform-distributed real value in (0,1)
	 */
	public static double RandOpen () {
		return (generador.genrand_real3());
	}
	/** RandClosed computes a psuedo-random float value between 0 and 1 inclusive  
	 * @return A uniform-distributed real value in [0,1]
	 */
	public static double RandClosed () {
		return (generador.genrand_real1());
	}
	/** RandGaussian generates a standardized gaussian random number  
	 * @return A normal-distributed real value with mean 0 and standard deviation equal to 1
	 */
	public static double RandGaussian () {
		return (generador.genrand_gaussian());
	}

	/** Randint gives an integer value between low and high, excluding high
	 * @param low Lower bound (included)
	 * @param high Upper bound (NOT included)
	 * @return A uniform-distributed integer value in [low,high)
	 */
	public static int Randint (int low, int high) {
		return ((int) (low + (high - low) * generador.genrand_res53()));
	}
	/** RandintOpen gives an integer value between low and high, excluding 0 and 1
	 * @param low Lower bound (NOT included)
	 * @param high Upper bound (NOT included)
	 * @return A uniform-distributed integer value in (low,high)
	 */
	public static int RandintOpen (int low, int high) {
		//we use low+1, to avoid that "low" could appear, since genrand_res53()
		//draws a real value in [0,1)
		return ((int) ((low+1) + (high - (low+1)) * generador.genrand_res53()));
	}
	/** RandintClosed gives an integer value between low and high inclusive
	 * @param low Lower bound (included)
	 * @param high Upper bound (included)
	 * @return A uniform-distributed integer value in [low,high]
	 */
	public static int RandintClosed (int low, int high) {
		//since genrand_res53() generates a double in [0,1), we increment
		//high by one, so "high" can appear with same probability as the rest of
		//numbers in the interval
		return ((int) (low + ((high+1) - low) * generador.genrand_res53()));
	}
	/** Randdouble gives an double value between low and high, excluding high
	 * @param low Lower bound (included)
	 * @param high Upper bound (NOT included)
	 * @return A uniform-distributed real value in [low,high)
	 */
	public static double Randdouble (double low, double high) {
		return (low + (high-low) * generador.genrand_res53());
	}
	/** RanddoubleOpen gives an double value between low and high, excluding low and high
	 * @param low Lower bound (NOT included)
	 * @param high Upper bound (NOT included)
	 * @return A uniform-distributed real value in (low,high)
	 */
	public static double RanddoubleOpen (double low, double high) {
		return (low + (high-low) * generador.genrand_real3());
	}
	/** RanddoubleClosed gives an double value between low and high inclusive
	 * @param low Lower bound (included)
	 * @param high Upper bound (included)
	 * @return A uniform-distributed real value in [low,high]
	 */
	public static double RanddoubleClosed (double low, double high) {
		return (low + (high-low) * generador.genrand_real1());
	}
}


