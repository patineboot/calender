# schedule.jar

## Requirements

後進の育成のためにJava Calender class APIを公開する。

スケジュール（「日時」と「用件」の組）を設定し、後から参照する。

- 「日時」は、西暦YYYY年MM月DD日hh時mm分をYYYYMMDDhhmmという12桁の数字による文字列で表現する。例えば2000年9月2日16時5分は200009021605となる。
- 存在しない「日時」による設定や検索は不可（エラー）とする。
- 同一の「日時」に複数の予定を登録できるものとする。
- 「用件」は、全角文字列とし、256文字以内とする。

## Design

予定表クラス(`Schedule`)と予定クラス(`Plan`)で実現する。

予定検索時に **O(log N)** の計算量で、任意の要素にアクセスできるリストとして、ソート済みデータリストと、バイナリサーチを考えた。

予定検索を、Java標準クラスを使用して実現するには、Java Collections Frameworkに含まれるソート可能なコレクション`TreeSet`が最も適している。

`TreeSet`でソートする手段を、予定クラスに実装する。

### 予定表クラス(`Schedule`)

- コンストラクタ: 予定の最大登録件数で予定表クラスを構築する。
- 予定設定(`add`): 日時、用件を入力として、予定を設定する。
- 予定検索(`find`): 始点日時、終点日時を入力として、指定した範囲の予定一覧を返却する。
- 予定削除(`remove`): 日時、用件を入力として、予定を削除する。

日時、用件の正当性の確認は、予定クラスで行う。

#### コンストラクタ

予定表クラスを構築する。予定の最大登録件数は、インスタンス変数に保持する。予定設定時参照し、最大登録件数に達している時は、例外を発生する。

#### 予定設定(`add`)

予定を予定表に設定する。予定設定は、日時が同じで用件が異なる予定を設定できる。ただし、日時、用件、両方とも同じ場合は、例外を発生する。

#### 予定検索(`find`)

予定表から予定を検索する。始点日時は、検索結果に含み、終点日時は検索結果に含めない。予定検索における日時の始点、終点の正当性の確認を行う。不正な場合は、例外を発生する。

#### 予定削除(`remove`)

予定を削除する。予定削除は、予定が見つからなかった時は、例外を発生する。

### 予定クラス(`Plan`)

- コンストラクタ: 日時と用件を入力とし、入力の正当性を確認する。不正な入力を受けた時は、例外を発生する。
- 日時取得(`getDateTime`): 日時を取得できる。
- 用件取得(`getContent`): 用件を取得できる。
- 用件の最大サイズの設定(`configureContentLength`): 用件の最大長さを設定する。

予定クラスは、イミュータブルな値クラスで実現する。
比較演算を実装するため、 `compareTo`、`equals`、`hashCode`をオーバーライドする。

#### コンストラクタの処理

日時と用件から予定クラスを構築する。

日時と用件の正当性を確認する。不正な場合には、例外を発生する。

- 日時の長さが12文字であり、有効文字のみ
- 年、月、日、時、分が有効な範囲内
- 閏年を考慮した日付が実在する
- 用件が最大サイズ以内

日時は、最も適したJava標準クラス`LocalDateTime`に変換して保持する。

#### 用件の最大サイズの設定(`configureContentLength`)

コンストラクタでの正当性確認で設定した値を使用する。

## 実行方法

### 実行環境

- Mac OS X
  - version: 10.15.7
  - BuildVersion: 19H2
- Java Development Kit
  - java version "12.0.2" 2019-07-16
  - Java(TM) SE Runtime Environment (build 12.0.2+10)
  - Java HotSpot(TM) 64-Bit Server VM (build 12.0.2+10, mixed mode, sharing)
- GNU Make 3.81
- Visual Studio Code Version: 1.51.1

### 実行モジュール作成

コンソールアプリケーション作成手順。

1. MacOS付属のターミナルを開く。
1. `Project_Schedule`ディレクトリへ移動してmakeを実行する。

```bash
make -f Makefile.gmk all javadoc
```

*`Makefile.gmk`のターゲット説明*

- `all`: コンソールアプリケーションを生成する。デフォルトターゲット。
- `javadoc`: クラスライブラリのAPIドキュメントを生成する。
- `clean`: 作成したコンソールアプリケーションを削除する。

### コンソールアプリケーション実行

ターミナルにて`Project_Schedule`ディレクトリへ移動してjavaコマンドを実行する。

```bash
java -jar schedule.jar
```

*`schedule.jar`の起動パラメータ*

予定の最大登録件数を指定できる。

起動パラメータとして入力する。起動パラメータ省略時のデフォルト値は5。

起動パラメータ実行例

```bash
java -jar schedule.jar 10
```

### コンソールアプリケーション操作

**メイン操作画面**

```bash
Type a control command and return.
Commands a: ADD        d: DELETE     s: SEARCH     l: LIST
         t: auto Test  b: Benchmark  e: END
```

コマンド一覧。詳細な説明は、後方にあり。

- a: ADD 予定を設定します。予定はコマンド選択後に入力します。
- d: DELETE 予定を削除します。予定はコマンド選択後に入力します。
- s: SEARCH 予定を検索します。始点日時、終点日時はコマンド選択後に入力します。
- l: LIST 設定済みの予定の一覧を表示します。
- t: auto Test 自動的テストを実行します。
- b: Benchmark 性能検証を実行します。性能検証の種類をコマンド選択後に入力します。
- e: END アプリケーションを終了します。

*実行例*

```bash
Commands a: ADD        d: DELETE     s: SEARCH     l: LIST
         t: auto Test  b: Benchmark  e: END
a[return] ← 'a'をタイプし、リターンキーを押下。
```

