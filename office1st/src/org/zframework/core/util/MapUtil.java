package org.zframework.core.util;


import java.math.BigDecimal;

import java.math.RoundingMode;


public class MapUtil
{
	public static double[] range(double lon, double lat, double length) {
		double range = 57.295779513082323D * length / 6372.7969999999996D;
		double ingR = range / Math.cos(lat * 3.141592653589793D / 180.0D);
		
		double maxLat = lat + range;
		double minLat = lat - range;
		double maxLon = lon + ingR;
		double minLon = lon - ingR;
		
		double[] ranges = { maxLat, minLat, maxLon, minLon };
		
		for (int i = 0; i < ranges.length; i++) {
			double v = ranges[i];
			BigDecimal bd = new BigDecimal(v);
			bd = bd.setScale(6, RoundingMode.HALF_UP);
			ranges[i] = bd.doubleValue();
		}
		
		return ranges;
	}

	
	public static double distance(double long1, double lat1, double long2, double lat2){
		double R = 6378137.0D;
		lat1 = lat1 * 3.141592653589793D / 180.0D;
		lat2 = lat2 * 3.141592653589793D / 180.0D;
		double a = lat1 - lat2;
		double b = (long1 - long2) * 3.141592653589793D / 180.0D;
		
		double sa2 = Math.sin(a / 2.0D);
		double sb2 = Math.sin(b / 2.0D);
		double d = 2.0D * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		return d;
	}
	
}