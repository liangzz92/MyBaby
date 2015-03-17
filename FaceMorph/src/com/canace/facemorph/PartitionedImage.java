/**
 * com.canace.facemorph PartitionedImage.java
 * 
 * @author liangzz
 * 2015-3-15
 */
package com.canace.facemorph;

import java.awt.Point;

public class PartitionedImage {

	Triangular[] regions;
	Point[] featurePoints;
	Point l_topPoint;
	Point r_topPoint;
	Point r_bottomPoint;
	Point l_bottomPoint;

	public PartitionedImage(java.awt.Point[] tmpPoints, boolean needCalculated,
			int width, int height) {
		// double t = Core.getTickCount();

		l_topPoint = new Point(0, 0);
		r_topPoint = new Point(0, width - 1);
		r_bottomPoint = new Point(height - 1, width - 1);
		l_bottomPoint = new Point(height - 1, 0);
		this.featurePoints = tmpPoints;
		regions = new Triangular[22];
		regions[0] = new Triangular(l_topPoint, r_topPoint, tmpPoints[1],
				needCalculated);
		regions[1] = new Triangular(r_topPoint, tmpPoints[1], tmpPoints[2],
				needCalculated);
		regions[2] = new Triangular(l_topPoint, l_bottomPoint, tmpPoints[0],
				needCalculated);
		regions[3] = new Triangular(l_topPoint, tmpPoints[0], tmpPoints[1],
				needCalculated);
		regions[4] = new Triangular(tmpPoints[2], tmpPoints[3], r_topPoint,
				needCalculated);
		regions[5] = new Triangular(r_topPoint, tmpPoints[3], r_bottomPoint,
				needCalculated);

		// regions[7] = new Quadrangular(featurePoints[0], featurePoints[1],
		// featurePoints[5], featurePoints[4]);
		regions[6] = new Triangular(tmpPoints[0], tmpPoints[1], tmpPoints[4],
				needCalculated);
		regions[7] = new Triangular(tmpPoints[1], tmpPoints[5], tmpPoints[4],
				needCalculated);
		// regions[6] = new Quadrangular(featurePoints[1], featurePoints[2],
		// featurePoints[6], featurePoints[5]);
		regions[8] = new Triangular(tmpPoints[1], tmpPoints[2], tmpPoints[5],
				needCalculated);
		regions[9] = new Triangular(tmpPoints[2], tmpPoints[6], tmpPoints[5],
				needCalculated);
		// regions[5] = new Quadrangular(featurePoints[2], featurePoints[3],
		// featurePoints[7], featurePoints[6]);
		regions[10] = new Triangular(tmpPoints[2], tmpPoints[3], tmpPoints[6],
				needCalculated);
		regions[11] = new Triangular(tmpPoints[3], tmpPoints[7], tmpPoints[6],
				needCalculated);

		regions[12] = new Triangular(tmpPoints[0], tmpPoints[4], l_bottomPoint,
				needCalculated);
		regions[13] = new Triangular(tmpPoints[4], tmpPoints[8], l_bottomPoint,
				needCalculated);
		regions[14] = new Triangular(tmpPoints[4], tmpPoints[5], tmpPoints[8],
				needCalculated);
		// regions[11] = new Quadrangular(featurePoints[5], featurePoints[6],
		// featurePoints[9], featurePoints[8]);
		regions[15] = new Triangular(tmpPoints[5], tmpPoints[6], tmpPoints[8],
				needCalculated);
		regions[16] = new Triangular(tmpPoints[6], tmpPoints[9], tmpPoints[8],
				needCalculated);
		regions[17] = new Triangular(tmpPoints[6], tmpPoints[7], tmpPoints[9],
				needCalculated);
		regions[18] = new Triangular(tmpPoints[3], tmpPoints[7], r_bottomPoint,
				needCalculated);
		regions[19] = new Triangular(tmpPoints[7], tmpPoints[9], r_bottomPoint,
				needCalculated);
		// regions[15] = new Quadrangular(featurePoints[8], featurePoints[9],
		// r_bottomPoint, l_bottomPoint);
		regions[20] = new Triangular(tmpPoints[8], tmpPoints[9], l_bottomPoint,
				needCalculated);
		regions[21] = new Triangular(tmpPoints[9], r_bottomPoint,
				l_bottomPoint, needCalculated);
		// t = (Core.getTickCount() - t) / Core.getTickFrequency();
		// System.out.println("Times passed in seconds: " + t);
	}

	public Triangular[] getRegions() {
		return regions;
	}

	public void setFeaturePoints(Point[] points) {
		// double t = Core.getTickCount();
		featurePoints = points;
		regions[0].setPoints(l_topPoint, r_topPoint, featurePoints[1]);
		regions[1].setPoints(r_topPoint, featurePoints[1], featurePoints[2]);
		regions[2].setPoints(l_topPoint, l_bottomPoint, featurePoints[0]);
		regions[3].setPoints(l_topPoint, featurePoints[0], featurePoints[1]);
		regions[4].setPoints(featurePoints[2], featurePoints[3], r_topPoint);
		regions[5].setPoints(r_topPoint, featurePoints[3], r_bottomPoint);

		// regions[7] = new Quadrangular(featurePoints[0], featurePoints[1],
		// featurePoints[5], featurePoints[4]);
		regions[6].setPoints(featurePoints[0], featurePoints[1],
				featurePoints[4]);
		regions[7].setPoints(featurePoints[1], featurePoints[5],
				featurePoints[4]);
		// regions[6] = new Quadrangular(featurePoints[1], featurePoints[2],
		// featurePoints[6], featurePoints[5]);
		regions[8].setPoints(featurePoints[1], featurePoints[2],
				featurePoints[5]);
		regions[9].setPoints(featurePoints[2], featurePoints[6],
				featurePoints[5]);
		// regions[5] = new Quadrangular(featurePoints[2], featurePoints[3],
		// featurePoints[7], featurePoints[6]);
		regions[10].setPoints(featurePoints[2], featurePoints[3],
				featurePoints[6]);
		regions[11].setPoints(featurePoints[3], featurePoints[7],
				featurePoints[6]);

		regions[12]
				.setPoints(featurePoints[0], featurePoints[4], l_bottomPoint);
		regions[13]
				.setPoints(featurePoints[4], featurePoints[8], l_bottomPoint);
		regions[14].setPoints(featurePoints[4], featurePoints[5],
				featurePoints[8]);
		// regions[11] = new Quadrangular(featurePoints[5], featurePoints[6],
		// featurePoints[9], featurePoints[8]);
		regions[15].setPoints(featurePoints[5], featurePoints[6],
				featurePoints[8]);
		regions[16].setPoints(featurePoints[6], featurePoints[9],
				featurePoints[8]);
		regions[17].setPoints(featurePoints[6], featurePoints[7],
				featurePoints[9]);
		regions[18]
				.setPoints(featurePoints[3], featurePoints[7], r_bottomPoint);
		regions[19]
				.setPoints(featurePoints[7], featurePoints[9], r_bottomPoint);
		// regions[15] = new Quadrangular(featurePoints[8], featurePoints[9],
		// r_bottomPoint, l_bottomPoint);
		regions[20]
				.setPoints(featurePoints[8], featurePoints[9], l_bottomPoint);
		regions[21].setPoints(featurePoints[9], r_bottomPoint, l_bottomPoint);
		// t = (Core.getTickCount() - t) / Core.getTickFrequency();
		// System.out.println("Times passed in seconds: " + t);
	}
}
