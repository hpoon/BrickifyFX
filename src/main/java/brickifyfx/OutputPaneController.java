package brickifyfx;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

import brickifyfx.core.BricksAndColors;
import brickifyfx.quantisation.QuantisationMethod;

public class OutputPaneController extends BasePaneController {
	@FXML
	private TitledPane titledPane;
	@FXML
	private Button saveButton;

	private File lastSaveFolder;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		titledPane.setExpanded(false);
	}

	@FXML
	protected void handleSave(@SuppressWarnings("unused") ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Mosaic");

		File baseFile = showSaveDialog(fileChooser, "mosaic");

		if (baseFile == null) {
			return;
		}

		final File imageFile = new File(baseFile.getAbsolutePath() + ".png");
		final File lDrawFile = new File(baseFile.getAbsolutePath() + ".ldr");

		System.out.println("Save file " + baseFile);

		try {
			// Save image
			ImageIO.write(getImageToWrite(), "png", imageFile);
			// Save LDraw
			new LdrawOutput().saveLdraw(lDrawFile, mainController.getBricksAndColors(), mainController.getMosaic());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		lastSaveFolder = baseFile.getParentFile();
	}

	/**
	 * Get the image to write. This is the mosaic image with alpha channel stripped, as this
	 * doesn't work with the JPEG export.
	 */
	private BufferedImage getImageToWrite() throws InterruptedException {
		BufferedImage image = SwingFXUtils.fromFXImage(mainController.getMosaicImage(), null);
		final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
		final ColorModel rgbOpaque = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

		PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
		pg.grabPixels();
		int width = pg.getWidth(), height = pg.getHeight();

		DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
		WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
		BufferedImage bi = new BufferedImage(rgbOpaque, raster, false, null);

		return bi;
	}

	private File showSaveDialog(FileChooser fileChooser, String defaultFileName) {
		File imageFile = mainController.getCurrentImageFile();

		File initialDirectory;
		if (imageFile == null) {
			if (lastSaveFolder == null) {
				initialDirectory = new File(System.getProperty("user.home"));
			} else {
				initialDirectory = lastSaveFolder;
			}
		} else {
			initialDirectory = imageFile.getParentFile();
		}

		fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setInitialFileName(defaultFileName);

		return fileChooser.showSaveDialog(titledPane.getScene().getWindow());
	}

	@Override
	public void imageLoaded(Image newImage) {
		// Disable "save" fields
		saveButton.setDisable(true);
		titledPane.setExpanded(false);
	}

	@Override
	public void mosaicRendered(BricksAndColors bricksAndColors, QuantisationMethod quantisationMethod, boolean threeDEffect, Image mosaicImage) {
		saveButton.setDisable(false);
		titledPane.setExpanded(true);
	}
}
