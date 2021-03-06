======================================
付録
======================================

------------------------------
流れるようなインターフェース
------------------------------

ここからは技術的な話題としましょう。興味のない方は、仕事にもどっていただいて結構です。
(ぞろぞろと人がいなくなるのをまつ・・・)

さて、進みましょう。
いままでの説明で流れるようなインターフェースは、IDEの補完機能とあいまってすばらしい効果を生むこと説明しました。

簡単な構文チェックもできることも説明しました。
私はここで、完全な構文チェックとは書きませんでした。実際完全には程遠いのです。
例えば、 ``begin()`` を呼ばないで、 ``e()`` の後に ``end()`` を書くことが可能です。IDEもそういう風に補完します。
しかしこれはエラーなのです。実際には、 ``QueryBuilder#getQuery()`` で例外が投げられます。
ここであなたはこういうかもしれません： ``begin()`` が呼ばれていないなら、 ``end()`` を補完させなければよい、と。

確かにそのとおりです。
しかし考えてください。
実際に ``begin()`` が呼ばれたとして、それを条件にすることはコンパイル時では不可能なのです。

コンパイル時に利用できる情報はクラスに対する静的なものだけです。
IDEは高機能ですが、しかし利用できる情報はこの静的な情報に限定されます。このような理由でその実行時の文脈からはありえないとされる構文もかけるようになっています。ですから、QueryBuilder#getQuery()が投げる例外には注意してください。最終的な構文のチェックはここで行われるのです。


流れるようなインターフェースを実現するには、その時点で入力できる次のメソッドを制限するということです。
この制限を実現するにはどうすればよいでしょうか。コンパイル時情報のみを利用するので、私のとった戦略はクラスによりメソッドを制限する、という方法です。

 ``QueryBuilder#e(...)`` メソッドを呼ぶと、Expオブジェクトが返ります。
IDEで補完されるのはExpクラスのメソッドということになるのです。

Expの次にかけるのは、and(), or(), end()です。ですから、Expクラスにこれらのメソッドを定義すればよいのです。
このようなメソッドのデザインおよび定義を他のクラス(BinOp, Nest, Begin, End, Root)に施すことにより、流れるようなインターフェースを実現しているのです。


------------------------------
条件式の構築
------------------------------

QueryBuilderでの条件式の内部表現は、木構造です。構成単位は、Exp、BinOp, Nest, Begin, End, Rootクラスです。

そのほかに、GroupBy と OrderBy クラスがありますが、これは後で説明します。

Expは式を表します。
e()で生成されるものです。聞きなれないBinOpは、Binary Operatorを略したもので、二項演算子です。and()とor()がこれに当たります。

QueryBuilderでは、andとorの違いは、文字列上の違いでしかありませんから、クラスとしてはBinOpのみを使っています。Nestは括弧によるネスト構造、Beginは左括弧、Endは右括弧をあらわします。

それぞれbegin(), end()で生成されます。Rootは特別な存在で、
木構造の根っこに当たる部分です。

さて退屈な説明が終わったところで実際の例を見てみましょう。

それが一番わかりやすいはずです。

例として、name = 'smith' and (passwd = 'JUkdn38cXIu' or expires > '20080601')を採用します。
この条件式の内部表現は以下のようになります。

 .. sourcecode:: none
    
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-------------------------------+
       |                                          |
     [Exp]                                     [N e s t]
      exp = {name = 'smith'}                     | |  |
                                              +--+ |  +--+
                                              |    |     |
                                           [Begin] |   [End]
                                                   |
                                                 [O r]
                                                  | |
                                           +------+ +--+
                                           |           |
                                         [Exp]        [Exp]
                  exp = {passwd = 'JUkdn38cXIu'}       exp = {expires > '20080601'}



[..]で囲まれているのは、先に説明した構成単位となるオブジェクトです。
[Exp]の下には、exp = ...の表記がありますが、そのExpが持つ式の値をわかりやすさのために書いています。
上下にリンクがありますが、上からでも下からでもたどれるようになっています。

ここでもう理解したと言われる方もいるでしょう。そういう方はどうぞ次の章へ入って下さい。
ここからは、内部表現の構築の過程をステップバイステップで見ていくことにします。

 .. sourcecode:: java
    
    QueryBuilder q = new QueryBuilder();


ここまではいいでしょう。この時点で、内部表現は以下のようになります。

 .. sourcecode:: none
    
               [Root]
                 |
                null

 ``q.e("name = 'smith'")`` まで着ました。
この時点で、Rootの直下にExpがぶら下がります。

 .. sourcecode:: none
    
               [Root]
                 |
               [Exp]
           exp = {name = 'smith'}

最初に示した図は違っていますね。
この時点では次にプログラマーがandを入力するとはわかりません。
andを入力しないかもしれません。

