package com.delivame.delivame.deliveryman.activities.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.delivame.delivame.deliveryman.R;
import com.delivame.delivame.deliveryman.activities.BaseActivity;
import com.delivame.delivame.deliveryman.utilities.Constants;
import com.delivame.delivame.deliveryman.utilities.MyUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.delivame.delivame.deliveryman.utilities.Constants.TAG;

public class WebContentActivity extends BaseActivity {

    String url;
    @BindView(R.id.webview)
    WebView webview;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        ButterKnife.bind(this);
        getExtras();
        initUI(title, true);
        MyUtility.logI(TAG, "url = " + url);
        webview.loadUrl(url);
    }

    public static void startMe(Context context, String url, String title) {
        Intent intent = new Intent(context, WebContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.WEB_URL, url);
        intent.putExtra(Constants.ACTIVITY_TITLE, title);
        context.startActivity(intent);
    }

    private void getExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra(Constants.WEB_URL);
            title = intent.getStringExtra(Constants.ACTIVITY_TITLE);
        }
    }
}
