package com.canace.facemorph;

import java.awt.Polygon;

import org.opencv.core.Point;

/**
 * @author liangzz
 * 
 */
public class DstTriangular {

	public Point a;
	public Point b;
	public Point c;
	Point q = new Point();
	private Polygon polygon;
	protected int region_type;
	protected Point[] points;
	double acy;
	double acx;
	double bcx;
	double bcy;
	double cacxy;
	double u2diff;

	public int getRegionType() {
		return region_type;
	}

	public DstTriangular(Point a, Point b, Point c) {
		this.a = new Point(a.x, a.y);
		this.b = new Point(b.x, b.y);
		this.c = new Point(c.x, c.y);
		region_type = Region.REGION_TYPE_TRIANGULAR;
		points = new Point[3];
		points[0] = a;
		points[1] = b;
		points[2] = c;
		int[] xpoints = { (int) a.x, (int) b.x, (int) c.x };
		int[] ypoints = { (int) a.y, (int) b.y, (int) c.y };
		polygon = new Polygon(xpoints, ypoints, 3);
		acy = a.y - c.y;
		acx = a.x - c.x;
		bcx = b.x - c.x;
		bcy = b.y - c.y;
		cacxy = c.y * acx - c.x * acy;
		u2diff = bcx * acy - bcy * acx;
	}

	public Point[] getPoints() {

		return points;
	}

	public boolean isInRegion(Point point) {
		// TODO Auto-generated method stub

		return polygon.contains(point.x, point.y);
	}

	public Point transformation(Point p, Triangular DEF) {
		// double t = Core.getTickCount();
		double u1, u2, u3;

		u2 = (p.x * acy - p.y * acx + cacxy) / u2diff;
		u1 = (p.x - c.x - u2 * bcx) / acx;
		u3 = 1 - u1 - u2;
		q.x = u1 * DEF.a.x + u2 * DEF.b.x + u3 * DEF.c.x;
		q.y = u1 * DEF.a.y + u2 * DEF.b.y + u3 * DEF.c.y;
		// t = (Core.getTickCount() - t) / Core.getTickFrequency();
		// System.out.println("Times passed in seconds: " + t);
		return q;
	}

}
