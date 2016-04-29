package com.example;

import java.text.DecimalFormat;

/**
 * Created by zzy on 2016/4/14.
 */
public class TestJaca {
    public static void main(String[] args) throws Exception {

        double zdf = 9.746;
        String zdfString = String.format("%.2f", zdf);
        System.out.println("zdf:"+zdfString);
        zdf = Double.parseDouble(zdfString);
        System.out.println(zdf);


        DecimalFormat df = new DecimalFormat("0.00");
        String zdfString1 = df.format(zdf);

        System.out.println(zdfString1);

    }
}
