/*
 * Copyright (c) 2020, 2021, Patineboot
 * All rights reserved.
 */

package com.patineboot.education;

import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeSet;
import java.text.SimpleDateFormat;

public class ConsoleApplication {

    public static void main(String[] args) {
        var application = new ConsoleApplication();
        application.execute(args);
    }

    public void execute(String[] args) {
        var schedule = new Schedule(Long.parseLong(args[0]));

        try (var scanner = new Scanner(System.in)) {
            mainloop: for (;;) {
                try {
                    System.out.println("\nType a control command and return.\nCommands a: ADD, d: DELETE, s: SEARCH, l: LIST t: TEST e: END");
                    var charString = scanner.nextLine();
                    var type = charString.charAt(0);

                    switch (type) {
                    case 'a':
                        System.out.println("\nADD Command: Type date time and content of the plan.\n e.g. [Date Time]<space>[Content].");
                        var dateTime = scanner.next();
                        var content = scanner.next();
                        // 標準入力を次の行まで読み捨て
                        scanner.nextLine();

                        schedule.add(dateTime, content);
                        break;
                    case 'd':
                        System.out.println("\nDELETE Command: Type date time. of the plan.\n e.g. [Date Time]<space>[Content].");
                        var rDateTime = scanner.next();
                        var rContent = scanner.next();
                        // 標準入力を次の行まで読み捨て
                        scanner.nextLine();

                        schedule.remove(rDateTime, rContent);
                        break;
                    case 's':
                        System.out.println("\nSEARCH Comand: Type start and end date time.\n [Start Date Time]<space>[End Date Time].");
                        var from = scanner.next();
                        var to = scanner.next();
                        // 標準入力を次の行まで読み捨て
                        scanner.nextLine();

                        var subset = schedule.find(from, to);

                        for (var plan: subset) {
                            System.out.println("DateTime: " + plan.getDateTime() + " Content: " + plan.getContent());
                        }
                        break;
                    case 'l':
                        System.out.println("\nLIST of plans.");

                        var start = "000001010000";
                        var end = "999912312339";
                        var all = schedule.find(start, end);

                        for (var plan: all) {
                            System.out.println("DateTime: " + plan.getDateTime() + " Content: " + plan.getContent());
                        }
                        break;
                    case 't':
                        System.out.println("\nAuto Test Start: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "\n");

                        var test = new AutoTest();
                        test.Schedule_Schedule();
                        test.Schedule_add();
                        test.Schedule_add_datetime_invalid();
                        test.Schedule_add_content_invalid();
                        test.Schedule_remove_invalid();
                        test.Schedule_find_invalid();
                        test.Schedule_benchmark();

                        System.out.println("\nAuto Test end: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                        break;
                    case 'e':
                        break mainloop;
                    default:
                        System.out.println("Invalid Charactor.");
                        break;
                   }
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private class AutoTest {
        public void Schedule_Schedule() {

            // 最大登録件数の有効範囲の下限
            {
                var s = new Schedule(1);
            }

            // 最大登録件数の有効範囲の上限
            {
                var s = new Schedule(Long.MAX_VALUE);
            }

            // 不正な最大登録件数を指定した 0
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(0);
                }
                catch (IllegalArgumentException iae) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException();}
            }

            // 不正な最大登録件数を指定した -1
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(-1);
                }
                catch (IllegalArgumentException iae) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException();}
            }

        }

        private static final String VALID_DATE_TIME = "202011220123";
        private static final String VALID_YEAER = "2020";
        private static final String VALID_MONTH = "11";
        private static final String VALID_DAY = "22";
        private static final String VALID_TIME = "0123";
        private static final String VALID_CONTENT = "東京駅の新幹線口で待ち合わせ";
        private static final String VALID_CONTENT2 = "東京駅の新幹線口で待ち合わせ☺️";
        private static final String VALID_CONTENT_MAX = 
"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０" +
"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０" +
"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０" +
"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０" +
"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０" +
"１２３４５６";

        private class Mode {
            public Mode(String c, boolean is) {
                content = c;
                isAddition = is;
            }
            public final String content;
            public final boolean isAddition;
        }

        public boolean isValidDay(int year, int month, int day){
            boolean isValid = false;
            boolean isLeapYear = false;
            if (year % 400 == 0) {
                isLeapYear = true;
            }
            else if (year % 100 == 0) {
                // no operation
            }
            else if (year % 4 == 0) {
                isLeapYear = true;
            }

            switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                if ( day <= 31 && day >= 1) {
                    isValid = true;
                }
                break;
            case 4: case 6: case 9: case 11:
                if ( day <= 30 && day >= 1) {
                    isValid = true;
                }
                break;
            case 2:
                if ((isLeapYear && day == 29)
                        || (day <= 28 && day >= 1)) {
                    isValid = true;
                } 
            }
            return isValid;
        }

