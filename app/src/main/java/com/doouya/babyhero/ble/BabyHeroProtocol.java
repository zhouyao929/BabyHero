package com.doouya.babyhero.ble;

import android.test.InstrumentationTestCase;

import com.doouya.babyhero.BabyHeroApplication;
import com.doouya.babyhero.utils.CRC16Utils;

import java.util.Calendar;

/**
 * Created by le on 2015/6/26.
 */
public class BabyHeroProtocol{

    //BLE设备的服务的UUID
    public final static String SERVICE_ACTIVITY_UUID = "000018aa";
    public final static String CHARACT_STATE_NOTIFY_UUID = "00000003";
    public final static String CHARACT_STATE_WRITE_UUID = "00000002";


    //根据L2生成L1
    public static byte[] createL1(byte[] L2){
        byte[] L1 = new byte[8];
        L1[0] = (byte)0xAB;
        L1[1] = 0; //00000000 其中高两位为保留位，3位表示传输正误（0正1误），4位表示应答标识位（0数据包，1应答包），后四位表示版本号
        L1[2] = (byte)((short) L2.length >> 8);
        L1[3] = (byte)((short) L2.length & 0x00ff);
        L1[4] = (byte)(CRC16Utils.getCRC1021(L2,L2.length) >> 8);
        L1[5] = (byte)(CRC16Utils.getCRC1021(L2,L2.length) & 0x00ff);
        L1[6] = (byte) (BabyHeroApplication.sequenceId >> 8);
        L1[7] = (byte) (BabyHeroApplication.sequenceId & 0x00ff);
        return L1;
    }

    //握手协议
    public static byte[] handShake(){
        byte[] L2 = {0x07, 0, 0x72, 0, 2, 0x40, (byte)0xA5};
        return L2;
    }

    //同步时间
    public static byte[] syscTime(){
        byte mYear,mMonth,mDay,mHour,mMinute,mSecond;
        String sYear,sMonth,sDay,sHour,sMinute,sSecond;
        final Calendar c = Calendar.getInstance();
        mYear = (byte) (c.get(Calendar.YEAR)%100); //获取当前年份
        mMonth = (byte) (c.get(Calendar.MONTH)+1);//获取当前月份
        mDay = (byte) c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
        mHour = (byte) c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        mMinute = (byte) c.get(Calendar.MINUTE);//获取当前的分钟数
        mSecond = (byte) (c.get(Calendar.SECOND));//获取当前秒数

        sYear = Integer.toBinaryString(mYear);
        sMonth = Integer.toBinaryString(mMonth);
        sDay = Integer.toBinaryString(mDay);
        sHour = Integer.toBinaryString(mHour);
        sMinute = Integer.toBinaryString(mMinute);
        sSecond = Integer.toBinaryString(mSecond);

        String s = getBitvalue(sYear,6)+getBitvalue(sMonth, 4)+getBitvalue(sDay, 5)+getBitvalue(sHour, 5)+getBitvalue(sMinute, 6)+getBitvalue(sSecond, 6);
        String s1 = s.substring(0, 8);
        String s2 = s.substring(8,16);
        String s3 = s.substring(16,24);
        String s4 = s.substring(24,32);
        byte[] time = new byte[4];
        time[0] = (byte) Short.parseShort(s1, 2);
        time[1] = (byte) Short.parseShort(s2, 2);
        time[2] = (byte) Short.parseShort(s3, 2);
        time[3] = (byte) Short.parseShort(s4, 2);
        byte[] value = {0x07,0,0x51,0,5,0x40,time[0],time[1],time[2],time[3]};
        return value;
    }

    //读取时间
    public static byte[] readCurrentTime(){
        byte[] L2 = {0x07,0,0x51,0,1,0x20};
        return L2;
    }

    //读取温度
    public static byte[] readTemp(){
        byte[] L2 = {0x07, 0, 0x31, 0, 1, (byte)0x80};
        return L2;
    }

    public static String getBitvalue(String str,int len){
        if(len == str.length())
            return str;
        else{
            int length = len-str.length();
            for(int i=0;i<length;++i){
                str = "0" + str;
            }

            return str;
        }
    }

    //将byte数组转为16进制
    public static String bytes2Hex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes)
        {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }

        return sb.toString();

    }

    //L1和L2组包
    public static byte[] mergeL1L2(byte[] L1,byte[] L2){

        byte []L = new byte[L1.length+L2.length];
        System.arraycopy(L1,0,L,0,L1.length);
        System.arraycopy(L2, 0, L, L1.length, L2.length);
        return L;
    }

}
