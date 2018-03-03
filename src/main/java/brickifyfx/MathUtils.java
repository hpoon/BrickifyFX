/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brickifyfx;

/**
 *
 * @author Dan
 */
public class MathUtils {

	public static double clamp(double val, double min, double max) {
		if (max < min) {
			return val;
		} else if (val < min) {
			return min;
		} else if (val > max) {
			return max;
		} else {
			return val;
		}
	}
	
	public static double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}
	
	public static double max(double a, double b, double c) {
		return Math.max(Math.max(a, b), c);
	}
	
	/**
	 * Takes an r, g, or b component from a JavaFX Color object and
	 * converts it to an equivalent byte value for an 8 bit per channel
	 * image.
	 */
	public static byte colorComponentToByte(double component) {
		if (component <= 0.0) {
			return 0;
		} else if (component >= 1.0) {
			return (byte) 255;
		} else {
			int converted = (int)(component * 255.0);
			return (byte)converted;
		}
	}

	public static double[][] calculateTransformation(int rotation, double width, double height, boolean isRectangular) {
		double rotationRad = convertDegreesToRadians(rotation);
		// For corners, they are not centered in the middle, but rather on the corner
		// I've only ever seen the corners go like this |¯, and this *should* generalise, but I haven't been able to
		// test it with corners in other orientations.  I'm basically translating it by a quarter so the center of
		// rotation becomes the same center as like a 2x2 piece
		double[][] cornerTranslationMatrix = {
			{ 1, 0, 0, 0 },
			{ 0, 1, 0, 0 },
			{ 0, 0, 1, 0 },
			{ width / 4.0, 0, height / 4.0, 1 }
		};
		double[][] rotationMatrix = {
			{ Math.cos(rotationRad), 0, Math.sin(rotationRad), 0 },
			{ 0, 1, 0, 0 },
			{ -Math.sin(rotationRad), 0, Math.cos(rotationRad), 0 },
			{ 0, 0, 0, 1 }
		};
		double[][] translationMatrix = {
			{ 1, 0, 0, 0 },
			{ 0, 1, 0, 0 },
			{ 0, 0, 1, 0 },
			// Right is +X and down is -Z in LDraw for some reason
			{ width / 2.0, 0, -height / 2.0, 1 },
		};
		if (isRectangular) {
			return mmult(rotationMatrix, translationMatrix);
		} else {
			return mmult(mmult(cornerTranslationMatrix, rotationMatrix), translationMatrix);
		}
	}

	private static double convertDegreesToRadians(int degrees) {
		return degrees * Math.PI / 180;
	}

	private static double[][] mmult(double[][] a, double[][] b) {
		int aRows = a.length;
		int aColumns = a[0].length;
		int bRows = b.length;
		int bColumns = b[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		double[][] c = new double[aRows][bColumns];
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				c[i][j] = 0.00000;
			}
		}

		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < bColumns; j++) { // bColumn
				for (int k = 0; k < aColumns; k++) { // aColumn
					c[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return c;
	}
}
