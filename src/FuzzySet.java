
class FuzzySet {
    private double min;
    private double max;
    private double mid;
    private double midValue;
    public FuzzySet(double min, double max, double midValue) {
        this(min, max, min + ((max - min) / 2.0), midValue);
    }

    FuzzySet(double min, double max, double mid, double midValue) {
        this.min = min;
        this.max = max;
        this.mid = mid;
        this.midValue = midValue;

    }

    double evaluate(double in) {

        if (in <= min || in >= max) {
            return 0;
        } else if (in == mid) {
            return midValue;
        } else if (in < mid) {
            double step = midValue / (mid - min);
            double d = in - min;
            return d * step;
        } else if (in > mid) {
            double step = midValue / (max - mid);
            double d = max - in;
            return d * step;
        } else return 0;
    }

    boolean isMember(double in) {
        return evaluate(in) != 0;
    }

    double getMin() {
        return min;
    }

    double getMax() {
        return max;
    }

    double getMid() {
        return mid;
    }

    double getMidValue() {
        return midValue;
    }

    double trumpFunc(double x, double in){
        if (x<=min){
            return 0;
        } else if((x>=min)&& (x<=FvalueL(in))){
            return in*(x-min)/(FvalueL(in)-min);
        } else if((x>=FvalueL(in))&&(x<=FvalueR(in))){
            return in;
        } else if((x>=FvalueR(in))&&(x<=max)){
            return in*(max-x)/(max-FvalueR(in));
        } else return 0;
    }

    private double FvalueR(double in){
        return (max-(in*(max-mid))/midValue);
    }

    private double FvalueL(double in){
        return ((in*(mid-min))/midValue) +min;
    }
}
