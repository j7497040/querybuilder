======================================
基本的な使い方
======================================

-----------------
免責事項
-----------------
本ソフトウエアは無保証です。

-----------------
ダウンロード
-----------------

 Mavenを使用する場合はpom.xmlに以下の記述を追加してください。
 
 .. sourcecode:: xml
    
    <dependency>
        <groupId>jp.co.hitachi.gp.ap.util.dao</groupId>
        <artifactId>querybuilder</artifactId>
        <version>1.1</version>
    </dependency>


-----------------
QueryBuilderの概要
-----------------

QueryBuilderは、条件式を組み立てるクラスです。
条件式は、and、orでの連結および括弧"()"によるネスト構造を表現できる柔軟なものです。

QueryBuilderは、「流れるようなインターフェース（fluent interface）」での条件式の構築ができます。

例えば、 ``name = 'smith' and (passwd = 'JUkdn38cXIu' or expires > '20080601')`` の条件式を組み立てるとき、以下のようなコードになります：

.. sourcecode:: java
    
    QueryBuilder q = new QueryBuilder();
    q.exp("name = 'smith'").and()
        .begin()
            .e(passwd = 'JUkdn38cXIu').or().e("expires > '20080601'")
        .end();
    
    String s = q.getQuery(); // where name = 'smith' and (passwd = 'JUkdn38cXIu' or expires > '20080601')
                             // が得られる

どうでしょう。いままでは少し違った感じがするのではないでしょうか。

そう思っていただけるとねらいどおりです。このインターフェースは紙の紙面だけでは伝えきれない魅力をもっています。

例えば、上記の例で、 ``e("name = 'smith'").`` と打ったあと、大抵のIDE(eclipse等)は、候補として、 ``and(), or(), end()`` を表示してくれるはずです。

ここで ``and()`` を選択し、さらに.を入力すると、``e(), begin()`` を候補として表示してくれるのです。

これは簡単な構文チェック機能であり、プログラマーが些細なミスを犯すことを抑制するということです。


その他、パラメータを式に埋め込んだり、その設定値を式に設定することができます。その後でパラメータ名と設定値のペアをMapインターフェースで受け取ることもできます。

例えば以下のようなものです：

.. sourcecode:: java
    
    QueryBuilder q = new QueryBuilder();
    q.e("name = :name", "smith").and()
        .begin()
            .e(passwd = :passwd", "JUkdn38cXIu").or().e("expires > :expires", "20080601")
        .end();
    
    Map<String, Object> params = q.getParams();


 ``:`` で始まる単語がパラメータ名です。
これはSQLの名前付クエリで一般的な書式です。
上記の例のparamsは以下のペアを含んでいます。

.. sourcecode:: none
    
    :name -> "smith"
    :passwd -> "JUkdn38cXIu"
    :expires -> "20080601"


一つの ``e()`` の中にはパラメータを複数含めることができます。
パラメータの数と、その設定値の数は正確に一致している必要があります。

不一致の場合、 ``QueryBuilder#getParams()`` で実行時例外が発生します。

-----------------
使い方
-----------------

さて、前章ですでに私は使い方を説明した気になっています。みなさんはどうでしょうか？

実際、QueryBuilderの使い方はすばらしく単純です。QueryBuilderをインスタンス化した後は、流れるようなインターフェースに従って条件式を構築するだけなのです。

流れるようなインターフェースは最低限、プログラマーがよくやる些細なミスを抑制する機能まであります。

条件式の構築が終われば、あとは ``QueryBuilder#getQuery()`` を呼び出すだけです。

パラメータがほしい場合は、 ``QueryBuilder#getParams()`` です。

さて後はどんな説明がいるでしょう。
とはいっても、QueryBuilder独特の言い回しや語句は必ずあるものです。その点を補足しましょう。


-----------------
beginとend
-----------------

これはもうお気づきの方も多いと思いますが、 ``begin()`` は左括弧 ``(`` を、 ``end()`` は右括弧 ``)`` をあらわします。

別な言い方をすると、 ``begin()`` で括弧によるネストを開始し、 ``end()`` でネストを終了します。

QueryBuilderでは、ネストの階層に制限はありません。begin()を10回繰り返しても誤動作したり、例外を投げたりはしません。

-----------------
パラメータの書式
-----------------

 ``:`` で始まる単語がパラメータ名です。
これはSQLの名前付クエリで一般的な書式です。

Expに複数のパラメータ名を混ぜることは自由ですが、QueryBuilderが認識するのは最初に表れるパラメータだけです。
他は無視します。


以上が補足です。


----------------------
QueryBuilderの使い所
----------------------

最後にQueryBuilderをどこで使うかの例をあげましょう。

例えば、動的にSQLのwhere句を組み立てることを考えてください。
名前付クエリが使えるなら、QueryBuilderはすばらしくフィットします。

名前付クエリはSpring frameworkやJPAでサポートされています。




