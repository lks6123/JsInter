package com.lks6123.jsinter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        //首先设置webview支持Javascript     注意:虽然这是最常见的与js交互的方式, 但在4.1之前的版本,可能会有严重的安全漏洞
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new JSObject(), "myObj");
        mWebView.loadUrl(mUrl);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mEditText.getText().toString();
                if (!string.isEmpty()) {
                    mWebView.loadUrl("javascript:onButtonClick('" + string + "')");
                }
            }
        });
    }

    private boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    class JSObject {
        //当点击webView中的图片之后,会通过js调用这个方法,显示出图片的地址,宽和高
        @JavascriptInterface  //17版本后,必须添加这个注解才能被js调用,建议全部添加
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