次にandを入力したときに、構造の入れ変えが起こることになるのです。
では、and()を続いて入力してみます。


 .. sourcecode:: java
    
    q.e("name = 'smith'").and()
    

Andの下に左右のオペランドがぶら下がるというモデルを私は選択しました。
ですから、RootとExpの間にAndを挿入し、Andの左オペランドにExpを代入するという操作がここで起こることになります。
その結果、内部表現は以下のようになります。


 .. sourcecode:: none
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-----------------------+
       |                                  |
     [Exp]                               null
      exp = {name = 'smith'}



Andの左オペランドは、現時点ではわかっていませんので、nullになります。

続いて、begin()が入力されます。

 .. sourcecode:: java
    
    q.e("name = 'smith'").and()
        .begin()


 ``begin()`` により、Nestが生成され、Andの左オペランドに代入されます。
また、NestにBeginがぶら下がります。


 .. sourcecode:: none
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-------------------------------+
       |                                          |
     [Exp]                                     [N e s t]
      exp = {name = 'smith'}                     | |  |
                                              +--+ |  +--+
                                              |    |     |
                                           [Begin] |    null
                                                  null


最初に見せた完成図と比べると、[End]の場所がnullになっています。QueryBuilderでは、end()が呼ばれた
時点でEndをNestにぶら下げることにしました。そのため、現時点ではnullのままでよいのです。

さあ、次にいきましょう。


 .. sourcecode:: java
    
    q.e("name = 'smith'").and()
        .begin()
            .e(passwd = 'JUkdn38cXIu')


おそらくご想像のとおり、Nestの下にExpがぶら下がります。

 .. sourcecode:: none
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-------------------------------+
       |                                          |
     [Exp]                                     [N e s t]
      exp = {name = 'smith'}                     | |  |
                                              +--+ |  +--+
                                              |    |     |
                                           [Begin] |    null
                                                   |
                                                 [Exp]
                                       exp = {passwd = 'JUkdn38cXIu'}



.begin()の後、ユーザーが操作するオブジェクトはBeginです。しかし、上図のようにExpはNestにぶら下がります。
実はBegin#begin(),Begin#e()は、Nest内の同名のメソッドに委譲するように設計されています。

そのため、上図のような構成になるのです。これは後ででてくるEndも同じ仕組みになっています。

次にor().e("expires > :expires", "20080601")と続くことになるのですが、これは先にみたAndの場合と
同じですので一気にいきます。

 .. sourcecode:: java
    
    q.e("name = 'smith'").and()
        .begin()
            .e(passwd = 'JUkdn38cXIu').or().e("expires > '20080601'")


の状態は、

 .. sourcecode:: none
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-------------------------------+
       |                                          |
     [Exp]                                     [N e s t]
      exp = {name = 'smith'}                     | |  |
                                              +--+ |  +--+
                                              |    |     |
                                           [Begin] |    null
                                                   |
                                                 [O r]
                                                  | |
                                           +------+ +--+
                                           |           |
                                         [Exp]        [Exp]
                  exp = {passwd = 'JUkdn38cXIu'}       exp = {expires > '20080601'}



さてだいぶ完成に近づきました。

最後は、 ``.end()`` です。 ``end()`` が呼ばれるときにユーザーが操作しているオブジェクトは、 ``exp = {expires > '20080601'}`` のExpです。しかし、Endをぶら下げるべきは、何階層も上のNestなのです。

QueryBuilderでは、 ``end()`` を受け取ると、上に向かってNestを検索します。
しかし見つかった最初のNestが必ずしもお目当てとは限りません。例えば、((A or B) and C)のように括弧が二重になっている場合です。

また、お目当てのNestがない場合もあります。ユーザーが誤ってbegin()を書いてないのにend()を書いた場合です。
前者に対しては、Nest#isClosedメソッドでそのNestにEndがぶら下がっているか、すなわち、括弧が閉じられているかを調べることができます。

括弧が閉じられたNestは無視してさらに上の階層に探索をすればよいのです。
後者については、例外を投げることにしています。これはプログラマーのミスなのです。
この例では、二回ツリーを根っこに向けてたどれば、お目当てのNestにたどり着きます。

このNestにEndをぶらさげます。よって、


 .. sourcecode:: java
    
    q.e("name = 'smith'").and()
        .begin()
            .e(passwd = 'JUkdn38cXIu').or().e("expires > '20080601'")
        .end();


の時点で、以下のような内部表現になるわけです。


 .. sourcecode:: none
    
               [Root]
                 |
               [And]
                | |
       +--------+ +-------------------------------+
       |                                          |
     [Exp]                                     [N e s t]
      exp = {name = 'smith'}                     | |  |
                                              +--+ |  +--+
                                              |    |     |
                                           [Begin] |   [End]
                                                   |
                                                 [O r]
                                                  | |
                                           +------+ +--+
                                           |           |
                                         [Exp]        [Exp]
                  exp = {passwd = 'JUkdn38cXIu'}       exp = {expires > '20080601'}


 ``end()`` の後ろにさらに ``and()`` を付けるとどうなるでしょうか。

