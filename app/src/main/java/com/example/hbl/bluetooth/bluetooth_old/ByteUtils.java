package com.example.hbl.bluetooth.bluetooth_old;

/**
 * @author zhaomeng
 */
public class ByteUtils {
    /**
     * 4位字节转换为int型
     *
     * @param b
     * @return
     */
    protected static int byte4ToInt(byte[] b) {
        int l = 0;
        l = b[0];
        l &= 0xff;
        l |= ((int) b[1] << 8);
        l &= 0xffff;
        l |= ((int) b[2] << 16);
        l &= 0xffffff;
        l |= ((int) b[3] << 24);
        l &= 0xffffffff;
        return l;
    }

    /**
     * 8字节转换为long型
     *
     * @param b
     * @return
     */
    public static long byte8ToLong(byte[] b) {
        long l = 0;
        l |= (((long) b[7] & 0xff) << 56);
        l |= (((long) b[6] & 0xff) << 48);
        l |= (((long) b[5] & 0xff) << 40);
        l |= (((long) b[4] & 0xff) << 32);
        l |= (((long) b[3] & 0xff) << 24);
        l |= (((long) b[2] & 0xff) << 16);
        l |= (((long) b[1] & 0xff) << 8);
        l |= ((long) b[0] & 0xff);
        return l;
    }

    /**
     * 2字节转换为int型
     *
     * @param res
     * @return
     */
    public static int byte2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
        return targets;
    }

    public static void main(String[] args) {
//        Queue<String> orderlist = new LinkedList<>();
//        orderlist.offer(Order.WRITE_OPEN);
//        orderlist.offer(Order.WRITE_LIGHT + "03");
//        orderlist.offer(Order.READ_MAC1);
//        orderlist.offer(Order.READ_MAC2);
//        orderlist.offer(Order.READ_MAC3);
//        orderlist.offer(Order.WRITE_LIGHT + "00");
//        orderlist.offer(Order.WRITE_CLOSE);
//        for (String q : orderlist) {
//            System.out.print(q + "->");
//        }
//        System.out.println("==");
//        String poll = orderlist.poll();
//        System.out.println(poll);
//        for (String q : orderlist) {
//            System.out.print(q + "->");
//        }
//        System.out.println("  ");
//        String asdf = orderlist.poll();
//        String df = orderlist.poll();
//        for (String q : orderlist) {
//            System.out.print(q + "->");
//        }
    }

}
