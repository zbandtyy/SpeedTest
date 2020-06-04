package detectmotion.tuple;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/31 19:13
 * @description：
 * @modified By：
 * @version: $
 */
import java.util.Optional;

public  abstract class Tuple {
    //<A>：声明此方法为泛型方法，该方法持有一个类型A
    //这里使用Optional是为了提醒使用返回值的方法做非Null检查
    public abstract <A> Optional<A> _1();

    public abstract <B> Optional<B> _2();

    public abstract <C> Optional<C> _3();

    public static <A, B> Tuple of(A a, B b) {
        return new Tuple2(a, b);
    }

    public static <A, B, C> Tuple of(A a, B b, C c) {
        return new Tuple3(a, b, c);
    }
    //....

    public static void main(String[] args) {
        Tuple2<String, Integer> tuple2 = new Tuple2<>("hello", 1);
        Tuple2<String, Integer> tupleNull =new Tuple2<>();
        System.out.println(tuple2._1().orElse("notKnow")+" "+tuple2._2().orElse(0));
        Tuple3<Integer, String, Tuple2<String, Integer>> tuple3 = new Tuple3<>(1, null, tuple2);
        System.out.println(tuple3._1().orElse(0)
                +" "+tuple3._2().orElse("notKnow")
                +" "+tuple3._3().get()._1().get());
    }
}