現在ユーザーが操作しているのはEndオブジェクトです。
Andは、Nestの上に挿入される必要があります。そしてAndの左オペランドにNestが代入されるのです。

QueryBuilderでは、 ``End#and() , End#or()`` は、そのまま ``Nest#and(), Nest#or()`` を呼ぶようになっています。
 ``Nest#and(), Nest#or()`` はパッケージスコープのメソッドです。
 ``Nest#and()`` では、Expが自らとRootの間にAndを挿入したのと同じ仕組みにより、Andを自らの上の要素として挿入するのです。


------------------------------
条件式を組み立てる
------------------------------

QueryBuilderのAPIを通して入力した条件がどうのようにQueryBuilderの内部で表現されているかを前章でみてきました。

この章では、内部表現からどのように条件式の文字列表現を得ているのかを見ていきます。

前章の図を見ると、Rootから始めて前順走査で内部表現の各構成要素の文字列表現を連結していけば、うまくいきそうです。
どのような文字列表現にするかは、クラス毎に決めることになります。

Expだと、Exp#getExp()で得られる値が文字列表現の候補になります。
Andは" and "でいいでしょう。Beginはどうなるでしょうか。左括弧をあらわすクラスですから"("になります。
Nestはどうでしょうか。Nestは構造上必要なもので、括弧はBeginとEndがあらわしてくれますから実は文字列表現は必要ありません。

QueryBuilderの場合、前順走査の実装にVisitorパターンは非常にうまくフィットします。
 ``QueryBuilderVisitor`` クラスはまさにこの実装クラスです。

このクラスは、走査の過程で上記の文字列表現を次々に連結し、条件式を作りだすのです。


------------------------------
パラメータを列挙する
------------------------------

実はパラメータの列挙もVisitorパターンでうまく実装できます。パラメータが登場するのはExp要素だけなので条件式を組み立てる場合よりもはるかに単純に実装ができるのです。

 ``ParamAccumulateVisitor`` がこの処理を実装しています。

Expを訪れたときのみ、Exp#getExp()で得られる文字列からパラメータ名を抽出し、Exp#getValue()の値とペアにして、Mapに格納するという処理をしているだけなのです。

------------------------------
カスタム Term Visitor を書く
------------------------------

QueryBuilder ではユーザーが TermVisitor を継承しカスタムな TermVisitor を作成して QueryBuilderの機能を拡張することが出来ます。

TermVisitor の仕組みを理解するには Visitor パターンを知っている必要があります。
拡張自体は簡単です。TermVisitor で定義した数個のメソッドを定義すればよいのです。
カスタムな TermVisitor を使うには、QueryBuilder#accept() に、カスタムな TermVisitor を渡します。

 ``QueryBuilder#accept()`` の中では、QueryBuilder が受け取った TermVisitor を使って内部構造を走査し、引数として渡した TermVisitor を返します。

カスタムな TermVisitor では走査の結果を得るために何らかのgetter メソッドを TermVisitor に定義する必要があるでしょう。

 ``QueryBuilder#accept()`` は、引数および返却値の TermVisitor はパラメータ化されているため、キャストは不要です。


------------------------------
式以外の要素
------------------------------

QueryBuilder では上述した条件式の後に、 ``groupBy()`` と ``orderBy()`` を追加で書くことができます。
これはダイレクトに SQL の "group by" と "order by" に相当します。

 ``groupBy()`` と ``orderBy()`` は、Root に直接ぶら下がります。
なぜなら、特定の式の値の下にぶら下げる理由がないからです。

これらはどの式の要素を修飾しているわけではないのです。
実装は簡単です。

Root は Term のリストをもっています。
 ``groupBy(), orderBy()`` は現れた順にこのリストに追加されていくだけです。
ご存知のようにこれらは何度も書けるわけではなく、順番も決まっています。

書ける順番は、流れるようなインターフェースで制御されます。
つまり、 ``groupBy()`` は、 ``e(), end()`` , またはQueryBuilder インスタンスの直後に書けます。

 ``orderBy()`` は、これらに加えて、 ``groupBy()`` の後に書けます。
groupBy() の後には、orderBy() しか書けませんし、orderBy() の後ろには何も書けません。

例を示しましょう。

 .. sourcecode:: java
    
    QueryBuilder q = new QueryBuilder();
    q.exp("division = 'u9'").groupBy("unit").orderBy("id");
    
    String s = q.getQuery(); // where division = 'u9' group by unit order by id
                             // が得られる







