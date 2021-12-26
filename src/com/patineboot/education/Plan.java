/*
 * Copyright (c) 2020, 2021, Patineboot
 * All rights reserved.
 */

package com.patineboot.education;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;


/**
 * 予定クラスです。
 * 日時と用件で構築することができます。
 * 日時と用件を取得することができます。
 * @implSpec このクラスは不変でスレッドセーフです。
 */
public class Plan implements Comparable<Plan> {

    /**
     * 設定している用件の最大サイズ
     */
    private static int contentMaxLength;

    /**
     * 日時
     */
    private final LocalDateTime dateTime;

    /**
     * 用件
     */
    private final String content;

    /**
     * 用件の最大サイズを設定する。
     * @param length 用件の最大サイズ
     * @exception IlligalArgumentException 負の値を指定したとき
     *
     * メモ：
     * Planを生成するクラス(Builder)を導入し、PlanはBuilderでのみ生成する。
     * アプリケーションは、Builderに用件の最大サイズを設定することで、
     * Plan毎に用件の最大サイズを設定できる。
     * しかし、顧客の想定はstatic fieldでの即値を使用したチェックだろう。
     */
    static void configureContentLength(int length)
    {
        contentMaxLength = length;
    }

    /**
     * 日時、用件から読み出しのみ可能な予定を作成する。
     * 
     * @param dateTime 日時。YYYYMMDDhhmm形式で指定する。
     * @param content 用件。全角文字列、事前に設定した文字列以内で指定する。
     * @exception DateTimeParseException 日時形式が不正。
     * @exception IllegalArgumentException contentの長さが長すぎる。dateTimeまたはcontentが、null。
     * @see configureContentLength
     * @see YYYYMMDDhhmm形式の参考。大文字小文字に意味がある。
     * @see <a href="https://qiita.com/tasogarei/items/df9e43ac36bde55aa928">JavaのDateFormatの小文字vs大文字</a>
     * 日付は、西暦0年1月1日から西暦9999年12月31日。時刻は00:00から23:59の範囲が有効。
     */
    public Plan(String dateTime, String content)
    {
        if (dateTime == null || content == null) {
            throw new IllegalArgumentException();
        }
        // 日時の保管
        var dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmm");
        dtf = dtf.withResolverStyle(ResolverStyle.STRICT);
        // throw DateTimeParseException when parse error occur.
        this.dateTime = LocalDateTime.parse(dateTime, dtf);

        // 用件の保管
        var contentlength = content.codePointCount(0, content.length());
        if (contentlength > contentMaxLength) {
            throw new IllegalArgumentException("content length over");
        }
        this.content = content;
    }

    /**
     * 予定から日時を取得する
     * @return 日時
     */
    public LocalDateTime getDateTime()
    {
        return dateTime;
    }

    /**
     * 予定から用件を取得する
     * @return 用件
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int compareTo(Plan plan)
    {
        int result = dateTime.compareTo(plan.dateTime);
        if (result == 0) {
            result = content.compareTo(plan.content);
        }

        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        var plan = (Plan)obj;
        boolean isEqual = dateTime.equals(plan.dateTime);
        if (isEqual) {
            isEqual = content.equals(plan.content);
        }
        return isEqual;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode()
    {
        int result = dateTime.hashCode() + content.hashCode();
        return result;
    }
}
