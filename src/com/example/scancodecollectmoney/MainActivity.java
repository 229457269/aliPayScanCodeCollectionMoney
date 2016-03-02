package com.example.scancodecollectmoney;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qr_codescan.MipcaActivityCapture;
import com.example.scancodecollectmoney.utils.MD5Util;
import com.example.scancodecollectmoney.utils.MapUtils;
import com.example.scancodecollectmoney.utils.SocketToNet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * ɨ��  �տ�demo����      ʹ��Socketʵ��ͨѶ   �Ƚϰ�ȫ
 * com.example.scancodecollectmoney
 * @Email zhaoq_hero@163.com
 * @author zhaoQiang : 2016-2-26
 */
public class MainActivity extends Activity {
	
	private TextView textView1;
	private TextView textView2;
	
	private Button btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textView1 = (TextView) findViewById(R.id.payCode);
		textView2 = (TextView)findViewById(R.id.result);
		
		btn = (Button) findViewById(R.id.btn);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		
	}
	
	private final static int SCANNIN_GREQUEST_CODE = 1;
	
	/**
	 * �������ݣ�
	 * {"service":"alipay_pay_2","terminal_no":"11110000",
	 * "timestamp":"20160222131228","subject":"�̵�ͨ��Ʒ",
	 * "dynamic_id":"287045028458078999","store_id":"001",
	 * "undiscountable_amount":"0.01","total_fee":"0.01",
	 * "out_trade_no":"20160222131158","oto_pid":"3088677747502334",
	 * "sign":"6f60bdc255e52a24807249524ff50c5d"
	 * }
	 */
   private String resultStr = "";
   
    private static final int THREAD_IS_OK = 2;
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        
		case SCANNIN_GREQUEST_CODE:
			
			if(resultCode == RESULT_OK){
				
				Bundle bundle = data.getExtras();
				//��ʾɨ�赽������
				String payId = bundle.getString("result");
				
				textView1.setText("����״̬:\n\r����id:" + payId +"��");
				textView2.setText("���Ե�,�������������...");
				
				//ƴ�����������data:
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp = format.format(new Date());    //ʱ��
				
				JSONObject testStr = new JSONObject();
				
				testStr.put("out_trade_no", "201603011288");  //�̻���ˮ��
				testStr.put("terminal_no", "00000002");  // �����ն˺�
				testStr.put("subject", "��ˮ");  //��������
				testStr.put("total_fee", "0.01");
				
				testStr.put("oto_pid",Content.SHOP_PID);
				testStr.put("timestamp", timestamp);
				testStr.put("dynamic_id", payId); //ɨ�赽��������
				
				/**
				 * ������  ת���ɼ�ֵ�Ե���ʽ      
				 */
				Map<String, String> map = MapUtils.getParamsFromJson(JSON.toJSONString(testStr));
				// Ȼ������ݰ�������ĸ˳��     ��������     
				String prestr = MapUtils.createLinkString(map); 
				// ��ȡ �������ַ�����ժҪ��Ϣ
				String sign = MD5Util.GetMD5Code(prestr+ Content.PRIVATE_KEY);
				
				testStr.put("sign", sign);  //���    ժҪ��Ϣ  ����   ��������
				
				toServer(testStr);
			}
			break;
		}
    }

	/**
	 * �����߳�   ��������
	 * @param testStr
	 */
	private void toServer(final JSONObject testStr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultStr = SocketToNet.socketTcpRequset(Content.SERVER_IP, Content.SERVER_PORT,
						testStr);
				
				//����������͸���ui:
				Message msg = Message.obtain(); 
				msg.what = THREAD_IS_OK;
				handler.sendMessage(msg);
			}
		}).start();		
	}
	
	private Handler handler = new MainHandler();// ���ܴ�ӡ�� �ش�����������

	private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case THREAD_IS_OK: { 
					
					textView2.setText(resultStr);
					Toast.makeText(MainActivity.this, "֧���ɹ�", 1).show();
					
					break;
				}
	
				default:
					break;
			}
		}
	}
	

}
