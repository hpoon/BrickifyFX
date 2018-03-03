package brickifyfx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import brickifyfx.core.Brick;
import brickifyfx.core.BricksAndColors;
import brickifyfx.core.ColoredBrick;
import brickifyfx.core.Mosaic;

public class LdrawOutput {

	// The width of a 1x1 Lego piece in LDraw
	private static final double BASE_WIDTH = 20.0;

	private PrintWriter printWriter;

	public void saveLdraw(File outputFile, BricksAndColors bricksAndColors, Mosaic mosaic) throws IOException {
		if (!bricksAndColors.getBricks().isTopDown()) {
			System.out.println("This export is not going to work correctly");
		}

		printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

		writeLdrawLine("0 // Created by brickifyfx");
		writeLdrawLine("0 // output of a " + mosaic.getMosaicWidth() + " by " + mosaic.getMosaicHeight() + " mosaic");
		double xCoord;
		double yCoord = 0.0;
		double zCoord = BASE_WIDTH / 2 + BASE_WIDTH * mosaic.getMosaicHeight();
		for (int row = 0; row < mosaic.getMosaicHeight(); ++row) {
			xCoord = BASE_WIDTH / 2;
			for (int column = 0; column < mosaic.getMosaicWidth(); ++column) {
				ColoredBrick coloredBrick = mosaic.getMosaic()[row][column];
				if (coloredBrick.isComplete()) {
					Brick brick = coloredBrick.getBrick();
					double transformation[][] = MathUtils.calculateTransformation(
							brick.getRotation(), brick.getWidth() * BASE_WIDTH, brick.getHeight() * BASE_WIDTH,
							brick.isRectangular());
					double xTranslate = transformation[3][0] + xCoord;
					double yTranslate = transformation[3][1] + yCoord;
					double zTranslate = transformation[3][2] + zCoord;
					writeLdrawLine(String.format("1 %s %f %f %f %f %f %f %f %f %f %f %f %f %s",
							coloredBrick.getColor().getLdrawColorNumber(), xTranslate, yTranslate, zTranslate,
							transformation[0][0], transformation[0][1], transformation[0][2],
							transformation[1][0], transformation[1][1], transformation[1][2],
							transformation[2][0], transformation[2][1], transformation[2][2],
							brick.getLdrawFile()));
				}
				xCoord += BASE_WIDTH;
			}
			zCoord -= BASE_WIDTH;
		}

		printWriter.close();
		printWriter = null;
	}

	private void writeLdrawLine(String line) {
		System.out.println(line);
		if (printWriter != null) {
			printWriter.println(line);
		}
	}
}
