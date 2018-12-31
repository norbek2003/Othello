import java.util.*;
import java.io.*;
public class Coordinate implements Serializable{

    public final Integer x;
    public final Integer y;

    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate coordinate = (Coordinate) o;
        if (!x.equals(coordinate.x)) return false;
        return y.equals(coordinate.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
	@Override
	public String toString(){
		return "("+ x + ", " + y + ")";
		
	}
}