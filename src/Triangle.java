public class Triangle {
    Point a, b, c;
    Triangle oppositeA, oppositeB, oppositeC;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triangle(Point[] t){
        a = t[0];
        b = t[1];
        c = t[2];
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Triangle)) return false;
        Triangle o = (Triangle) obj;
        return (o.a == this.a  &&  o.b == this.b  &&  o.c == this.c);
    }

    public void setAdjacentTriangle(Triangle n) {
        if(n == null)
            return;
        if(a != n.a  &&  a != n.b  &&  a != n.c)
            oppositeA = n;
        else if(b != n.a  &&  b != n.b  &&  b != n.c)
            oppositeB = n;
        else if(c != n.a  &&  c != n.b  &&  c != n.c)
            oppositeC = n;
        else
            System.out.println("some error in triangle adjacency");
    }

    public Triangle getOppositeTriangle(Point point){
        if(point == a)
            return oppositeA;
        else if(point == b)
            return oppositeB;
        else if(point == c)
            return oppositeC;
        else{
            System.out.println("error in getting opposite triangle");
            return null;
        }
    }

    public Point getPL(Triangle t){
        if(a != t.a  ||  a != t.b  || a != t.c)
            return a;
        else if(b != t.a  ||  b != t.b  || b != t.c)
            return b;
        else if(c != t.a  ||  c != t.b  || c != t.c)
            return c;
        else{
            System.out.println("very much problem");
            return null;
        }
    }

    @Override
    public String toString(){
        return a.toString() + "\n" + b.toString() + "\n" + c.toString() + "\n\n";
    }

    public boolean contains(Point point) {
        if(point == a  ||  point == b  ||  point == c)
            return true;
        return false;
    }
}
