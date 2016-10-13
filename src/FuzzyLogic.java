import java.util.ArrayList;
import java.util.Iterator;

class FuzzyLogic {
    private ArrayList<FuzzySet> list = new ArrayList<FuzzySet>();

    void add(FuzzySet set) {
        list.add(set);
    }

    void clear() {
        list.clear();
    }

    double[] evaluate(double in) {
        double result[] = new double[list.size()];
        Iterator itr = list.iterator();
        int i = 0;
        while (itr.hasNext()) {
            FuzzySet set = (FuzzySet) itr.next();
            result[i++] = set.evaluate(in);
        }
        return result;
    }

    public int size() {
        return list.size();
    }

    public FuzzySet get(int i) {
        return list.get(i);
    }
}
