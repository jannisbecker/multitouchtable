package managers;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import utils.TouchPoint;

public class ImageManager {
	private static ImageManager instance;
	public static ImageManager getInstance() {
		if (instance == null) {
			instance = new ImageManager();
		}
		return instance;
	}
	
	/**
	 * Erstellt einen Image Manager und initialisiert die Webcam
	 */
	private ImageManager() {
		webcam = new VideoCapture(0);
		webcam.set(5, 60);
	};
	
	private VideoCapture webcam;
	private Mat bgImage = new Mat(), image = new Mat();
	private int frameCount;
	
	/**
	 * Gibt das aktuelle Webcambild zurück
	 * @return Das aktuelle Webcambild
	 */
	public Mat getImage() {
		return image;
	}
	
	/**
	 * Gibt das aktuelle Hintergrundbild zurück
	 * @return Das aktuelle Hintergrundbild
	 */
	public Mat getBackgroundImage() {
		return bgImage;
	}
	
	/**
	 * Erfasst ein neues Bild
	 * @return Boolean, ob dies geglückt ist
	 */
	public boolean captureFrame() {
		frameCount++;
		return webcam.read(image);
	}
	
	/**
	 * Sucht nach Touchpunkten, erstellt eine Liste und gibt diese zurück
	 * @param minSize Minimale Größe der Touchpunkte
	 * @param maxSize Maximale Größe der Touchpunkte
	 * @return Die gefüllte ArrayList
	 */
	public ArrayList<TouchPoint> calculateTouchPoints(int minSize, int maxSize) {
		ArrayList<TouchPoint> touchPoints = new ArrayList<TouchPoint>();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); 
		if(!bgImage.empty()) {
			Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
			int i = 0;
			for(MatOfPoint contour : contours) {
				Rect rect = Imgproc.boundingRect(contour);
				if(rect.area() >= minSize && rect.area() < maxSize) {
					i++;
					int x = rect.x + rect.width/2;
					int y = rect.y + rect.height/2;
					touchPoints.add(new TouchPoint(x, y, rect.width*rect.height));
					Core.rectangle(image, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255,0,0));
				}
			}
			//if(!touchPoints.isEmpty())
				//System.out.print(touchPoints.get(0).x+","+touchPoints.get(0).y);
		}
		return touchPoints;
	}
	
	/**
	 * Wendet einen Grauwertfilter an
	 */
	public void cvtGrayscale() {
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
	}
	
	/**
	 * Wendet eine quadratische Verstärkung an
	 */
	public void amplify() {
		Core.pow(image, 2, image);
	}
	
	/**
	 * Wendet einen Boxfilter mit der angegegebenen Größe an
	 * @param size Größe des Filterkerns
	 */
	public void boxFilter(int size) {
		Imgproc.boxFilter(image, image, -1, new Size(size,size));
	}
	
	/**
	 * Entfernt den Hintergrund aus dem Bild
	 */
	public void removeBackground() {
		if(frameCount > 5) {
			if(bgImage.empty()) {
				this.recaptureBackground();
				System.out.println("recaptured");
			}
			Core.absdiff(image, bgImage, image);
		}
	}
	
	/**
	 * Speichert das aktuelle Bild als Hintergrundbild
	 */
	public void recaptureBackground() {
		image.copyTo(bgImage);
	}
	
	/**
	 * Wendet einen binären Hochpassfilter an
	 * @param minValue Minimaler Wert, ab dem die Grauwerte durch Weiß ersetzt werden sollen
	 */
	public void binaryThreshold(int minValue) {
		Imgproc.threshold(image, image,minValue, 255, Imgproc.THRESH_BINARY);
	}
	
	/**
	 * Wendet einen Hochpassfilter an
	 * @param minValue Minimaler Wert, ab dem die Grauwerte durch Weiß ersetzt werden sollen
	 */
	public void threshold(int minValue) {
		Imgproc.threshold(image, image,minValue, 255, Imgproc.THRESH_TOZERO);
	}
}
