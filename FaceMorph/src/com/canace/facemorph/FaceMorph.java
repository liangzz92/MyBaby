package com.canace.facemorph;

import java.awt.Point;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class FaceMorph {

	static int[] trycounts = new int[22];

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		double time = Core.getTickCount();
		Point[][] points = new Point[4][10];
		points[0] = new Point[] { new Point(316, 73), new Point(308, 172),
				new Point(294, 315), new Point(281, 414), new Point(438, 97),
				new Point(433, 198), new Point(427, 291), new Point(418, 388),
				new Point(508, 213), new Point(508, 279) };
		points[1] = new Point[] { new Point(274, 119), new Point(283, 213),
				new Point(288, 387), new Point(296, 473), new Point(434, 116),
				new Point(442, 230), new Point(448, 343), new Point(448, 437),
				new Point(536, 236), new Point(539, 313) };
		// points[0] = new Point[] { new Point(92, 78), new Point(92, 98),
		// new Point(94, 140), new Point(94, 163), new Point(141, 84),
		// new Point(140, 100), new Point(140, 132), new Point(140, 150),
		// new Point(162, 108), new Point(162, 127) };
		// points[1] = new Point[] { new Point(84, 91), new Point(84, 110),
		// new Point(83, 142), new Point(83, 160), new Point(120, 87),
		// new Point(119, 112), new Point(117, 138), new Point(120, 154),
		// new Point(135, 118), new Point(134, 135) };
		String[] pathStrings = { "test/6.jpg", "test/5.jpg" }; // "test/1.png",
																// "test/2.png"
																// };

		Mat sourceMat, dstMat, resultMat, tmp_srcMat, tmp_dstMat;
		PartitionedImage srcPartitionedImage, dstPartitionedImage, tmpPartitionedImage = null;
		for (int i = 0; i < pathStrings.length - 1; i++) {
			sourceMat = Highgui.imread(pathStrings[i], 1);
			dstMat = Highgui.imread(pathStrings[i + 1], 1);
			resultMat = Mat.zeros(sourceMat.size(), CvType.CV_8UC3);
			tmp_srcMat = Mat.zeros(sourceMat.size(), CvType.CV_8UC3);
			tmp_dstMat = Mat.zeros(sourceMat.size(), CvType.CV_8UC3);
			srcPartitionedImage = new PartitionedImage(points[i], false,
					sourceMat);
			Triangular[] srcRegions = srcPartitionedImage.getRegions();
			dstPartitionedImage = new PartitionedImage(points[i + 1], false,
					dstMat);
			Triangular[] dstRegions = dstPartitionedImage.getRegions();
			Triangular[] tmpRegions;
			int alpha = 0;
			int delta = 2;
			Imshow imshow = new Imshow("morphing");
			while (alpha <= 100) {
				double t = Core.getTickCount();
				Point[] tmpPoints = calculateFeatureDstPoints(
						(double) alpha / 100, points[i], points[i + 1]);
				if (alpha == 0) {
					tmpPartitionedImage = new PartitionedImage(tmpPoints, true,
							sourceMat);
					tmpRegions = tmpPartitionedImage.getRegions();
				} else {
					tmpPartitionedImage.setFeaturePoints(tmpPoints);
					tmpRegions = tmpPartitionedImage.getRegions();
				}
				t = (Core.getTickCount() - t) / Core.getTickFrequency();
				System.out.println("getRegions Times passed in seconds: " + t);
				// t = Core.getTickCount();
				// Triangular[] tmpRegions = getRegions(sourceMat, tmpPoints,
				// true);
				// affineTransform(tmp_srcMat, sourceMat, srcRegions,
				// tmpRegions);
				// affineTransform(tmp_dstMat, dstMat, dstRegions, tmpRegions);
				// Core.addWeighted(tmp_srcMat, 1 - (double) alpha / 100,
				// tmp_dstMat, (double) alpha / 100, 0, resultMat);
				// t = Core.getTickCount();

				// colorMorph(resultMat, sourceMat, dstMat, (double) alpha /
				// 100);
				affineTransform(tmp_srcMat, tmp_dstMat, resultMat, sourceMat,
						srcRegions, dstMat, dstRegions, (double) alpha / 100,
						tmpRegions);
				imshow.showImage(resultMat);

				// tmp_srcMat.release();
				// tmp_dstMat.release();
				// tmpMat2.release();

				alpha += delta;

				t = (Core.getTickCount() - t) / Core.getTickFrequency();
				System.out.println("Times passed in seconds: " + t);
			}
			sourceMat.release();
			dstMat.release();
		}
		time = (Core.getTickCount() - time) / Core.getTickFrequency();
		System.out.println("Times passed in seconds: " + time);
		for (int i = 21; i >= 0; i--) {
			System.out.println((21 - i + 1) + " trycount:" + trycounts[i]);
		}
	}

	private static void affineTransform(Mat resultMat, Mat sourceMat,
			Triangular[] srcRegions, Triangular[] tmpRegions) {
		// TODO Auto-generated method stub
		double t = Core.getTickCount();
		int last_region_index = 0;
		for (int i = 0; i < sourceMat.rows(); i++) {
			for (int j = 0; j < sourceMat.cols(); j++) {
				// double t = Core.getTickCount();

				Point point = new Point(i, j);
				int try_count = tmpRegions.length - 1;
				while (try_count >= 0) {
					if (tmpRegions[last_region_index].isInRegion(point)) {

						Point src_originPoint = tmpRegions[last_region_index]
								.transformation(point,
										srcRegions[last_region_index]);

						src_originPoint.x = Math.min(sourceMat.rows() - 1,
								src_originPoint.x);
						src_originPoint.y = Math.min(sourceMat.cols() - 1,
								src_originPoint.y);

						// System.out.println("x:" + src_originPoint.x + " y:"
						// + src_originPoint.y);
						// double time = Core.getTickCount();
						resultMat.put(i, j, sourceMat.get(src_originPoint.x,
								src_originPoint.y));
						trycounts[try_count]++;
						// time = (Core.getTickCount() - time)
						// / Core.getTickFrequency();
						// System.out
						// .println("calculate Times passed in seconds: "
						// + time);
						break;
					} else {
						last_region_index = (last_region_index + 1)
								% tmpRegions.length;
					}

					try_count--;
				}

			}
		}
		t = (Core.getTickCount() - t) / Core.getTickFrequency();
		System.out.println("Times passed in seconds: " + t);
		// return resultMat;
	}

	private static void colorMorph(Mat resultMat, Mat sourceMat, Mat dstMat,
			double d) {
		// TODO Auto-generated method stub
		Core.addWeighted(sourceMat, 1 - d, dstMat, d, 0, resultMat);
	}

	private static void affineTransform(Mat tmp_srcMat, Mat tmp_dstMat,
			Mat resultMat, Mat sourceMat, Triangular[] srcRegions, Mat dstMat,
			Triangular[] dstRegions, double alpha, Triangular[] tmpRegions) {
		// TODO Auto-generated method stub
		// double t = Core.getTickCount();
		int last_region_index = 0;
		double[] new_point = new double[3];
		for (int i = 0; i < sourceMat.rows(); i++) {
			for (int j = 0; j < sourceMat.cols(); j++) {
				// double t = Core.getTickCount();

				Point point = new Point(i, j);
				int try_count = tmpRegions.length - 1;
				while (try_count >= 0) {
					if (tmpRegions[last_region_index].isInRegion(point)) {

						Point src_originPoint = new Point();
						Point dst_originPoint = new Point();

						src_originPoint = tmpRegions[last_region_index]
								.transformation(point,
										srcRegions[last_region_index]);

						src_originPoint.x = Math.min(sourceMat.rows() - 1,
								src_originPoint.x);
						src_originPoint.y = Math.min(sourceMat.cols() - 1,
								src_originPoint.y);
						// System.out.println("x:" + src_originPoint.x +
						// " y:"
						// + src_originPoint.y);
						dst_originPoint = tmpRegions[last_region_index]
								.transformation(point,
										dstRegions[last_region_index]);

						dst_originPoint.x = Math.min(sourceMat.rows() - 1,
								dst_originPoint.x);
						dst_originPoint.y = Math.min(sourceMat.cols() - 1,
								dst_originPoint.y);
						// System.out.println("x:" + dst_originPoint.x +
						// " y:"
						// + dst_originPoint.y);
						double t = Core.getTickCount();
						for (int k = 0; k < 288000; k++) {
							double[] src_point = sourceMat.get(
									src_originPoint.x, src_originPoint.y);
							double[] dst_point = dstMat.get(dst_originPoint.x,
									dst_originPoint.y);
							mergePoints(new_point, alpha, src_point, dst_point);
						}
						t = (Core.getTickCount() - t) / Core.getTickFrequency();
						System.out
								.println("calculate  points Times passed in seconds: "
										+ t);
						// mergePoints(new_point, alpha, src_point, dst_point);
						// double time = Core.getTickCount();
						// for (int k = 0; k < 288000; k++) {
						// mergePoints(new_point, alpha, src_point, dst_point);
						resultMat.put(i, j, new_point);
						// }

						// time = (Core.getTickCount() - time)
						// / Core.getTickFrequency();
						// System.out.println("put Times passed in seconds: "
						// + time);
						trycounts[try_count]++;

						break;
					} else {
						last_region_index = (last_region_index + 1)
								% dstRegions.length;
					}

					try_count--;
				}
				// t = (Core.getTickCount() - t) / Core.getTickFrequency();
				// System.out.println("one point Times passed in seconds: " +
				// t);

			}
		}

		// t = (Core.getTickCount() - t) / Core.getTickFrequency();
		// System.out.println("Times passed in seconds: " + t);

		// return resultMat;
	}

	private static void mergePoints(double[] new_point, double alpha,
			double[] src, double[] dst) {
		// TODO Auto-generated method stub
		// double[] result = new double[3];
		// System.out.println(src.length);
		for (int i = 0; i < new_point.length; i++) {
			new_point[i] = src[i] * (1 - alpha) + dst[i] * alpha;
		}
		// return result;
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

	private static Mat transform1(Mat sourceMat, Triangular[] srcRegions,
			Triangular[] dstRegions) {
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
						trycounts[21 - k]++;
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

	private static Triangular[] getRegions(Mat sourceMat,
			Point[] featurePoints, boolean needCalculated) {
		// TODO Auto-generated method stub
		double t = Core.getTickCount();
		Triangular[] regions = new Triangular[22];
		Point l_topPoint = new Point(0, 0);
		Point r_topPoint = new Point(0, sourceMat.cols() - 1);
		Point r_bottomPoint = new Point(sourceMat.rows() - 1,
				sourceMat.cols() - 1);
		Point l_bottomPoint = new Point(sourceMat.rows() - 1, 0);
		// regions[0] = new Quadrangular(l_topPoint, r_topPoint,
		// featurePoints[2],
		// featurePoints[1]);
		regions[0] = new Triangular(l_topPoint, r_topPoint, featurePoints[1],
				needCalculated);
		regions[1] = new Triangular(r_topPoint, featurePoints[1],
				featurePoints[2], needCalculated);
		regions[2] = new Triangular(l_topPoint, l_bottomPoint,
				featurePoints[0], needCalculated);
		regions[3] = new Triangular(l_topPoint, featurePoints[0],
				featurePoints[1], needCalculated);
		regions[4] = new Triangular(featurePoints[2], featurePoints[3],
				r_topPoint, needCalculated);
		regions[5] = new Triangular(r_topPoint, featurePoints[3],
				r_bottomPoint, needCalculated);

		// regions[7] = new Quadrangular(featurePoints[0], featurePoints[1],
		// featurePoints[5], featurePoints[4]);
		regions[6] = new Triangular(featurePoints[0], featurePoints[1],
				featurePoints[4], needCalculated);
		regions[7] = new Triangular(featurePoints[1], featurePoints[5],
				featurePoints[4], needCalculated);
		// regions[6] = new Quadrangular(featurePoints[1], featurePoints[2],
		// featurePoints[6], featurePoints[5]);
		regions[8] = new Triangular(featurePoints[1], featurePoints[2],
				featurePoints[5], needCalculated);
		regions[9] = new Triangular(featurePoints[2], featurePoints[6],
				featurePoints[5], needCalculated);
		// regions[5] = new Quadrangular(featurePoints[2], featurePoints[3],
		// featurePoints[7], featurePoints[6]);
		regions[10] = new Triangular(featurePoints[2], featurePoints[3],
				featurePoints[6], needCalculated);
		regions[11] = new Triangular(featurePoints[3], featurePoints[7],
				featurePoints[6], needCalculated);

		regions[12] = new Triangular(featurePoints[0], featurePoints[4],
				l_bottomPoint, needCalculated);
		regions[13] = new Triangular(featurePoints[4], featurePoints[8],
				l_bottomPoint, needCalculated);
		regions[14] = new Triangular(featurePoints[4], featurePoints[5],
				featurePoints[8], needCalculated);
		// regions[11] = new Quadrangular(featurePoints[5], featurePoints[6],
		// featurePoints[9], featurePoints[8]);
		regions[15] = new Triangular(featurePoints[5], featurePoints[6],
				featurePoints[8], needCalculated);
		regions[16] = new Triangular(featurePoints[6], featurePoints[9],
				featurePoints[8], needCalculated);
		regions[17] = new Triangular(featurePoints[6], featurePoints[7],
				featurePoints[9], needCalculated);
		regions[18] = new Triangular(featurePoints[3], featurePoints[7],
				r_bottomPoint, needCalculated);
		regions[19] = new Triangular(featurePoints[7], featurePoints[9],
				r_bottomPoint, needCalculated);
		// regions[15] = new Quadrangular(featurePoints[8], featurePoints[9],
		// r_bottomPoint, l_bottomPoint);
		regions[20] = new Triangular(featurePoints[8], featurePoints[9],
				l_bottomPoint, needCalculated);
		regions[21] = new Triangular(featurePoints[9], r_bottomPoint,
				l_bottomPoint, needCalculated);
		t = (Core.getTickCount() - t) / Core.getTickFrequency();
		System.out.println("Times passed in seconds: " + t);
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
			dstPoints[i] = new Point((int) (alpha * x2 + (1 - alpha) * x1),
					(int) (alpha * y2 + (1 - alpha) * y1));
		}
		return dstPoints;
	}

	// private static Mat transform(Mat[] sourceMats, Region[] srcRegions,
	// Region[] dstRegions) {
	// // TODO Auto-generated method stub
	// Mat resultMat = Mat.zeros(sourceMats[0].size(), CvType.CV_8UC3);
	// Mat tmpMat = new Mat();
	//
	// for (int i = 0; i < sourceMats.length; i++) {
	// if (srcRegions[i].getRegionType() == Region.REGION_TYPE_TRIANGULAR) {
	//
	// MatOfPoint2f srcMatOfPoint2f = new MatOfPoint2f(
	// srcRegions[i].getPoints());
	// MatOfPoint2f dstMatOfPoint2f = new MatOfPoint2f(
	// dstRegions[i].getPoints());
	// Mat transMat = Imgproc.getAffineTransform(srcMatOfPoint2f,
	// dstMatOfPoint2f);
	// // Imgproc.warpAffine(sourceMats[i], tmpMat, transMat,
	// // sourceMats[i].size());
	//
	// Imgproc.warpAffine(sourceMats[i], tmpMat, transMat,
	// sourceMats[i].size(), Imgproc.WARP_INVERSE_MAP,
	// Imgproc.BORDER_TRANSPARENT, new Scalar(0));
	//
	// Imshow imshow = new Imshow("origin" + i);
	// // imshow.showImage(sourceMats[i]);
	// Imshow imshow1 = new Imshow("tmp" + i);
	// // imshow1.showImage(tmpMat);
	// Mat tmp2 = Mat.zeros(sourceMats[i].size(), CvType.CV_8UC3);
	// // Core.addWeighted(sourceMats[i], 0.5, tmpMat, 0.5, 0, tmp2);
	// Core.addWeighted(resultMat, 1, tmpMat, 1, 0, resultMat);
	// Imshow imshow2 = new Imshow("result" + i);
	// // imshow2.showImage(resultMat);
	// } else {
	// MatOfPoint2f srcMatOfPoint2f = new MatOfPoint2f(
	// srcRegions[i].getPoints());
	// MatOfPoint2f dstMatOfPoint2f = new MatOfPoint2f(
	// dstRegions[i].getPoints());
	// Mat transMat = Imgproc.getPerspectiveTransform(srcMatOfPoint2f,
	// dstMatOfPoint2f);
	// Imgproc.warpPerspective(sourceMats[i], tmpMat, transMat,
	// sourceMats[i].size());
	//
	// Imshow imshow = new Imshow("origin" + i);
	// // imshow.showImage(sourceMats[i]);
	// Imshow imshow1 = new Imshow("tmp" + i);
	// // imshow1.showImage(tmpMat);
	// // Mat tmp2 = Mat.zeros(sourceMats[i].size(), CvType.CV_8UC3);
	// // Core.addWeighted(sourceMats[i], 0.5, tmpMat, 0.5, 0, tmp2);
	// Core.addWeighted(resultMat, 1, tmpMat, 1, 0, resultMat);
	// Imshow imshow2 = new Imshow("result" + i);
	// // imshow2.showImage(resultMat);
	//
	// }
	// }
	//
	// Imshow imshow1 = new Imshow("final");
	// // imshow1.showImage(resultMat);
	//
	// return resultMat;
	//
	// }
}
