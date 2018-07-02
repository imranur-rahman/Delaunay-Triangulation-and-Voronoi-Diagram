import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class main {

    static int n;
    static Point point[];
    static ArrayList<Triangle>triangles = new ArrayList<>();
    static ArrayList<Point>voronoiVertexes = new ArrayList<>();
    static ArrayList<Edge>voronoiDrawingEdges = new ArrayList<>();


    public static void main(String[] args) throws FileNotFoundException {
        takeInput();
        delaunayTriangulation();
        for(Triangle t: triangles){
            System.out.println(t);
        }
        voronoiDiagram();
    }

    private static void voronoiDiagram() {
        for(Triangle t: triangles){
            Point center = getCenter(t);
            voronoiVertexes.add(center);
            Point[] intersectionPoint = getIntersections(t, center);
            for(Point p: intersectionPoint){
                voronoiDrawingEdges.add(new Edge(center, p));
            }
        }
    }

    private static Point[] getIntersections(Triangle t, Point center) {
        Point[] points = new Point[3];
        points[0] = getIntersectionPoint(t.a, t.b, center);
        points[1] = getIntersectionPoint(t.b, t.c, center);
        points[2] = getIntersectionPoint(t.c, t.a, center);
        return points;
    }

    private static Point getIntersectionPoint(Point a, Point b, Point center) {
        // first convert line to normalized unit vector
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double mag = Math.sqrt(dx*dx + dy*dy);
        dx /= mag;
        dy /= mag;

// translate the point and get the dot product
        double lambda = (dx * (center.x - a.x)) + (dy * (center.y - a.y));
        double x = (dx * lambda) + a.x;
        double y = (dy * lambda) + a.y;
        return new Point(x, y);
    }

    private static Point getCenter(Triangle t) {
        double d = t.a.x * (t.b.y - t.c.y) + t.b.x * (t.c.y - t.a.y) + t.c.x * (t.a.y - t.b.y);
        d *= 2.0;

        double axySquare = t.a.x * t.a.x + t.a.y * t.a.y;
        double bxySquare = t.b.x * t.b.x + t.b.y * t.b.y;
        double cxySquare = t.c.x * t.c.x + t.c.y * t.c.y;

        double x = (axySquare * (t.b.y - t.c.y) + bxySquare * (t.c.y - t.a.y) + cxySquare * (t.a.y - t.b.y)) / d;
        double y = (axySquare * (t.c.x - t.b.x) + bxySquare * (t.a.x - t.c.x) + cxySquare * (t.b.x - t.a.x)) / d;

        return new Point(x, y);
    }

    private static void delaunayTriangulation() {
        double lowestX = findLowestX();
        double lowestY = findLowestY();
        double highestX = findHighestX();
        double highestY = findHighestY();

        /*Point P0 = new Point(Math.abs(lowestX) + Math.abs(highestX) + 50, Math.abs(lowestY) + Math.abs(highestY) + 50);
        Point P1 = new Point(Math.abs(lowestX) + Math.abs(highestX) + 50, -Math.abs(lowestY) - Math.abs(highestY) - 50);
        Point P2 = new Point(-Math.abs(lowestX) - Math.abs(highestX) - 50, -Math.abs(lowestY) - Math.abs(highestY) - 50);*/
        Point P0 = new Point(0, 1000);
        Point P1 = new Point(1000, -1000);
        Point P2 = new Point(-1000, -1000);

        Point[] ori = getCorrectOrientation(P0, P1, P2);
        Triangle temp = new Triangle(ori);
        //System.out.println(temp);
        triangles.add(temp);

        //Collections.shuffle(point);
        shuffleArray(point);

        for(int r = 0; r < n; ++r){
            Triangle t = findTriangle(point[r]);
            if(t == null){
                System.out.println("bishal error");
                System.out.println(point[r]);
                break;
            }
            if(PointInASideOfTriangle(point[r], t)){
                //on the edge

                int whichSide = getSide(point[r], t);
                Point pi, pj, pk;
                if(whichSide == 0){
                    pi = t.a;
                    pj = t.b;
                    pk = t.c;
                }
                else if(whichSide == 1){
                    pi = t.b;
                    pj = t.c;
                    pk = t.a;
                }
                else if(whichSide == 2){
                    pi = t.c;
                    pj = t.a;
                    pk = t.b;
                }
                else{
                    System.out.println("error getting side");
                    return;
                }

                Triangle oppositeT;
                oppositeT = t.getOppositeTriangle(pk);


                if(oppositeT == null){
                    makeLegalize(point[r], pj, pk, pi, t);
                    makeLegalize(point[r], pk, pi, pj, t);

                    removeTriangle(t);
                }
                else{

                    Point pl = oppositeT.getPL(t);

                    makeLegalize(point[r], pi, pl, pj, oppositeT);
                    makeLegalize(point[r], pl, pj, pi, oppositeT);
                    makeLegalize(point[r], pj, pk, pi, t);
                    makeLegalize(point[r], pk, pi, pj, t);

                    removeTriangle(t);
                    removeTriangle(oppositeT);
                }
            }
            else{
                //inside

                makeLegalize(point[r], t.a, t.b, t.c, t);
                makeLegalize(point[r], t.b, t.c, t.a, t);
                makeLegalize(point[r], t.c, t.a, t.b, t);

                /*Triangle now, oppositeT;

                now = addTriangle(point[r], t.a, t.b);
                oppositeT = t.getOppositeTriangle(t.c);
                now.setAdjacentTriangle(oppositeT);
                LegalizeEdge(point[r], t.a, t.b, now);

                now = addTriangle(point[r], t.b, t.c);
                oppositeT = t.getOppositeTriangle(t.a);
                now.setAdjacentTriangle(oppositeT);
                LegalizeEdge(point[r], t.b, t.c, now);

                now = addTriangle(point[r], t.c, t.a);
                oppositeT = t.getOppositeTriangle(t.b);
                now.setAdjacentTriangle(oppositeT);
                LegalizeEdge(point[r], t.c, t.a, now);*/

                removeTriangle(t);
            }
        }

        List<Triangle> toRemove = new ArrayList<Triangle>();
        for(Triangle t: triangles){
            if(t.contains(P0) || t.contains(P1)  ||  t.contains(P2))
                toRemove.add(t);
        }
        triangles.removeAll(toRemove);
    }

    //a, b, c - eder niye triangle add korbo
    //triangle t te b, c, rest era chilo
    private static void makeLegalize(Point a, Point b, Point c, Point rest, Triangle t) {
        Triangle now, oppositeT;

        now = addTriangle(a, b, c);
        oppositeT = t.getOppositeTriangle(rest);
        now.setAdjacentTriangle(oppositeT);
        LegalizeEdge(a, b, c, now);
    }

    private static int getSide(Point point, Triangle t) {
        if(ccw(point, t.a, t.b) == 0.0)
            return 0;
        if(ccw(point, t.b, t.c) == 0.0)
            return 1;
        if(ccw(point, t.c, t.a) == 0.0)
            return 2;
        return -1;
    }

    private static Triangle findTriangle(Point point) {
        for(Triangle t: triangles){
            if(PointInsideTriangle(point, t)){
                return t;
            }
        }
        return null;
    }

    private static void LegalizeEdge(Point z, Point x, Point y, Triangle t) {

        //z ke insert kore ei triangle hoiche

        Triangle adjacentTriangle;
        Point rest;
        if((x == t.a  &&  y == t.b)  ||  (x == t.b  &&  y == t.a)){
            adjacentTriangle = t.oppositeC;
            rest = t.c;
        }
        else if((x == t.b  &&  y == t.c)  ||  (x == t.c  &&  y == t.b)){
            adjacentTriangle = t.oppositeA;
            rest = t.a;
        }
        else{
            adjacentTriangle = t.oppositeB;
            rest = t.b;
        }

        //nothing to legalize
        if(adjacentTriangle == null)
            return;

        if(IsIllegal(z, x, y, rest)){
            removeTriangle(x, y, rest);
            Triangle n1 = addTriangle(z, x, rest);
            Triangle n2 = addTriangle(z, y, rest);
            n1.setAdjacentTriangle(n2);
            n2.setAdjacentTriangle(n1);

            LegalizeEdge(z, x, rest, n1);
            LegalizeEdge(z, y, rest, n2);
        }
    }

    private static Triangle addTriangle(Point a, Point b, Point c) {
        Triangle t = new Triangle(getCorrectOrientation(a, b, c));
        triangles.add(t);
        return t;
    }

    private static void removeTriangle(Point x, Point y, Point rest) {
        Triangle toRemove = new Triangle(getCorrectOrientation(x, y, rest));
        /*for(Triangle t: triangles){
            if(t == toRemove)
                triangles.remove(t);
        }*/
        triangles.remove(toRemove);
    }

    private static void removeTriangle(Triangle t) {
        triangles.remove(t);
    }

    private static boolean IsIllegal(Point a, Point b, Point c, Point rest) {
        Point[] t = getCorrectOrientation(a, b, c);//counter clock wise
        double determinant = getDeterminant(t, rest);
        if(determinant > 0.0){
            //rest is inside the circle, so illegal edge 0->2
            return true;
        }
        return false;
    }

    private static double getDeterminant(Point[] t, Point d) {
        double[][] mat = new double[3][3];
        for(int row = 0; row < 3; ++row){
            mat[row][0] = t[row].x - d.x;
            mat[row][1] = t[row].y - d.y;
            mat[row][2] = mat[row][0] * mat[row][0] + mat[row][1] * mat[row][1];
        }
        return Determinant(mat);
    }

    private static double Determinant(double[][] a) {
        double x=a[0][0]*((a[1][1]*a[2][2])-(a[2][1]*a[1][2]));
        double y=-a[0][1]*((a[0][1]*a[2][2])-(a[2][0]*a[1][2]));
        double z=a[0][2]*((a[1][0]*a[2][1])-(a[1][1]*a[2][0]));

        double r=x+y+z;
        return r;
    }

    private static Point[] getCorrectOrientation(Point a, Point b, Point c) {
        Point[] t = new Point[3];
        if(ccw(a, b, c) > 0.0){
            t[0] = a;
            t[1] = b;
            t[2] = c;
        }
        else if(ccw(a, c, b) > 0.0){
            t[0] = a;
            t[1] = c;
            t[2] = b;
        }
        else if(ccw(b, a, c) > 0.0){
            t[0] = b;
            t[1] = a;
            t[2] = c;
        }
        else if(ccw(b, c, a) > 0.0){
            t[0] = b;
            t[1] = c;
            t[2] = a;
        }
        else if(ccw(c, a, b) > 0.0){
            t[0] = c;
            t[1] = a;
            t[2] = b;
        }
        else{
            t[0] = c;
            t[1] = b;
            t[2] = a;
        }
        return t;
    }

    static double ccw (Point p1, Point p2, Point p3)
    {
        double val = (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
        return val;
    }

    static boolean PointInsideTriangle (Point pt, Triangle t)
    {
        boolean b1, b2, b3;

        b1 = ccw(pt, t.a, t.b) >= 0.0;
        b2 = ccw(pt, t.b, t.c) >= 0.0;
        b3 = ccw(pt, t.c, t.a) >= 0.0;

        return ((b1 == b2) && (b2 == b3));
    }

    static boolean PointInASideOfTriangle (Point pt, Triangle t)
    {
        boolean b1, b2, b3;

        b1 = (ccw(pt, t.a, t.b) == 0.0);
        b2 = (ccw(pt, t.b, t.c) == 0.0);
        b3 = (ccw(pt, t.c, t.a) == 0.0);

        return (b1 | b2 | b3);
    }

    // Implementing Fisher–Yates shuffle
    static void shuffleArray(Point[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Point a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    private static double findHighestY() {
        double ret = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < n; ++i)
            ret = Math.max(ret, point[i].y);
        return ret;
    }

    private static double findHighestX() {
        double ret = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < n; ++i)
            ret = Math.max(ret, point[i].x);
        return ret;
    }

    private static double findLowestY() {
        double ret = Double.POSITIVE_INFINITY;
        for(int i = 0; i < n; ++i)
            ret = Math.min(ret, point[i].y);
        return ret;
    }

    private static double findLowestX() {
        double ret = Double.POSITIVE_INFINITY;
        for(int i = 0; i < n; ++i)
            ret = Math.min(ret, point[i].x);
        return ret;
    }

    private static void takeInput() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/input.txt"));
        if(sc.hasNext())
            n = sc.nextInt();
        point = new Point[n];
        for(int i = 0; i < n; ++i){
            double x = 0.0, y = 0.0;
            if(sc.hasNext())
                x = sc.nextDouble();
            if(sc.hasNext())
                y = sc.nextDouble();
            point[i] = new Point(x, y);
        }
        sc.close();
        /*for(int i = 0; i < n; ++i)
            System.out.println(point[i].x + " " + point[i].y);*/
    }
}
