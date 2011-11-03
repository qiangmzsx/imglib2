/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.example;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.operation.binary.real.RealAdd;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example1 {
	
	private static final long XSIZE = 100;
	private static final long YSIZE = 200;
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private static Img<? extends RealType<?>> makeInputImage() {
		Img<? extends RealType<?>> inputImg = allocateImage();
		RandomAccess<? extends RealType<?>> accessor = inputImg.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x+y);
			}			
		}
		return inputImg;
	}
	
	// calculate output values by adding 15 to the values of an input image
	
	private static boolean testAssignment() {
		
		boolean success = true;
		
		Img<? extends RealType<?>> inputImage = makeInputImage();
		
		DiscreteNeigh neighborhood = new DiscreteNeigh(new long[2], new long[2], new long[2]);
		
		Function<long[],Real> constant = new ConstantRealFunction<long[]>(new Real(15));
		
		Function<long[],Real> image = new RealImageFunction(inputImage);

		Function<long[],Real> additionFunc =
			new GeneralBinaryFunction<long[], Real, Real, Real>(constant, image, new RealAdd());
		
		long[] index = neighborhood.getKeyPoint();
		
		Real pointValue = new Real();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				index[0] = x;
				index[1] = y;
				additionFunc.evaluate(neighborhood, neighborhood.getKeyPoint(), pointValue);
				if (! veryClose(pointValue.getReal(), (x+y+15))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
							+(x+y+15)+") actual ("+pointValue.getReal()+")");
					success = false;
				}
			}
		}
		
		return success;
	}
	

	public static void main(String[] args) {
		System.out.println("Example1");
		if (testAssignment())
			System.out.println(" Successful test");
	}
}