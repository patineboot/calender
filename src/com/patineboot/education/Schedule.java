/*
 * Copyright (c) 2020, 2021, Patineboot
 * All rights reserved.
 */

package com.patineboot.education;

import java.time.format.DateTimeParseException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 予定表クラスです。
 * 予定を登録、削除、検索することができます。
 * 予定は、日付と用件で構成されています。
 * - 日付: YYYYMMDDhhmm形式を指定し、例えば、例えば2000年9月2日16時5分は200009021605となる。
 * - 用件: Unicodeでの基本多言語面(BMP)コード・ポイント(U+0000 - U+FFFF)の文字セットで、256文字までとする。
 * .
 * 検索は、範囲、日時の始まりと終わり、を指定し、終わりより前の日時を検索結果に含める。
 * 予定表には、最大登録可能件数を設定できます。
 */
public class Schedule {

    /**
     * 予定の用件の最大サイズ
     */
    private static final int CONTENT_LENGTH = 256;

    /**
     * 予定の最大登録件数
     */
    private final long capacity;

    /**
     * 予定のセット
     */
    private final TreeSet<Plan> plans;

    /**
     * 予定表クラスを、最大登録可能な件数を指定し、作成する。
     * 
     * @param capacity 最大登録可能件数。最大登録可能件数のチェックが不要な場合は、Long.MAX_VALUEを指定してください。
     * @exception IllegalArgumentException capacityが0以下の場合
     */
    public Schedule(long capacity)
    {
        // 用件を256文字以内に設定する。
        Plan.configureContentLength(CONTENT_LENGTH);

        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity is zero or negative value");
        }
        this.capacity = capacity;

        // TODO: JCFのマルチスレッド対応はできる。要望を確認してから実装する。
        // memo: SortedSet s = Collections.synchronizedSortedSet(new TreeSet(...));
        plans = new TreeSet<>();
    }

    /**
     * 予定を予定表に登録する。
     * @param dateTime 日時。YYYYMMDDhhmm形式で指定する。
     * @param content 用件。全角文字列、256文字以内。
     * @exception DateTimeParseException 日時形式が不正。
     * @exception IllegalArgumentException contentの長さが長すぎる。dateTimeまたはcontentが、null。
     * @exception IllegalStateException 最大登録した後に、さらに呼び出した。登録済みの予定で呼び出した。
     * 
     * 登録済みの予定と、同じ日時と異なる用件で、登録することが可能です。
     * しかし、同じ日時と同じ用件の予定は登録できません。その場合、IllegalStateExceptionが発生します。
     */
    public void add(String dateTime, String content)
    {
        if (dateTime == null || content == null) {
            throw new IllegalArgumentException("dateTime or content is null.");
        }
        // 最大登録可能件数を超えて、登録した時
        if (plans.size() >= capacity) {
            throw new IllegalStateException("no capacity more");
        }

        // may throw DateTimeParseException or IllegalArgumentException
        var plan = new Plan(dateTime, content);
        if (!plans.add(plan)) {
            throw new IllegalStateException("duptilcate plan");
        }
    }

    /**
     * 予定を予定表から削除する。
     * @param dateTime 日時。YYYYMMDDhhmm形式で指定する。
     * @param content 用件。全角文字列、256文字以内。
     * @exception DateTimeParseException 日時形式が不正。
     * @exception IllegalArgumentException contentの長さが長すぎる。dateTimeまたはcontentが、null。
     * @exception IllegalStateException 予定が見つからなかった。
     */
    public void remove(String dateTime, String content)
    {
        if (dateTime == null || content == null) {
            throw new IllegalArgumentException("dateTime or content is null.");
        }

        // may throw DateTimeParseException or IllegalArgumentException
        var plan = new Plan(dateTime, content);
        if(!plans.remove(plan)){
            throw new IllegalStateException("Not found");
        }
    }

    /**
     * 予定を検索時に使用するダミー用件
     */
    private static final String CONTENT_DUMMY = "";

    /**
     * 予定を予定表から検索する。
     * @param fromDateTime 始点日時(これを含む)
     * @param toDateTime 終点日時(これを含まない)
     * @return 予定の一覧
     * @exception DateTimeParseException 日時形式が不正。
     * @exception IllegalArgumentException contentの長さが長すぎる。fromElementがtoElementより前。fromDateTimeまたはtoDateTimeがnull。
     * fromDateTimeとtoDateTimeが等しい場合は、空のセットが返されます。
     */
    public SortedSet<Plan> find(String fromDateTime, String toDateTime)
    {
        if (fromDateTime == null || toDateTime == null) {
            throw new IllegalArgumentException("fromDateTime or toDateTime is null.");
        }

        var from = new Plan(fromDateTime, CONTENT_DUMMY);
        var to = new Plan(toDateTime, CONTENT_DUMMY);

        // may throw NullPointerException or IllegalArgumentException.
        var subset = plans.subSet(from, to);
        return subset;
    }
}