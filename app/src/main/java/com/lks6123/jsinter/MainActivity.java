package com.lks6123.jsinter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_view)
    TextView mTextView;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.edit_text)
    EditText mEditText;
    @BindView(R.id.button)
    Button mButton;
    Context mContext = null;

    String mUrl = "file:///android_asset/test.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        //首先设置webview支持Javascript     注意:虽然这是最常见的与js交互的方式,
        // 但在4.1(包括4.1)之前的版本,会有严重的安全漏洞,可能会引发跨站脚本攻击
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSObject(), "myObj");
        mWebView.loadUrl(mUrl);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注意括号中的单引号, 传入的参数是字符串的话要加单引号
                String string = "javascript:onButtonClick('" + mEditText.getText().toString() + "')";
//                String string = "javascript:onButtonClick(" + 3 + ")";
                if (!string.isEmpty()) {
                    //这种方法是在安卓4.4才加入的,优点是不会刷新页面,可直接接收返回值,
                    //缺点是因为是4.4才加入的,局限性比较大
                    if (Build.VERSION.SDK_INT > 18) {
                        mWebView.evaluateJavascript(string, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                //调用之后的回调,可以处理逻辑, value就是调用js方法后的返回值
                                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mWebView.loadUrl(string);//最常见的调用native调用JS方法
                    }
                } else {
                    Toast.makeText(mContext, "请在文本框中输入内容", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    class JSObject {
        //当点击webView中的图片之后,会通过js调用这个方法,显示出图片的地址,宽和高
        @JavascriptInterface  //版本17后,必须添加这个注解才能被js调用,建议所有要调用的方法都加上
        public void onImageClick(String text, int width, int height) {
            final String str = "图片被点击: Url = " + text + "  宽度 = " + width + "  高度 = " + height;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(str);
                }
            });
        }
    }
}
