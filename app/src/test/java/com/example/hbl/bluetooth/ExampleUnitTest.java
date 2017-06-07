package com.example.hbl.bluetooth;

import org.junit.Test;

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