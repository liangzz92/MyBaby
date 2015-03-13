import java.awt.Polygon;

import org.opencv.core.Point;

/**
 * @author liangzz
 * 
 */
public class Triangular extends Region {

	public Point a;
	public Point b;
	public Point c;
	private Polygon polygon;

	public Triangular(Point a, Point b, Point c) {
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
	}

	public Point[] getPoints() {

		return points;
	}

	@Override
	public boolean isInRegion(Point point) {
		// TODO Auto-generated method stub

		return polygon.contains(point.x, point.y);
	}

	public Point transformation(Point p, Region DEF) {
		Point q = new Point();
		double u1, u2;
		u2 = ((p.x - c.x) * (a.y - c.y) - (p.y - c.y) * (a.x - c.x))
				/ ((b.x - c.x) * (a.y - c.y) - (b.y - c.y) * (a.x - c.x));
		u1 = (p.x - c.x - u2 * (b.x - c.x)) / (a.x - c.x);
		q.x = u1 * ((Triangular) DEF).a.x + u2 * ((Triangular) DEF).b.x
				+ (1 - u1 - u2) * ((Triangular) DEF).c.x;
		q.y = u1 * ((Triangular) DEF).a.y + u2 * ((Triangular) DEF).b.y
				+ (1 - u1 - u2) * ((Triangular) DEF).c.y;
		return q;
	}

}
