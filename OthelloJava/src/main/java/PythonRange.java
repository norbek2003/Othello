import java.util.*;
public class PythonRange implements Iterable<Integer> {
    private ArrayList<Integer> list = new ArrayList<Integer>();
    public PythonRange(int end) {
        this(0, end);
    }

    public PythonRange(int start, int end) {
        this(start, end, 1);
    }

    public PythonRange(int start, int end, int step) {

        if (start < end && step > 0) {
            for (int i = start; i < end; i += step) {
                list.add(i);
            }
        } else if (start > end && step < 0) {
            for (int i = start; i > end; i += step) {
                list.add(i);
            }
        }
    }
    // constructor
    @Override
    public Iterator<Integer> iterator() {
        return list.iterator();
    }



}