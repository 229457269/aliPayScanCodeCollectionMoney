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
 * 扫码  收款demo测试      使用Socket实现通讯   比较安全
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
	 * 参数数据：
	 * {"service":"alipay_pay_2","terminal_no":"11110000",
	 * "timestamp":"20160222131228","subject":"商道通产品",
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
				//显示扫描到的内容
				String payId = bundle.getString("result");
				
				textView1.setText("付款状态:\n\r付款id:" + payId +"。");
				textView2.setText("请稍等,正在请求服务器...");
				
				//拼接请求参数：data:
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp = format.format(new Date());    //时间
				
				JSONObject testStr = new JSONObject();
				
				testStr.put("out_trade_no", "201603011288");  //商户流水号
				testStr.put("terminal_no", "00000002");  // 本机终端号
				testStr.put("subject", "酒水");  //消费主题
				testStr.put("total_fee", "0.01");
				
				testStr.put("oto_pid",Content.SHOP_PID);
				testStr.put("timestamp", timestamp);
				testStr.put("dynamic_id", payId); //扫描到的条形码
				
				/**
				 * 将数据  转换成键值对的形式      
				 */
				Map<String, String> map = MapUtils.getParamsFromJson(JSON.toJSONString(testStr));
				// 然后对数据按键的字母顺序     进行排序     
				String prestr = MapUtils.createLinkString(map); 
				// 获取 排序后的字符串的摘要信息
				String sign = MD5Util.GetMD5Code(prestr+ Content.PRIVATE_KEY);
				
				testStr.put("sign", sign);  //最后将    摘要信息  跟在   参数后面
				
				toServer(testStr);
			}
			break;
		}
    }

	/**
	 * 创建线程   访问网络
	 * @param testStr
	 */
	private void toServer(final JSONObject testStr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				resultStr = SocketToNet.socketTcpRequset(Content.SERVER_IP, Content.SERVER_PORT,
						testStr);
				
				//向服务器发送更新ui:
				Message msg = Message.obtain(); 
				msg.what = THREAD_IS_OK;
				handler.sendMessage(msg);
			}
		}).start();		
	}
	
	private Handler handler = new MainHandler();// 接受打印机 回传回来的数据

	private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case THREAD_IS_OK: { 
					
					textView2.setText(resultStr);
					Toast.makeText(MainActivity.this, "支付成功", 1).show();
					
					break;
				}
	
				default:
					break;
			}
		}
	}
	

}
