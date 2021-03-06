import java.util.*;
import java.util.stream.Collectors;

/**
 * Alexander Artamonov (asartamonov@gmail.com) 2016.
 */

public class FastCollinearPoints {
    private ArrayList<ArrayList<Point>> segments = new ArrayList<>();
    private final Point[] points;
    private final LineSegment[] toReturn;

    /**
     * Finds all line segments containing 4 or more points
     * Throw a java.lang.NullPointerException either the argument
     * to the constructor is null or if any point in the array is null.
     * Throw a java.lang.IllegalArgumentException if the argument
     * to the constructor contains a repeated point.
     */
    public FastCollinearPoints(Point[] initPoints) {
        if (initPoints == null) throw new NullPointerException();
        points = Arrays.copyOf(initPoints, initPoints.length);
        Arrays.sort(points); //array points sorted by point's position
        Point[] sortedBySlope;
        for (int i = 0; i < points.length; i++) {
            Point first = points[i];
            if (first == null)
                throw new NullPointerException();
            sortedBySlope = Arrays.copyOfRange(points, i + 1, points.length);
            Arrays.sort(sortedBySlope, first.slopeOrder()); //array points sorted by slope to first point
            Map<Double, Integer> slopeStats = new HashMap<>();
            ArrayList<Double> slopes = new ArrayList<>();
            for (Point aSortedBySlope : sortedBySlope) slopes.add(first.slopeTo(aSortedBySlope));
            for (int l = 0; l < sortedBySlope.length; l++) {
                if (first.compareTo(sortedBySlope[l]) == 0)
                    throw new IllegalArgumentException();
                if (slopeStats.get(slopes.get(l)) != null)
                    slopeStats.put(slopes.get(l),
                            slopeStats.get(slopes.get(l)) + 1);
                else slopeStats.put(slopes.get(l), 1);
            }
            Map<Double, Integer> slopesOfCollinear =
                    slopeStats.entrySet()
                            .stream()
                            .filter(p -> p.getValue() >= 3)
                            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
            for (Double slope : slopesOfCollinear.keySet()) {
                ArrayList<Point> segment = new ArrayList<>();
                segment.add(first);
                segment.addAll(Arrays.asList(sortedBySlope)
                        .subList(slopes.indexOf(slope), slopes.lastIndexOf(slope) + 1));
                if (!checkIsIncluded(segments, segment))
                    segments.add(segment);
            }
        }
        toReturn = new LineSegment[segments.size()];
        for (int i = 0; i < toReturn.length; i++)
            toReturn[i] = new LineSegment(segments.get(i).get(0), segments.get(i).get(segments.get(i).size() - 1));
    }

    /***
     * Checks whether newSegment is a part of already added segment
     * as long as our array is sorted, no longer segment are possible
     * through the search, only shorter parts of already included segments;
     */
    private static boolean checkIsIncluded(ArrayList<ArrayList<Point>> segmentList, ArrayList<Point> newSegment) {
        boolean isAlreadyAdded = false;
        if (segmentList.size() > 0) {
            ListIterator<ArrayList<Point>> segmentsIterator = segmentList.listIterator(segmentList.size());
            while (segmentsIterator.hasPrevious()) {
                ArrayList<Point> segment = segmentsIterator.previous();
                if (segment.get(segment.size() - 1) == newSegment.get(newSegment.size() - 1)
                        && segment.get(segment.size() - 1).slopeTo(segment.get(0))
                        == newSegment.get(newSegment.size() - 1).slopeTo(newSegment.get(0))) {
                    isAlreadyAdded = true;
                    break;
                }
            }
        }
        return isAlreadyAdded;
    }

    /**
     * Returns the number of line segments
     */
    public int numberOfSegments() {
        return toReturn.length;
    }

    /**
     * Returns the line segments as an array
     */
    public LineSegment[] segments() {
        return Arrays.copyOf(toReturn, toReturn.length);
    }
}

