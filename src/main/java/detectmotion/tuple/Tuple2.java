package detectmotion.tuple;

import java.util.Optional;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/31 19:14
 * @description：
 * @modified By：
 * @version: $
 */
public class Tuple2<A,B> extends Tuple{

    private A a;
    private B b;

    public Tuple2() {
    }

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Optional<A> _1() {
        return Optional.ofNullable(a);
    }

    @Override
    public Optional<B> _2() {
        return Optional.ofNullable(b);
    }

    @Override
    public <C> Optional<C> _3() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Tuple2{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
