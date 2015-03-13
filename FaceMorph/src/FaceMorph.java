import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FaceMorph {
	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Point[] featurePoints1 = { new Point(92, 78), new Point(92, 98),
		// new Point(94, 140), new Point(94, 163), new Point(141, 84),
		// new Point(140, 100), new Point(140, 132), new Point(140, 150),
		// new Point(162, 108), new Point(162, 127) };
		// Point[] featurePoints2 = { new Point(84, 91), new Point(84, 110),
		// new Point(83, 142), new Point(83, 160), new Point(120, 87),
		// new Point(119, 112), new Point(117, 138), new Point(120, 154),
		// new Point(135, 118), new Point(134, 135) };
		// Mat sourceMat = Highgui.imread("test/1.png", 1);
		// Mat dstMat = Highgui.imread("test/2.png", 1);
		Point[] featurePoints1 = { new Point(316, 73), new Point(308, 172),
				new Point(294, 315), new Point(281, 414), new Point(438, 97),
				new Point(433, 198), new Point(427, 291), new Point(418, 388),
				new Point(508, 213), new Point(508, 279) };
		Point[] featurePoints2 = { new Point(274, 119), new Point(283, 213),
				new Point(288, 387), new Point(296, 473), new Point(434, 116),
				new Point(442, 230), new Point(448, 343), new Point(448, 437),
				new Point(536, 236), new Point(539, 313) };
		Mat sourceMat = Highgui.imread("test/6.jpg", 1);
		// drawPoints(sourceMat, featurePoints1);
		Imshow imshow4 = new Imshow("src");
		// imshow4.showImage(sourceMat);
		Mat dstMat = Highgui.imread("test/5.jpg", 1);
		// drawPoints(dstMat, featurePoints2);
		Imshow imshow5 = new Imshow("dst");
		// imshow5.showImage(dstMat);
		Region[] srcRegions = getRegions(sourceMat, featurePoints1);
		Region[] dstRegions = getRegions(dstMat, featurePoints2);
		// Mat[] srcMats = cropMat(sourceMat, srcRegions);
		// Mat[] dstMats = cropMat(dstMat, dstRegions);

		List<String> imageList = new ArrayList<String>();
		double alpha = 50;
		double delta = 100;
		Imshow imshow = new Imshow("morphing");
		while (alpha <= 100) {
			double t = Core.getTickCount();
			Point[] tmpPoints = calculateFeatureDstPoints((double) alpha / 100,
					featurePoints1, featurePoints2);
			Region[] tmpRegions = getRegions(sourceMat, tmpPoints);
			// Mat tmp_srcMat = transform(srcMats, srcRegions, tmpRegions);
			// Mat tmp_dstMat = transform(dstMats, dstRegions, tmpRegions);
			Mat tmp_srcMat = transform1(sourceMat, srcRegions, tmpRegions);
			Imshow imshow2 = new Imshow("TMP1");
			// imshow2.showImage(tmp_srcMat);
			Mat tmp_dstMat = transform1(dstMat, dstRegions, tmpRegions);
			Imshow imshow3 = new Imshow("TMP1");
			// imshow3.showImage(tmp_dstMat);
			Mat tmpMat = Mat.zeros(tmp_dstMat.size(), CvType.CV_8UC3);
			Core.addWeighted(tmp_srcMat, 1 - (double) alpha / 100, tmp_dstMat,
					(double) alpha / 100, 0, tmpMat);
			String path = "test-result-2/" + (int) alpha + ".jpg";
			Highgui.imwrite(path, tmpMat);
			imageList.add(path);
			tmp_srcMat.release();
			tmp_dstMat.release();
			tmpMat.release();
			// imshow.showImage(tmpMat);
			alpha += delta;

			t = (Core.getTickCount() - t) / Core.getTickFrequency();
			System.out.println("Times passed in seconds: " + t);
		}

		for (int i = 0; i < 101; i++) {
			imageList.add("test-result-2/" + i + ".jpg");
		}
		for (int i = 0; i < imageList.size(); i++) {
			System.out.println(imageList.get(i));
			imshow.showImage(Highgui.imread(imageList.get(i)));
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// t = (Core.getTickCount() - t) / Core.getTickFrequency();
		// System.out.println("Times passed in seconds: " + t);

	}

	private static void drawPoints(Mat sourceMat, Point[] featurePoints1) {
		// TODO Auto-generated method stub
		double[] green = { 0, 255, 0 };
		for (int i = 0; i < featurePoints1.length; i++) {
			sourceMat.put((int) featurePoints1[i].x, (int) featurePoints1[i].y,
					green);
			sourceMat.put((int) featurePoints1[i].x - 1,
					(int) featurePoints1[i].y - 1, green);
			sourceMat.put((int) featurePoints1[i].x - 1,
					(int) featurePoints1[i].y, green);
			sourceMat.put((int) featurePoints1[i].x,
					(int) featurePoints1[i].y - 1, green);
		}
	}

	private static Mat transform1(Mat sourceMat, Region[] srcRegions,
			Region[] dstRegions) {
		// TODO Auto-generated method stub
		Mat resultMat = Mat.zeros(sourceMat.size(), CvType.CV_8UC3);
		for (int i = 0; i < sourceMat.rows(); i++) {
			for (int j = 0; j < sourceMat.cols(); j++) {
				Point point = new Point(i, j);
				for (int k = 0; k < dstRegions.length; k++) {
					// boolean flag;
					// double t = Core.getTickCount();
					// for (long m = 0; m < 20000000; m++) {
					// flag = dstRegions[k].isInRegion(point);
					// }
					// t = (Core.getTickCount() - t) / Core.getTickFrequency();
					// System.out.println(k + "Times passed in seconds: " + t);
					if (dstRegions[k].isInRegion(point)) {
						Point originPoint = dstRegions[k].transformation(point,
								srcRegions[k]);
						// System.out.println("i:" + i + " j:" + j + " x:"
						// + originPoint.x + " y:" + originPoint.y);
						originPoint.x = Math.min(sourceMat.rows() - 1,
								originPoint.x);
						originPoint.y = Math.min(sourceMat.cols() - 1,
								originPoint.y);
						resultMat.put(i, j, sourceMat.get((int) originPoint.x,
								(int) originPoint.y));
						break;

					}
				}
			}
		}

		return resultMat;
	}

	private static Mat[] cropMat(Mat sourceMat, Region[] regions) {
		// TODO Auto-generated method stub
		Mat[] mats = new Mat[22];
		for (int i = 0; i < mats.length; i++) {
			mats[i] = Mat.zeros(sourceMat.size(), CvType.CV_8UC3);
		}

		for (int i = 0; i < sourceMat.rows(); i++) {
			for (int j = 0; j < sourceMat.cols(); j++) {
				double[] tmpPoint = sourceMat.get(i, j);
				for (int k = 0; k < regions.length; k++) {
					if (regions[k].isInRegion(new Point(i, j))) {
						mats[k].put(i, j, tmpPoint);
						break;
					}
				}
			}
		}
		return mats;
	}

	private static Region[] getRegions(Mat sourceMat, Point[] featurePoints) {
		// TODO Auto-generated method stub
		Region[] regions = new Region[22];
		Point l_topPoint = new Point(0, 0);
		Point r_topPoint = new Point(0, sourceMat.cols() - 1);
		Point r_bottomPoint = new Point(sourceMat.rows() - 1,
				sourceMat.cols() - 1);
		Point l_bottomPoint = new Point(sourceMat.rows() - 1, 0);
		// regions[0] = new Quadrangular(l_topPoint, r_topPoint,
		// featurePoints[2],
		// featurePoints[1]);
		regions[0] = new Triangular(l_topPoint, r_topPoint, featurePoints[1]);
		regions[1] = new Triangular(r_topPoint, featurePoints[1],
				featurePoints[2]);
		regions[2] = new Triangular(l_topPoint, l_bottomPoint, featurePoints[0]);
		regions[3] = new Triangular(l_topPoint, featurePoints[0],
				featurePoints[1]);
		regions[4] = new Triangular(featurePoints[2], featurePoints[3],
				r_topPoint);
		regions[5] = new Triangular(r_topPoint, featurePoints[3], r_bottomPoint);
		// regions[5] = new Quadrangular(featurePoints[2], featurePoints[3],
		// featurePoints[7], featurePoints[6]);
		regions[6] = new Triangular(featurePoints[2], featurePoints[3],
				featurePoints[6]);
		regions[7] = new Triangular(featurePoints[3], featurePoints[7],
				featurePoints[6]);
		// regions[6] = new Quadrangular(featurePoints[1], featurePoints[2],
		// featurePoints[6], featurePoints[5]);
		regions[8] = new Triangular(featurePoints[1], featurePoints[2],
				featurePoints[5]);
		regions[9] = new Triangular(featurePoints[2], featurePoints[6],
				featurePoints[5]);
		// regions[7] = new Quadrangular(featurePoints[0], featurePoints[1],
		// featurePoints[5], featurePoints[4]);
		regions[10] = new Triangular(featurePoints[0], featurePoints[1],
				featurePoints[4]);
		regions[11] = new Triangular(featurePoints[1], featurePoints[5],
				featurePoints[4]);
		regions[12] = new Triangular(featurePoints[0], featurePoints[4],
				l_bottomPoint);
		regions[13] = new Triangular(featurePoints[4], featurePoints[8],
				l_bottomPoint);
		regions[14] = new Triangular(featurePoints[4], featurePoints[5],
				featurePoints[8]);
		// regions[11] = new Quadrangular(featurePoints[5], featurePoints[6],
		// featurePoints[9], featurePoints[8]);
		regions[15] = new Triangular(featurePoints[5], featurePoints[6],
				featurePoints[8]);
		regions[16] = new Triangular(featurePoints[6], featurePoints[9],
				featurePoints[8]);
		regions[17] = new Triangular(featurePoints[6], featurePoints[7],
				featurePoints[9]);
		regions[18] = new Triangular(featurePoints[3], featurePoints[7],
				r_bottomPoint);
		regions[19] = new Triangular(featurePoints[7], featurePoints[9],
				r_bottomPoint);
		// regions[15] = new Quadrangular(featurePoints[8], featurePoints[9],
		// r_bottomPoint, l_bottomPoint);
		regions[20] = new Triangular(featurePoints[8], featurePoints[9],
				l_bottomPoint);
		regions[21] = new Triangular(featurePoints[9], r_bottomPoint,
				l_bottomPoint);
		return regions;
	}

	private static Point[] calculateFeatureDstPoints(double alpha,
			Point[] featurePoints1, Point[] featurePoints2) {
		// TODO Auto-generated method stub
		Point[] dstPoints = new Point[featurePoints1.length];
		for (int i = 0; i < dstPoints.length; i++) {
			double x1 = featurePoints1[i].x;
			double y1 = featurePoints1[i].y;
			double x2 = featurePoints2[i].x;
			double y2 = featurePoints2[i].y;
			dstPoints[i] = new Point(alpha * x2 + (1 - alpha) * x1, alpha * y2
					+ (1 - alpha) * y1);
		}
		return dstPoints;
	}

	private static Mat transform(Mat[] sourceMats, Region[] srcRegions,
			Region[] dstRegions) {
		// TODO Auto-generated method stub
		Mat resultMat = Mat.zeros(sourceMats[0].size(), CvType.CV_8UC3);
		Mat tmpMat = new Mat();

		for (int i = 0; i < sourceMats.length; i++) {
			if (srcRegions[i].getRegionType() == Region.REGION_TYPE_TRIANGULAR) {

				MatOfPoint2f srcMatOfPoint2f = new MatOfPoint2f(
						srcRegions[i].getPoints());
				MatOfPoint2f dstMatOfPoint2f = new MatOfPoint2f(
						dstRegions[i].getPoints());
				Mat transMat = Imgproc.getAffineTransform(srcMatOfPoint2f,
						dstMatOfPoint2f);
				// Imgproc.warpAffine(sourceMats[i], tmpMat, transMat,
				// sourceMats[i].size());

				Imgproc.warpAffine(sourceMats[i], tmpMat, transMat,
						sourceMats[i].size(), Imgproc.WARP_INVERSE_MAP,
						Imgproc.BORDER_TRANSPARENT, new Scalar(0));

				Imshow imshow = new Imshow("origin" + i);
				// imshow.showImage(sourceMats[i]);
				Imshow imshow1 = new Imshow("tmp" + i);
				// imshow1.showImage(tmpMat);
				Mat tmp2 = Mat.zeros(sourceMats[i].size(), CvType.CV_8UC3);
				// Core.addWeighted(sourceMats[i], 0.5, tmpMat, 0.5, 0, tmp2);
				Core.addWeighted(resultMat, 1, tmpMat, 1, 0, resultMat);
				Imshow imshow2 = new Imshow("result" + i);
				// imshow2.showImage(resultMat);
			} else {
				MatOfPoint2f srcMatOfPoint2f = new MatOfPoint2f(
						srcRegions[i].getPoints());
				MatOfPoint2f dstMatOfPoint2f = new MatOfPoint2f(
						dstRegions[i].getPoints());
				Mat transMat = Imgproc.getPerspectiveTransform(srcMatOfPoint2f,
						dstMatOfPoint2f);
				Imgproc.warpPerspective(sourceMats[i], tmpMat, transMat,
						sourceMats[i].size());

				Imshow imshow = new Imshow("origin" + i);
				// imshow.showImage(sourceMats[i]);
				Imshow imshow1 = new Imshow("tmp" + i);
				// imshow1.showImage(tmpMat);
				// Mat tmp2 = Mat.zeros(sourceMats[i].size(), CvType.CV_8UC3);
				// Core.addWeighted(sourceMats[i], 0.5, tmpMat, 0.5, 0, tmp2);
				Core.addWeighted(resultMat, 1, tmpMat, 1, 0, resultMat);
				Imshow imshow2 = new Imshow("result" + i);
				// imshow2.showImage(resultMat);

			}
		}

		Imshow imshow1 = new Imshow("final");
		// imshow1.showImage(resultMat);

		return resultMat;

	}
}
