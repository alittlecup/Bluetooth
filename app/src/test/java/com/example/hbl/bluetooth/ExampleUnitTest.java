package com.example.hbl.bluetooth;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String s = UUID.randomUUID().toString().toUpperCase();
        System.out.println(s);
        int a=0xff;
        int b=0xff;
        int c=0xE0;
        int d=0x00;
        int e=0x00;
        int f=0x07;
        int g=0x2016;
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
        System.out.println(f);
        System.out.println(g);
        System.out.println(a+b+c+d+e+f+g);
    }
    public static void main(String[] args) {
        String s = "asad";
        s = null;
        try {
            int length = s.length();
            System.out.println(length);
        } catch (Exception e) {

            System.out.println("null");
        }
    }
}