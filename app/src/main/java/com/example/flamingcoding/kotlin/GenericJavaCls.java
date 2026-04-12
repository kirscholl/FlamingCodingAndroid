package com.example.flamingcoding.kotlin;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GenericJavaCls {

    public void GenericAdapterSymbolTest(Context context) {
        TextView textView = new Button(context);
        List<Button> buttons1 = new ArrayList<Button>();

        // 多态用在这里会报错 incompatible types: List<Button> cannot be converted to List<TextView>
//        List<TextView> textViews = buttons;

        // Java 的泛型本身具有「不可变性 Invariance」，Java 里面认为 List<TextView> 和 List<Button> 类型并不一致，
        // 也就是说，子类的泛型（List<Button>）不属于泛型（List<TextView>）的子类
        // java 使用泛型通配符  ? extends 和 ? super来解决这个问题
        List<? extends TextView> textViews1 = buttons1;

        // List<? extends TextView> 由于类型未知，它可能是 List<Button>，也可能是 List<TextView>。
        // 对于前者，显然我们要添加 TextView 是不可以的。
        // 实际情况是编译器无法确定到底属于哪一种，无法继续执行下去，就报错了
        List<? extends TextView> textViews2 = new ArrayList<Button>();
        TextView textView2 = textViews2.get(0); // get 可以
        // 报错
//        textViews2.add(textView2);
        // 使用了 ? extends 泛型通配符的 List，只能够向外提供数据被消费，从这个角度来讲，向外提供数据的一方称为「生产者 Producer」。

        // ? super「下界通配符」，可以使 Java 泛型具有「逆变性 Contravariance」。
        List<? super Button> buttons2 = new ArrayList<TextView>();
        Object object = buttons2.get(0); // 👈 get 出来的是 Object 类型
        Button button = new Button(context);
        buttons2.add(button); // 👈 add 操作是可以的

        // 可以使用泛型通配符 ? extends 来使泛型支持协变，但是「只能读取不能修改」，这里的修改仅指对泛型集合添加元素，如果是 remove(int index) 以及 clear 当然是可以的。
        //可以使用泛型通配符 ? super 来使泛型支持逆变，但是「只能修改不能读取」，这里说的不能读取是指不能按照泛型类型读取，你如果按照 Object 读出来再强转当然也是可以的。
    }
}
