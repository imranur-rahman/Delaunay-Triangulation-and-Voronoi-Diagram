public class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Point)) return false;
        Point o = (Point) obj;
        return (o.x == this.x  &&  o.y == this.y);
    }

    @Override
    public String toString(){
        return x + " " + y;
    }
}
