AozoraZip2Mobi
============

説明
------------
青空文庫のzipファイルをePub3ファイルとmobiファイルに変換するツールです。（複数ファイル対応可） 

同一作品はいくつのバージョンがあった場合（新旧仮名漢字）、上書きされるので、一つずつ変換してください。

動作環境
------------
Java 7 / Java 8 の動作環境 ( http://www.java.com/ja/ )  

Windows10以降で動作確認済 


使い方
------------
一、AozoraEpub3をインストールする。
　https://www18.atwiki.jp/hmdev/

二、kindlegenをダウンロードして、AozoraEpub3のインストールパスにコピーする。
　https://www.amazon.com/gp/feature.html?docId=1000765211

三、AozoraZip2Mobi.jarをダウンロードして、AozoraEpub3のインストールパスにコピーする。

四、変換したい青空文庫のzipファイルをダウンロードしてください。
+ 方法一：http://www.aozora.gr.jp　から直接ダウンロード
+ 方法二：https://github.com/aozorabunko/aozorabunko/tree/master/cards　から直接ダウンロード
+ 方法三：https://github.com/aozorabunko/aozorabunko/tree/master/cards　からSVNでダウンロード

　checkoutのURLは「tree/master」を「trunk」に変換するURL：例：https://github.com/aozorabunko/aozorabunko/trunk/cards

五、コマンドラインからの実行
cd AozoraEpub3のインストールパス

java -jar AozoraZip2Mobi.jar 入力フォルダパス  出力先パス 

入力フォルダパス：zipファイルのフォルダ構成は「https://github.com/aozorabunko/aozorabunko」のcardsフォルダを参考してください。

入力フォルダパスは「cards」のパス（cardsが含まれない）

例：入力フォルダパスは「c:\input」の場合、入力フォルダのフォルダ構成は以下のように：
    
<pre>
    input
      |-cards
          |-著者番号
          |-files
              |-作品番号_xxx_xxxx.zip
</pre>

出力先パス：例：出力先パスは「c:\output」の場合、出力ファイルのフォルダ構成は以下のように：
<pre>
    output
      |-books
          |-epub
          |  |-著者名
          |      |-[著者名]作品名.epub
          |-mobi
             |-著者名
                 |-[著者名]作品名.mobi
</pre>
