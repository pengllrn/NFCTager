package com.example.pengl.nfctager.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengl.nfctager.R;
import com.example.pengl.nfctager.base.BaseNfcActivity;

import java.io.IOException;
import java.nio.charset.Charset;

public class ReadMUActivity extends BaseNfcActivity {

    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mu);
        textView = (TextView) findViewById(R.id.textView);
    }
    @Override
    public void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = readByHex(tag);
        if (data != null)
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        textView.setText(data);
    }
    public String readTag(Tag tag) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            byte[] data = ultralight.readPages(5);
            return new String(data, Charset.forName("GB2312"));
        } catch (Exception e) {
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
            }
        }
        return null;
    }


    public String readTagUltralight(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            int size = mifare.PAGE_SIZE;
            byte[] payload = mifare.readPages(0);
            String result = "page1：" + new String(payload) + "\n" + "总容量：" + String.valueOf(size) + "\n";

            //这里只读取了其中几个page、
            byte[] payload1 = mifare.readPages(4);
            byte[] payload2 = mifare.readPages(8);
            byte[] payload3 = mifare.readPages(12);
            result += "page4:" + new String(payload1) + "\npage8:" + new String(payload2) + "\npage12：" + new String(payload3) + "\n";

            //byte[] payload4 = mifare.readPages(16);
            //byte[] payload5 = mifare.readPages(20);
            return result;
            //+ new String(payload4, Charset.forName("US-ASCII"));
            //+ new String(payload5, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e("TAG", "IOException while writing MifareUltralight message...",
                    e);
            return "读取失败！";
        } catch (Exception ee) {
            Log.e("TAG", "IOException while writing MifareUltralight message...",
                    ee);
            return "读取失败！";
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
                    Log.e("TAG", "Error closing tag...", e);
                }
            }
        }
    }

    public String readByHex(Tag tag){
        MifareUltralight mifare = MifareUltralight.get(tag);
        String result="";
        String temp="";
        try {
            mifare.connect();
            int size = mifare.PAGE_SIZE;
            for(int i =0;i<44;i+=4){
                byte[] payload = mifare.readPages(i);
                for(int j =0;j<payload.length;j++) {
                    if (j % 4 == 0) {
                        result += "\n" + "page" + (i+j/4) + ":";
                    }
                    temp += Integer.toHexString(payload[j] & 0xFF);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    result += temp;
                    temp="";
                }
            }
            return result;
        } catch (IOException e) {
            Log.e("TAG", "IOException while writing MifareUltralight message...",
                    e);
            return "读取失败！";
        } catch (Exception ee) {
            Log.e("TAG", "IOException while writing MifareUltralight message...",
                    ee);
            return "读取失败！";
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
                    Log.e("TAG", "Error closing tag...", e);
                }
            }
        }
    }
}