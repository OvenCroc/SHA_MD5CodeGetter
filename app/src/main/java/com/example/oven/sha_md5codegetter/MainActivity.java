package com.example.oven.sha_md5codegetter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button generate ,copy_SHA,copy_MD5;
    PackageManager pm ;
    EditText editText;
    TextView SHA,MD5;
    PackageInfo pi;
    ClipboardManager cm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pm = getPackageManager();
        generate = (Button) findViewById(R.id.generate);
        editText = (EditText) findViewById(R.id.input);
        copy_SHA = (Button) findViewById(R.id.copy_SHA);
        copy_MD5 = (Button) findViewById(R.id.copy_MD5);
        SHA = (TextView) findViewById(R.id.SHA);
        MD5 = (TextView) findViewById(R.id.MD5);
        cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        generate.setOnClickListener(this);
        copy_SHA.setOnClickListener(this);
        copy_MD5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String SHA_str = "";
        String content = editText.getText().toString();
        if("".equals(content)){
            content = editText.getHint().toString();
        }

        SHA_str = getSHA(content);
        switch (v.getId()){
            case R.id.generate:
                if(!"".equals(SHA)){
                    this.SHA.setText(SHA_str.split(";")[0]);
                    MD5.setText(SHA_str.split(";")[1]);
                }
                break;
            case R.id.copy_SHA:
                cm.setText(SHA_str.split(";")[0]);
                Toast.makeText(MainActivity.this, "sha复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_MD5:
                cm.setText(SHA_str.split(";")[1]);
                Toast.makeText(MainActivity.this, "md5复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 生成sha的方法
     * @author zhoup
     * Created at 2016/6/12 22:39
     */
    public String getSHA(String packageName){
        String hexString = null;
        try {
            pi = pm.getPackageInfo(packageName,PackageManager.GET_SIGNATURES);
            Signature[] signature = pi.signatures;
            byte[] cert = signature[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X509");
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            X509Certificate c1 = null;
            try {
                c1 = (X509Certificate) cf.generateCertificate(input);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            try {
                //加密算法的类，这里的参数可以使MD4,MD5等加密算法
                MessageDigest md = MessageDigest.getInstance("SHA1");
                MessageDigest md2 = MessageDigest.getInstance("MD5");
                //获得公钥
                byte[] publicKey = md.digest(c1.getEncoded()),
                        publicKeyMd5 = md2.digest(c1.getEncoded());

                //字节到十六进制的格式转换
                hexString = byte2HexFormatted(publicKey)+";"+byte2HexFormatted(publicKeyMd5);
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    /**
     * 复制到粘贴板
     * @author zhoup
     * Created at 2016/6/12 23:24
     */
    public void Copy(){
        Toast.makeText(MainActivity.this, "复制到粘贴板", Toast.LENGTH_SHORT).show();
    }
}
