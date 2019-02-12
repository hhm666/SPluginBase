# SPluginBase
A Nukkit plugin that eases the burden on developers. Including language API, commonly used expansion functions.

[中文简介](README_chs.md)

## Example usage

1.Create a new 'lang' folder in your resource folder.

2.Create a new language file in this folder.

  The format is: LanguageCode.yml. 
  
  See the language code in language.list
  
  You should add at least en_US.yml.
  
3.Add the following code to your onLoad/onEnable method.
  
  TITLE is your plugin name.
  
  In the language file,%TITLE represents the TITLE variable here
```kotlin
LanguageUtils.load(this, TITLE)
```
## Sample language file
Please add the name of your plugin to Key words in language files to ensure that it does not duplicate with other plugins.
```yaml
spluginbase.lang.defaultSet: "Your language has been set to %1 by region. If you want to change it, please type /lang list to see the list of languages, then type /lang set <language code> to set the language!"
spluginbase.lang.set.notFound: "No language: %1!"
spluginbase.lang.set.success: "Successfully set the language to %1!"
spluginbase.lang.load.pluginNotFound: "Multi-language loading of plugin %1 failed!"
spluginbase.lang.load.usePluginDefaultLanguage: "Cannot find %2 plugin %1 language configuration file, so use %3 plugin default language configuration file"
spluginbase.lang.load.useEN_US: "Cannot find %2 plugin %1 language configuration file, so use %3 language configuration file"
spluginbase.command.lang.notFound: "Unknown usage, please enter /lang help to see usage"
spluginbase.command.lang.help: "§6---SPluginBase-LanguageSystemHelp---\n§a/lang set <language code> Set language\n§b/lang list View language code"
```