*表示画面*

```bash
ADD Command: Type date time and content of the plan.
 e.g. [Date Time]<space>[Content].
```

#### a: ADD 予定を設定

**予定設定画面**

```bash
ADD Command: Type date time and content of the plan.
 e.g. [Date Time]<space>[Content].
```

設定する予定を入力します。設定後は、**メイン操作画面**へ自動で遷移します。

- [Date Time] 日時。形式はYYYYMMDDhhmmという12桁の数字。
- [Content] 用件。全角文字列で256文字以内。

*実行例*

```bash
ADD Command: Type date time and content of the plan.
 e.g. [Date Time]<space>[Content].
202011122200 締め切り[return] ← 日時と用件をタイプし、リターンキーを押下。
```

#### d: DELETE 予定を削除

**予定削除画面**

```bash
DELETE Command: Type date time. of the plan.
 e.g. [Date Time]<space>[Content].
```

削除する予定を入力します。設定後は、**メイン操作画面**へ自動で遷移します。

- [Date Time] 日時。形式はYYYYMMDDhhmmという12桁の数字。
- [Content] 用件。全角文字列で256文字以内。

*実行例*

```bash
DELETE Command: Type date time. of the plan.
 e.g. [Date Time]<space>[Content].
202011122200 締め切り[return] ← 日時と用件をタイプし、リターンキーを押下。
```

#### s: SEARCH 予定を検索

**予定検索画面**

```bash
SEARCH Comand: Type start and end date time.
 [Start Date Time]<space>[End Date Time].
```

検索の始点日時、終点日時を入力します。設定後は、**メイン操作画面**へ自動で遷移します。

- [Start Date Time] 始点開始日時。形式はYYYYMMDDhhmmという12桁の数字。
- [End Date Time] 終点開始日時。形式はYYYYMMDDhhmmという12桁の数字。

*実行例*

```bash
SEARCH Comand: Type start and end date time.
 [Start Date Time]<space>[End Date Time].
202011122200 202011122201 ← 始点日時と終点日時をタイプし、リターンキーを押下。
```

*表示*

```bash
DateTime: 2020-11-12T22:00 Content: 締め切り
```

#### t: auto Test 自動的テスト

**自動テスト**

```bash
Auto Test Start: 2020/11/12 16:46:00
Auto Test End: Succeeded 2020/11/12 16:47:48
```

自動テストを実行します。

ScheduleクラスのAPIを実際に呼び出し、期待する結果と実際の結果を比較します。
PlanクラスのAPIは、Scheduleクラスから呼び出しテストします。

- 成功した場合は、テストが完了します。
- 失敗した場合は、例外が発生します。多くの場合、RuntimeExceptionが発生します。

**テスト内容**

1. Scheduleコンストラクタ
   - 引数の最大登録可能件数の上限、下限
   - 引数の最大登録可能件数にゼロ、負の数字
1. 正常系の呼び出し
   - 予定を最大登録件数まで登録できる
   - 予定を最大登録件数より多くは登録できない
   - さまざまな予定を、設定、検索とその結果を見て、削除する。
   - 日時の月と日の組み合わせ、大の月、小の月、閏年。
   - 予定が含む用件の長さの上限、下限
   - 検索で０件の発見
   - 検索での始点日時、終点日時が同じ
1. addメソッド(不正な日時)
   - 日時の月がゼロ、上限より１大きい
   - 日時の日がゼロ、上限より１大きい
   - 日時の時が、上限より１大きい
   - 日時の分が、上限より１大きい
   - 日時の時間が、上限より１分大きい
   - 日時の文字列がフォーマットより、１短い、１大きい、不正な文字を含む
   - 日時がnull
1. addメソッド(不正な用件)
   - 用件の長さが、１大きい
   - 用件がnull
1. removeメソッド(不正な用件)
   - 設定されていない予定を、削除しようとした
   - 日時がnull
   - 用件がnull
1. findメソッド(不正な用件)
   - 終点日時が、始点日時より前
   - 始点日時がnull
   - 終点日時がnull

#### b: Benchmark 性能検証

**性能検証**

1000万件から500万件、5万件を検索し、**検索時間はほぼ同じ**になる。

**手順**

1. アプリケーションを起動します  
    起動していた場合は、再起動します。  
    **注意**：再起動しない場合は、性能評価が正常にできません。
1. 性能検証で "max" を選びます。
1. アプリケーションを再起動します。
1. 性能検証で "min" を選びます。
1. 画面出力Found Plans(num)で予定件数が、100倍大きいことを確認。
1. 画面出力elapse time(ns)で時間は、**ほぼ同じ**ことを確認。

*アプリケーション起動コマンド*

```bash
java -jar schedule.jar
```

*性能検証コマンド*

```bash
Benchmark Command: Type "max" or "min".
max[return] ← maxとタイプし、リターンキーを押下。
```

maxかminをタイプします。

*Max画面出力*

```bash
[Max] Preparing... 2020/11/12 18:26:02
[Max] Prepared!    2020/11/12 18:26:35
[Max] Found Plans(num): 5492310 ← 検索で見つけた予定
[Max] All   Plans(num): 10962750 ← 予定表の全ての予定

[Max] elapse time(ns): 481506
```

*Min画面出力*

```bash
[Min] Preparing... 2020/11/12 18:27:09
[Min] Prepared!    2020/11/12 18:27:41
[Min] Found Plans(num): 54810 ← 検索で見つけた予定
[Min] All   Plans(num): 10962750 ← 予定表の全ての予定

[Min] elapse time(ns): 532218
```
