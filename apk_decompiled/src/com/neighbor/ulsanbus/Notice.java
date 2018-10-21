package com.neighbor.ulsanbus;

import android.util.Log;

public class Notice {
    private String ErrorMsg;
    private String NoticeCnt;
    private String NoticeContent;
    private String NoticeDate;
    private String NoticeFile;
    private String NoticeTitle;
    private String Seq;

    public String getNoticeCnt() {
        return this.NoticeCnt;
    }

    public void setNoticeCnt(String noticecnt) {
        this.NoticeCnt = noticecnt;
    }

    public String getSeq() {
        return this.Seq;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getNoticeDate() {
        return this.NoticeDate;
    }

    public void setNoticeDate(String noticedate) {
        this.NoticeDate = noticedate;
    }

    public String getNoticeTitle() {
        return this.NoticeTitle;
    }

    public void setNoticeTitle(String noticetitle) {
        this.NoticeTitle = noticetitle;
    }

    public String getNoticeContent() {
        return this.NoticeContent;
    }

    public void setNoticeContent(String noticecontent) {
        this.NoticeContent = noticecontent;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }

    public void toLogNotice() {
        Log.e("Notice", "NoticeCnt : " + this.NoticeCnt);
        Log.e("Notice", "Seq : " + this.Seq);
        Log.e("Notice", "NoticeDate : " + this.NoticeDate);
        Log.e("Notice", "NoticeTitle : " + this.NoticeTitle);
        Log.e("Notice", "NoticeContent : " + this.NoticeContent);
        Log.e("Notice", "ErrorMsg : " + this.ErrorMsg);
        Log.e("Notice", "NoticeFile : " + this.NoticeFile);
    }

    public String getNoticeFile() {
        return this.NoticeFile;
    }

    public void setNoticeFile(String noticeFile) {
        this.NoticeFile = noticeFile;
    }
}
