package com.example.hbl.bluetooth;

import org.junit.Test;

import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        int[] number = new int[]{890, 760, 660, 570, 1100, 990, 1200, 560, 740, 1020, 880, 580, 770, 450, 1350};
        int[] count = new int[]{10, 9, 8, 9, 11, 13, 17, 9, 6, 10, 14, 4, 2, 6, 7};
        sort(number);
        int[] sortcount=sortCount(scor,number,count);
        System.out.println(Arrays.toString(scor));
        System.out.println(Arrays.toString(sortcount));

    }

    private int[] sortCount(int[] newnumber, int[] oldnumber,int[] count) {
        int[] newConut=new int[count.length];
        for(int i=0;i<newnumber.length-1;i++){
            for(int j=0;j<oldnumber.length-1;j++){
                if(newnumber[i]==oldnumber[j]){
                    newConut[i]=count[j];
                    break;
                }
            }
        }
        return newConut;
    }

    private void sort(int[] args) {
        int[] p = new int[args.length];
        mergesort(args, 0, args.length - 1, p);
        return ;
    }
    private int [] scor;
    private void mergesort(int[] args, int first, int last, int[] p) {
        if (first < last) {
            int mid = (first + last) / 2;
            mergesort(args, first, mid, p);
            mergesort(args, mid + 1, last, p);
            mergeArray(args, first, mid, last, p);
        }
    }

    private int[] mergeArray(int[] a, int first, int mid, int last, int[] p) {
        int i = first, j = mid + 1, m = mid, n = last, k = 0;
        while (i <= m && j <= n) {
            if(a[i]<=a[j]){
                p[k++]=a[i++];
            }else {
                p[k++]=a[j++];
            }
        }
        while (i<=m){
            p[k++]=a[i++];
        }
        while (j<=n){
            p[k++]=a[j++];
        }
        return p;
    }

}