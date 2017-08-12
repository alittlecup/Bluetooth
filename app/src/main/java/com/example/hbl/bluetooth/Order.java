package com.example.hbl.bluetooth;

/**
 * Created by hbl on 2017/8/2.
 */

public interface Order {
    String READ_MAC1 = "AA000011";
    String READ_MAC2 = "AA010011";
    String READ_MAC3 = "AA020011";
    String READ_MAC4 = "AA030011";
    String READ_MAC5 = "AA040011";
    String READ_MAC6 = "AA050011";
    String READ_ENERGY = "AA060001";
    String WRITE_LIGHT = "AB070001";
    String READ_HEAT = "AA240001";
    String WRITE_HEAT = "AB240001";
    String WRITE_OPEN = "AB27000101";
    String WRITE_CLOSE = "AB27000100";
    String WRITE_TIME = "AB200012";
    String READ_TIME = "AA200012";



//    if(Attribute_Value[0]==0xAA)
//
//    {
//        if ((Attribute_Value[1] == 0x00) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[5];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x01) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[4];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x02) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[3];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x03) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[2];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x04) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[1];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x05) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//            GATT_Upload[1] = ZQ_BLE_Address_Buffer[0];
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x06) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//
//            if (Battery_Voltage < 6000) GATT_Upload[1] = 0;
//            else if (Battery_Voltage < 6600) GATT_Upload[1] = 1;
//            else if (Battery_Voltage < 6900) GATT_Upload[1] = 2;
//            else if (Battery_Voltage < 7100) GATT_Upload[1] = 3;
//            else if (Battery_Voltage < 7300) GATT_Upload[1] = 4;
//            else if (Battery_Voltage < 7500) GATT_Upload[1] = 5;
//            else if (Battery_Voltage < 7700) GATT_Upload[1] = 6;
//            else if (Battery_Voltage < 7900) GATT_Upload[1] = 7;
//            else if (Battery_Voltage < 8100) GATT_Upload[1] = 8;
//            else GATT_Upload[1] = 9;
//
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        } else if ((Attribute_Value[1] == 0x0c) && (Attribute_Value[2] == 0x00)) {
//            GATT_Upload[0] = 0xAA;
//
//            GATT_Upload[1] = 0x20;
//            GATT_Upload[2] = 0x17;
//            GATT_Upload[3] = 0x07;
//            GATT_Upload[4] = 0x18;
//
//            blt_push_notify_data(30, & GATT_Upload[0], 5);
//        } else if (0x20 == Attribute_Value[1]) {
//            GATT_Upload[0] = 0xAA;
//
//            GATT_Upload[1] = (WGY_Dec_Heat_timer >> 8);
//            GATT_Upload[2] = WGY_Dec_Heat_timer;
//            blt_push_notify_data(30, & GATT_Upload[0], 3);
//        } else {
//            GATT_Upload[0] = 0xAC;
//            Attribute_Value[1] = 0x01;
//
//            blt_push_notify_data(30, & GATT_Upload[0], 1);
//        }
//    }
//
//
//    else if((Attribute_Value[1]==0x07)&&(ZQ_POWER_ON ==ZQ_Power_Mode_Now)){
//        GATT_Upload[0] = 0xAB;
//
//        ZQ_UI_Update(ZQ_UI_MODE_LED3_NORMAL, 0xFF);//Clear
//
//        if (Attribute_Value[4] == 1) ZQ_UI_Update(ZQ_UI_MODE_LED3_1Hz, 0xFF);//Green
//        else if (Attribute_Value[4] == 2) ZQ_UI_Update(ZQ_UI_MODE_LED3_2Hz, 0xFF);//Green
//        else if (Attribute_Value[4] == 3) ZQ_UI_Update(ZQ_UI_MODE_LED3_3Hz, 0xFF);//Green
//
//        else ZQ_UI_Update(ZQ_UI_MODE_LED3_100mS, 0xFF);//Green
//
//        blt_push_notify_data(30, & GATT_Upload[0], 1);
//    } else if(0x20==Attribute_Value[1])
//
//    {
//        GATT_Upload[0] = 0xAB;
//        WGY_Set_Heat_timer = 0;
//        WGY_Set_Heat_timer = Attribute_Value[4];
//        WGY_Set_Heat_timer <<= 8;
//        WGY_Set_Heat_timer += Attribute_Value[5];
//        WGY_Dec_Heat_timer = WGY_Set_Heat_timer;
//        blt_push_notify_data(30, & GATT_Upload[0], 1);
//    } else if((Attribute_Value[1]==0x25)&&(ZQ_POWER_ON ==ZQ_Power_Mode_Now))
//
//    {
//        GATT_Upload[0] = 0xAB;
//
//        ZQ_UI_Update(ZQ_UI_MODE_LED1_NORMAL, 0xFF);//Clear
//        ZQ_UI_Update(ZQ_UI_MODE_LED2_NORMAL, 0xFF);//Clear
//        ZQ_UI_Update(ZQ_UI_MODE_LED3_NORMAL, 0xFF);//Clear
//
//        if (90 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 49999, 50000);//pwm_DutyCycleSet(PWM1,999,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED3_100mS, 0xFF);
//        } else if (80 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 47500, 50000);//pwm_DutyCycleSet(PWM1,950,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED3_100mS, 0xFF);
//        } else if (70 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 45000, 50000);//pwm_DutyCycleSet(PWM1,900,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED3_100mS, 0xFF);
//        } else if (60 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 42500, 50000);//pwm_DutyCycleSet(PWM1,850,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED3_100mS, 0xFF);
//        } else if (50 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 40000, 50000);//pwm_DutyCycleSet(PWM1,800,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED1_100mS, 0xFF);
//        } else if (40 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 37500, 50000);//pwm_DutyCycleSet(PWM1,750,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED1_100mS, 0xFF);
//        } else if (30 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 35000, 50000);//pwm_DutyCycleSet(PWM1,700,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED1_100mS, 0xFF);
//        } else if (20 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 30000, 50000);//pwm_DutyCycleSet(PWM1,600,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED2_100mS, 0xFF);
//        } else if (10 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 25000, 50000);//pwm_DutyCycleSet(PWM1,500,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED2_100mS, 0xFF);
//        } else if (0 <= Attribute_Value[4]) {
//            pwm_DutyCycleSet(PWM1, 20000, 50000);//pwm_DutyCycleSet(PWM1,400,1000);
//            ZQ_UI_Update(ZQ_UI_MODE_LED2_100mS, 0xFF);
//        } else {
//
//        }
//
//        blt_push_notify_data(30, & GATT_Upload[0], 1);
//    } else if(0x27==Attribute_Value[1])
//
//    {
//        GATT_Upload[0] = 0xAb;
//        if (0 == Attribute_Value[4]) {
//            ZQ_Power_Mode_Will = ZQ_POWER_OFF;
//
//            blt_push_notify_data(30, & GATT_Upload[0], 1);
//        } else if (1 == Attribute_Value[4]) {
//            ZQ_Power_Mode_Will = ZQ_POWER_ON;
//            blt_push_notify_data(30, & GATT_Upload[0], 1);
//        } else {
//            GATT_Upload[0] = 0xac;
//            GATT_Upload[1] = 0x02;
//            blt_push_notify_data(30, & GATT_Upload[0], 2);
//        }
//
//    } else
//
//    {
//        GATT_Upload[0] = 0xAC;
//        Attribute_Value[1] = 0x01;
//        blt_push_notify_data(30, & GATT_Upload[0], 2);
//    }
}