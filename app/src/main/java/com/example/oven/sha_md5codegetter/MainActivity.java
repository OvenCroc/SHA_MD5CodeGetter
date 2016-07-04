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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    Button generate ,copy_SHA,copy_MD5;
    PackageManager pm ;
    EditText editText;
    TextView SHA,MD5;
    PackageInfo pi;
    CheckBox upper,maohao;
    ClipboardManager cm;
    static boolean isUpper = true,isMaohao=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pm = getPackageManager();
        generate = (Button) findViewById(R.id.generate);
        editText = (EditText) findViewById(R.id.input);
        copy_SHA = (Button) findViewById(R.id.copy_SHA);
        copy_MD5 = (Button) findViewById(R.id.copy_MD5);
        upper = (CheckBox) findViewById(R.id.upper);
        maohao = (CheckBox) findViewById(R.id.maohao);
        maohao.setChecked(isMaohao);
        upper.setChecked(isUpper);
        upper.setOnCheckedChangeListener(this);
        maohao.setOnCheckedChangeListener(this);

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

        SHA_str = getSHA_MD5(content);
        switch (v.getId()){
            case R.id.generate:
                if(!"".equals(SHA)){
                    this.SHA.setText(SHA_str.split(";")[0]);
                    MD5.setText(SHA_str.split(";")[1]);
                }
                break;
            case R.id.copy_SHA:
                cm.setText(SHA.getText());
                Toast.makeText(MainActivity.this, "sha复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_MD5:
                cm.setText(MD5.getText());
                Toast.makeText(MainActivity.this, "md5复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 生成sha的方法
     * @author zhoup
     * Created at 2016/6/12 22:39
     */
    public String getSHA_MD5(String packageName){
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
            str.append(isUpper?h.toUpperCase():h.toLowerCase());
            if (i < (arr.length - 1) && isMaohao)
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String code[] = getMD5_SHAValue();
        switch (buttonView.getId()){
            case R.id.maohao:
                isMaohao = isChecked;
                if (!isChecked) {
                    code[0]=deleteMaohao(code[0]);
                    code[1]=deleteMaohao(code[1]);
                }else{
                    code[0]=appendMaohao(code[0]);
                    code[1]=appendMaohao(code[1]);
                }
                break;
            case R.id.upper:
                isUpper = isChecked;
                code[0]=charExchange(code[0],isChecked);
                code[1]=charExchange(code[1],isChecked);
                break;
        }
        setMD5_SHA(code);
    }

    /**
     * 去掉冒号的方法
     * @author zhoup
     * Created at 2016/7/4 14:55
     */
    public String deleteMaohao(String code){
        return code.replace(":","");
    }

    /**
     * 添加冒号
     * @author zhoup
     * Created at 2016/7/4 15:35
     */
    public String appendMaohao(String code){
        char [] arr = code.toCharArray();
        String a = "";
        for(int i=0;i<arr.length;i++){
            if(i%2==1){
                a+=arr[i]+":";
            }else {
                a+=arr[i];
            }
        }
        return a;
    }

    /**
     * 大小写转换的方法
     * @author zhoup
     * Created at 2016/7/4 14:57
     */
    public String charExchange(String code,boolean isUpper){
        if(!isUpper){
            code = code.toLowerCase();
        }else{
            code = code.toUpperCase();
        }
        return code;
    }

    /**
     * 获得两个code的值的方法
     * @author zhoup
     * Created at 2016/7/4 15:01
     */
    public String[] getMD5_SHAValue(){
        String [] code = new String[2];
        code[0]= String.valueOf(SHA.getText());
        code[1]= String.valueOf(MD5.getText());
        return code;
    }

    /**
     * 设置两个textview的文字内容
     * @author zhoup
     * Created at 2016/7/4 15:13
     */
    public void setMD5_SHA(String [] code){
        SHA.setText(code[0]);
        MD5.setText(code[1]);
    }
}
