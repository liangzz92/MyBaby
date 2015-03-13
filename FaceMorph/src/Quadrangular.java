import java.awt.Polygon;

import org.opencv.core.Point;

/**
 * @author liangzz
 * 
 */
public class Quadrangular extends Region {

	public Point a;
	public Point b;
	public Point c;
	public Point d;

	public Quadrangular(Point a, Point b, Point c, Point d) {
		this.a = new Point(a.x, a.y);
		this.b = new Point(b.x, b.y);
		this.c = new Point(c.x, c.y);
		this.d = new Point(d.x, d.y);
		region_type = Region.REGION_TYPE_QUADRANGULAR;
		points = new Point[4];
		points[0] = this.a;
		points[1] = this.b;
		points[2] = this.c;
		points[3] = this.d;
	}

	public Point[] getPoints() {

		return points;
	}

	@Override
	public boolean isInRegion(Point point) {
		// TODO Auto-generated method stub
		int[] xpoints = { (int) a.x, (int) b.x, (int) c.x, (int) d.x };
		int[] ypoints = { (int) a.y, (int) b.y, (int) c.y, (int) d.y };
		Polygon polygon = new Polygon(xpoints, ypoints, 4);
		return polygon.contains(point.x, point.y);
	}

	public Point transformation(Point p, Region EFGH) {
		Point q = new Point(0, 0);
		double u = 0, v = 0;
		double ax, ay, bx, by, cx, cy, dx, dy;
		ax = 1 - p.x;
		ay = 1 - p.y;
		bx = a.x - 1;
		by = a.y - 1;
		cx = a.x - d.x;
		cy = a.y - d.y;
		dx = a.x - b.x + c.x - d.x;
		dy = a.y - b.y + c.y - d.y;
		double k = (ay * dx - ax * dy) / (by * dx - bx * dy);
		double w = (cy * dx - cx * dy) / (by * dx - bx * dy);
		double a = w * dx;
		double b = -(bx * w + cx - k * dx);
		double c = ax - bx * k;
		double delta = b * b - 4 * a * c;
		if (delta >= 0) {
			v = (-b + Math.sqrt(delta)) / 2 / a;
			u = k + w * v;
			if (!(v >= 0 && v <= 1 && u >= 0 && u <= 1)) {
				v = (-b - Math.sqrt(delta)) / 2 / a;
				u = k + w * v;
			}
			if (!(v >= 0 && v <= 1 && u >= 0 && u <= 1)) {
				u = v = 0;
			}
		} else {
			u = 0;
			v = 0;
		}
		q.x = (1 - u) * (1 - v) * points[0].x + u * (1 - v) * points[1].x + u
				* v * points[2].x + (1 - u) * v * points[3].x;
		q.y = (1 - u) * (1 - v) * points[0].y + u * (1 - v) * points[1].y + u
				* v * points[2].y + (1 - u) * v * points[3].y;
		return q;
	}
}
