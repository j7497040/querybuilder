======================================
その他の機能
======================================

------------------------
BooleanBuilderの使い方
------------------------

クラス `BooleanBuilder` は、ver1.1から追加された条件を組み立てるためのヘルパークラスです。

QueryBuilderで条件式を組み立てる場合、if文で条件の組み立ての開始が必ずしもない場合があります。

例えば、以下のようなケースがあります。

 .. sourcecode:: java
    
    // 困るケース
    UserDto user = /*...*/;
    QueryBuilder query = new QueryBuilder();
    
    if(StringUtils.isNotEmpty(user.getName())) {
        query.e("name = :name", user.getName());
    }
    
    if(StringUtils.isNotEmpty(user.mailaddress())) {
        // ※全て他の条件式がある場合、and()を付与するなどのような記述方法はできない。
        query.and().e("mailaddress = :mailaddress", user.getMailaddress());
    }

 .. sourcecode:: java
    
    // 今までは、このように記述していた。
    UserDto user = /*...*/;
    QueryBuilder query = new QueryBuilder();
    
    Exp exp = null; // QueryBuilderの途中の式のノードを取得する。
    if(StringUtils.isNotEmpty(user.getName())) {
        if(exp == null) {
            exp = query.e("name = :name", user.getName());
        } else {
            // 既に他の条件式がある場合
            exp = exp.and().e("name = :name", user.getName());
        }
    }
    
    if(StringUtils.isNotEmpty(user.mailaddress())) {
        if(exp == null) {
            exp = query.e("mailaddress = :mailaddress", user.getMailaddress());
        } else {
            // 既に他の条件式がある場合
            exp = exp.and().e("mailaddress = :mailaddress", user.getMailaddress());
        }
    }
    

このような場合、BooleanBuilderを使うと簡単に記述できます。

 .. sourcecode:: java
    
    UserDto user = /*...*/;
    
    BooleanBuilder subQuery = new BooleanBuilder();
    
    if(StringUtils.isNotEmpty(user.getName())) {
        subQuery.and("name = :name", user.getName());
    }
    
    if(StringUtils.isNotEmpty(user.getMailaddress())) {
        subQuery.and("mailaddress = :mailaddress", user.getMailaddress());
    }
    
    QueryBuilder query = new QueryBuilder();
    query.exp(subQuery);


さらに、画面の検索条件でOR/AND条件で組み立てる場合など、for文で簡単に組み立てることもできます。

 .. sourcecode:: java
    
    BooleanBuiilder subQuery = new BooleanBuilder();
    for(String name : list) {
        // 同じ変数が使用できないため、変数に自動的にインデックスを付与してユニークにすることもできます。
        // この場合、「name = :name_1 or name = :name_2」などのように式が組み立てられる。
        subQuery.orByName("name", "=", name);
    }
    
    QueryBuilder query = new QueryBuilder();
    query.exp(subQuery);


-------------------------
OrderBuilderの使い方
-------------------------

クラス `OrderBuilder` は、ver1.1から追加された並び順を組み立てるためのヘルパークラスです。

条件式を、for文で組み立てたり、if文を組み合わせることができます。

 .. sourcecode:: java
    
    OrderBuilder order = new OrderBuilder();
    for(String name : list) {
        order.asc(name);
    }
    
    if(StringUtils.isNotEmpty(path)) {
        order.desc(path);
    }
    
    QueryBuilder query = new QueryBuilder();
    query.orderBy(order);

