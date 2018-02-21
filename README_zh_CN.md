AozoraZip2Mobi
============

説明
------------
本工具用于将从青空文库上下载的zip文件批量转换成ePub和mobi格式的电子书。 

其中有些作品是由于新旧假名和汉字的原因，会出现同名同作者的情况，此时后转换的会将先转换的书覆盖掉，因此如果对版本有需求的人请先针对其单独进行转换。

运行环境
------------
Java 7 / Java 8

Windows10（win7以上应该就没问题） 


使用方法
------------
一、安装AozoraEpub3（用于将青空文库的书转换成epub）

  可设置成竖排版

  https://www18.atwiki.jp/hmdev/

二、下载kindlegen，然后将其复制到AozoraEpub3的安装路径下（亚马逊官方软件，用于将epub转成mobi）
　https://www.amazon.com/gp/feature.html?docId=1000765211

三、在本网页下载AozoraZip2Mobi.jar，然后将其复制到AozoraEpub3的安装路径下

四、下载希望转换的zip文件
+ 方法一：http://www.aozora.gr.jp　直接从青空文库官网下载（只能一个一个下载，效率慢）
+ 方法二：https://github.com/aozorabunko/aozorabunko/tree/master/cards　从青空文库的github项目上下载cards文件夹，里面包含所有的作品
+ 方法三：https://github.com/aozorabunko/aozorabunko/tree/master/cards　使用svn从青空文库的github项目上下载cards文件夹

　checkout时，URL将「tree/master」改成「trunk」：例：https://github.com/aozorabunko/aozorabunko/trunk/cards

五、命令执行

cd AozoraEpub3的安装路径

java -jar AozoraZip2Mobi.jar [-options]

-c -cards 输入路径（「cards」的路径）
-b -books 输出路径（「books」的路径）
-t -convertType 转换类型（1:zip->epub, 2:zip->epub->mobi, 3:epub->mobi）

输入路径：zip文件所在文件夹的构成请参照「https://github.com/aozorabunko/aozorabunko」中cards文件夹下的构成。

输入路径指的是「cards」所在路径（不包括cards）

例：输入路径为「c:\input」，文件夹构成如下：
​    
<pre>
    input
      |-cards
          |-作者号
          |-files
              |-作品号_xxx_xxxx.zip
</pre>

输出路径：例：输出路径为「c:\output」，文件夹构成如下：
<pre>
    output
      |-books
          |-epub
          |  |-作者名
          |      |-[作者名]作品名.epub
          |-mobi
             |-作者名
                 |-[作者名]作品名.mobi
</pre>