        public void Schedule_add() {
            // 最大登録件数のまで登録でき、それ以上登録するとエラー
            {
                boolean isOk = false;
                int number = -1;
                try {
                    var s = new Schedule(10);
                    for(number = 0; number < 11; number++) {
                        String c = Integer.toString(number);
                        s.add(VALID_DATE_TIME, c);
                    }
                }
                catch (IllegalStateException ise) {
                    if (number == 10) {
                        isOk = true;
                    }
                }
                if (!isOk) { throw new RuntimeException();}
            }

            // 日付の有効範囲で登録、削除。
            {
                Mode[] modes = {
                    new Mode(VALID_CONTENT, true),
                    new Mode(VALID_CONTENT2, true),
                    new Mode(VALID_CONTENT, false),
                    new Mode(VALID_CONTENT2, false),
                };

                var s = new Schedule(Long.MAX_VALUE);
                TreeSet<Plan> set = null;

                // 有効な全ての日付の1/10000で用件を変えて、2回登録し、全て削除。
                for (int i = 0; i < 4; i++) {
                    for (int year = 0; year < 10000; year += 100 ) {
                        for (int month = 1; month < 13; month++) {
                            for (int day = 1; day < 31; day++) {
                                for (int time = 0; time < 24 * 60; time += 100) {
                                    var date = String.format("%04d%02d%02d%02d%02d", year, month, day, time/60, time%60);
                                    try {
                                        // check for add method
                                        if (modes[i].isAddition) {
                                            s.add(date, modes[i].content);
                                        }
                                        else {
                                            // check for remove method
                                            s.remove(date, modes[i].content);
                                            // check for find method
                                            var plan = new Plan(date, modes[i].content);
                                            if (!set.contains(plan)) {
                                                throw new RuntimeException();
                                            }
                                        }
                                    }
                                    catch (DateTimeParseException ise) {
                                        if (isValidDay(year, month, day)) {
                                            throw ise;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 登録済みの検索結果を保存しておき、削除時に検索結果の内容を確認する
                    if (i == 1) {
                        var view = s.find("000001010000", "999912312359");
                        set = new TreeSet<>(view);
                    }
                }
            }

            // 用件の有効範囲の下限 0文字
            {
                var s = new Schedule(Long.MAX_VALUE);
                String c = "";
                s.add(VALID_DATE_TIME, c);
            }

            // 用件の有効範囲で上限 256文字
            {
                var s = new Schedule(Long.MAX_VALUE);
                String c = VALID_CONTENT_MAX;
                s.add(VALID_DATE_TIME, c);
            }

            // 検索で0件を発見した
            {
                var nonMinutes = VALID_YEAER + VALID_MONTH + VALID_DAY + "00";
                var s = new Schedule(10);
                for(int number = 0; number < 10; number++) {
                    var c = nonMinutes + String.format("%2d", number);
                    s.add(VALID_DATE_TIME, c);
                }

                var view = s.find(nonMinutes + "11" , nonMinutes + "12");
                if (!view.isEmpty()) {
                    throw new RuntimeException();
                }
            }

            // 終了日時が始点日時と同じ
            {
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                var view = s.find(VALID_DATE_TIME, VALID_DATE_TIME);
                if (!view.isEmpty()) {
                    throw new RuntimeException();
                }
            }
        }

        public void Schedule_add_datetime_invalid() {
            // 有効範囲外: 月の下限より1大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + "00" + VALID_DAY + VALID_TIME;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 有効範囲外: 月の上限より1大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + "13" + VALID_DAY + VALID_TIME;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 有効範囲外: 日の下限より1小さい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + VALID_MONTH + "00" + VALID_TIME;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 有効範囲外: 日の上限より1大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + VALID_MONTH + "32" + VALID_TIME;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 有効範囲外: 時間の上限より1大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + VALID_MONTH + VALID_DAY + "2423";
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 有効範囲外: 時間の下限より1小さい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + VALID_MONTH + VALID_DAY + "0160";
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException();}
            }

            // 有効範囲外: 時刻の上限より1大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = VALID_YEAER + VALID_MONTH + VALID_DAY + "2400";
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // パースエラー 文字数が1少ない
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt =  "20201122012";
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // パースエラー 文字数が1多い
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt =  VALID_DATE_TIME + 0;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // パースエラー 不正な文字を含める
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String dt = "000A" + VALID_MONTH + VALID_DAY + VALID_TIME;
                    s.add(dt, VALID_CONTENT);
                }
                catch (DateTimeParseException dtpe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 日時がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                try {
                    s.add(null, VALID_CONTENT);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }
        }

        public void Schedule_add_content_invalid() {
            // 用件の長さが大きい
            {
                boolean isOk = false;
                try {
                    var s = new Schedule(Long.MAX_VALUE);
                    String c = VALID_CONTENT_MAX + "あ";
                    s.add(VALID_DATE_TIME, c);
                }
                catch (IllegalArgumentException iae) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 日時がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                try {
                    s.add(VALID_DATE_TIME, null);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }
        }

        public void Schedule_remove_invalid() {
            // 削除する予定がない
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.remove(VALID_DATE_TIME, VALID_CONTENT2);
                }
                catch (IllegalStateException ise) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 日時がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.remove(null, VALID_CONTENT);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 用件がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.remove(VALID_DATE_TIME, null);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }
        }

        public void Schedule_find_invalid() {
            // 終了日時が始点日時より前の場合
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.find("202011220123", "202011220122");
                }
                catch (IllegalArgumentException iae) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 始点日時がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.find(VALID_DATE_TIME, null);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }

            // 終点日時がnull
            {
                boolean isOk = false;
                var s = new Schedule(Long.MAX_VALUE);
                s.add(VALID_DATE_TIME, VALID_CONTENT);
                try {
                    s.find(null, VALID_DATE_TIME);
                }
                catch (IllegalArgumentException npe) {
                    isOk = true;
                }
                if (!isOk) { throw new RuntimeException(); }
            }
        }

        public void Schedule_benchmark() {
            // 全ての日時に予定を 2件登録。検索は5000年間。
            var schedule = new Schedule(Long.MAX_VALUE);

            for (int year = 0; year < 10000; year += 100 ) {
                for (int month = 1; month < 13; month++) {
                    for (int day = 1; day < 31; day++) {
                        for (int time = 0; time < 24 * 60; time += 100) {
                            var date = String.format("%04d%02d%02d%02d%02d", year, month, day, time/60, time%60);
                            try {
                                schedule.add(date, VALID_CONTENT);
                                schedule.add(date, VALID_CONTENT2);
                            }
                            catch (DateTimeParseException ise) {
                                if (isValidDay(year, month, day)) {
                                    throw ise;
                                }
                            }
                        }
                    }
                }
            }

            long start1 = System.nanoTime();
            var set1 = schedule.find("250001010000", "750012312359");
            long end1 = System.nanoTime();
            System.out.println("full elapse time: " + (end1 - start1));
            System.out.println("Found Plans:" + set1.size());


            // 全ての日時に予定を  1件登録。検索は50年間。
            schedule = new Schedule(Long.MAX_VALUE);

            for (int year = 0; year < 10000; year += 100) {
                for (int month = 1; month < 13; month++) {
                    for (int day = 1; day < 31; day++) {
                        for (int time = 0; time < 24 * 60; time += 100) {
                            var date = String.format("%04d%02d%02d%02d%02d", year, month, day, time/60, time%60);
                            try {
                                schedule.add(date, VALID_CONTENT);
                            }
                            catch (DateTimeParseException ise) {
                                if (isValidDay(year, month, day)) {
                                    throw ise;
                                }
                            }
                        }
                    }
                }
            }

            long start2 = System.nanoTime();
            var set2 = schedule.find("497501012359", "502501012359");
            long end2 = System.nanoTime();
            System.out.println("1/200 part elapse time: " + (end2 - start2));
            System.out.println("Found Plans: " + set2.size());
        }
    }
}

