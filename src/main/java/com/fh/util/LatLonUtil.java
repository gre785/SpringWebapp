
package com.fh.util;

public class LatLonUtil
{
    private static final double PI = 3.14159265;
    private static final double EARTH_RADIUS = 6378137;
    private static final double RAD = Math.PI / 180.0;

    public static double[] getAround(double lat, double lon, int raidus)
    {
        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;
        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;
        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        System.out.println("jingdu" + minLat + "weidu" + minLng + "zuidajingdu" + maxLat + "zuidaweidu" + maxLng);
        return new double[] { minLat, minLng, maxLat, maxLng};
    }

    public static void main(String[] src)
    {
        getAround(36.68027, 117.12744, 1000);
    }

}
