package detectmotion.tuple;

import java.util.Optional;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/31 19:14
 * @description：
 * @modified By：
 * @version: $
 */
public class Tuple3<A,B,C> extends Tuple{

    private A a;
    private B b;
    private C c;

    public Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public Optional<A> _1() {
        return Optional.ofNullable(a);
    }

    @Override
    public  Optional<B> _2() {
        return Optional.ofNullable(b);
    }

    @Override
    public  Optional<C> _3() {
        return Optional.ofNullable(c);
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}