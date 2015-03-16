package com.canace.facemorph;

import java.awt.Point;

/**
 * @author liangzz
 * 
 */
public abstract class Region {

	public final static int REGION_TYPE_TRIANGULAR = 0;
	public final static int REGION_TYPE_QUADRANGULAR = 1;
	protected int region_type;
	protected Point[] points;

	public Point[] getPoints() {

		return points;
	}

	public int getRegionType() {
		return region_type;
	}

	public abstract boolean isInRegion(java.awt.Point point);

	public abstract Point transformation(Point p, Region DEF);

}
