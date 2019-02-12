#SPluginBase
一个可以减轻开发人员负担的nukkit插件。包括语言API，常用的扩展函数。

[English Introduction](README.md)

##示例用法

1.在资源文件夹中创建一个新的“lang”文件夹。

2.在此文件夹中新建语言文件。

格式为：语言代码.yml。

请参见language.list中的语言代码。

你至少应该加上en_US.yml。

3.将以下代码添加到您的onLoad/onEnable方法中。

标题是你的插件名。

在语言文件中，%title表示此处的标题变量
```kotlin
LanguageUtils.load(this, TITLE)
```
##示例语言文件
请将插件的名称添加到语言文件中的关键字中，以确保它不会与其他插件重复。

就像下面的spluginbase.xx.xx.xx
```yaml
spluginbase.lang.defaultSet: "你的语言已经根据地区设置为%1,如果想更改,请输入/lang list来查看语言列表,然后输入/lang set <语言代码> 来设置语言!"
spluginbase.lang.set.notFound: "没有语言:%1!"
spluginbase.lang.set.success: "成功设置语言为%1!"
spluginbase.lang.load.pluginNotFound: "插件%1的多语言加载失败!"
spluginbase.lang.load.usePluginDefaultLanguage: "无法找到%2的插件%1语言配置文件,因此使用%3的插件默认语言配置文件"
spluginbase.lang.load.useEN_US: "无法找到%2的插件%1语言配置文件,因此使用%3语言配置文件"
spluginbase.command.lang.notFound: "未知用法,请输入/lang help来查看用法"
spluginbase.command.lang.help: "§6---SPluginBase-LanguageSystemHelp---\n§a/lang set <语言代码> 设置语言\n§b/lang list 查看语言代码"
```