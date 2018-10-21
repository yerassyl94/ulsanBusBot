package com.neighbor.ulsanbus.network;

import com.neighbor.ulsanbus.Notice;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseNotice extends ParseXML {
    private static final String NOTICE = "http://apis.its.ulsan.kr:8088/Service4.svc/Notice.xo";
    Notice mNotice;
    List<Object> mNoticelist = null;
    String mResult;
    String mTagname;
    URL mUrl;

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/Notice.xo");
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("NoticeInfoTable")) {
                    this.mNotice = new Notice();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equals("NOTICEDATE")) {
                    this.mNotice.setNoticeDate(xpp.getText());
                } else if (this.mTagname.equals("NOTICETITLE")) {
                    this.mNotice.setNoticeTitle(xpp.getText());
                } else if (this.mTagname.equals("NOTICECONTENT")) {
                    this.mNotice.setNoticeContent(xpp.getText());
                } else if (this.mTagname.equals("NOTICEFILE")) {
                    this.mNotice.setNoticeFile(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader")) {
                    if (this.mResult.equals("OK")) {
                        this.mNoticelist = new ArrayList();
                    }
                } else if (this.mTagname.equals("NoticeInfoTable")) {
                    this.mNoticelist.add(this.mNotice);
                }
            }
        }
    }

    public List get() {
        return this.mNoticelist;
    }

    public Object getObject() {
        return super.getObject();
    }
}
