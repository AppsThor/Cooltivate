package com.droidcon.it.hackaton.cooltivate;

import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cooltivate.hackathon.it.droidcon.com.cooltivate.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById
    WebView webView;

    @AfterViews
    public void loadWebView() {
        String divVideo = "<div class=\"iv-embed\" style=\"margin:0 auto;padding:0;border:0;width:642px;\"><div class=\"iv-v\" style=\"display:block;margin:0;padding:1px;border:0;background:#000;\"><iframe class=\"iv-i\" style=\"display:block;margin:0;padding:0;border:0;\" src=\"//open.ivideon.com/embed/v2/?server=100-47089094a84620e10715eda4b9a87bfb&amp;camera=0&amp;width=&amp;height=&amp;lang=en\" width=\"640\" height=\"480\" frameborder=\"0\" allowfullscreen></iframe></div><div class=\"iv-b\" style=\"display:block;margin:0;padding:0;border:0;\"><div style=\"float:right;text-align:right;padding:0 0 10px;line-height:10px;\"><a class=\"iv-a\" style=\"font:10px Verdana,sans-serif;color:inherit;opacity:.6;\" href=\"http://www.ivideon.com/\" target=\"_blank\">powered by Ivideon</a></div><div style=\"clear:both;height:0;overflow:hidden;\">&nbsp;</div><script src=\"//open.ivideon.com/embed/v2/embedded.js\"></script></div></div>";
        String html = "<html><head></head><body>" + divVideo + "</body></html>";
        webView.loadData(html, "text/html", null);
    }

}
