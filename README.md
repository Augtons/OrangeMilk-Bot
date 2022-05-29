# OrangeMilk-Bot

![](https://shields.io/github/languages/top/Augtons/OrangeMilk-Bot)
![](https://shields.io/github/license/Augtons/OrangeMilk-Bot)

基于`mirai`实现，运行在`JVM`平台的机器人`Orange-Milk`。[mirai仓库地址](https://github.com/mamoe/mirai)

> mirai 是一个在全平台下运行，提供 QQ 多种设备协议支持的高效率机器人库

#### 目录
- [一、简介](#一简介)
- [二、部署](#二部署)
  - [(一) 通过源码：](#一-通过源码：)
    - [1. clone本仓库源码](#1-clone本仓库源码)
    - [2. 新建`SpringBoot`配置文件](#2-新建springboot配置文件)
- [三、添加功能](#三添加功能)
- [四、MC风格指令](#四mc风格指令)
  - [(1) 无序传参](#1-无序传参) 
  - [(2) 多类型可变参数](2-多类型可变参数)

## 一、简介
懒得写，不写了
## 二、部署

1. 开发环境：`IntelliJ IDEA` 越新越好

2. 依赖环境：

| 序号  | 环境                |
|-----|-------------------|
| 1   | Java 11+          |
| 2   | node.js (最新LTS即可) |
| 3   | npm               |



### (一) 通过源码：

#### 1. clone本仓库源码
#### 2. 新建SpringBoot配置文件
1. 在`/src/main/resources/`下新建空白文件`application.yml`。或者在项目根目录下直接新建空白文件`application.yml`。<br/>
如果你了解过`SpringBoot`，也可以根据自己的了解放在其他合适的位置。
2. 在这个文件中放入配置项目
```yaml
# ====机器人账户配置====
bot:
  # QQ号、密码
  qq: 这里替换成机器人的QQ号
  password: 这里替换成密码
  # 机器人主人的账号(列表)，若仅有一个则删去一个
  # master的作用是当机器人启动之后，为主人发一条消息
  # 并且用于测试的监听器只为主人绑定（当然你可以手动修改逻辑）
  masters:
    - 主人1的QQ号
    - 主人2的QQ号

# ====机器人应用配置====
botapp:
#  cacheDir: 这里放置缓存目录，此行被注释的时候默认为工作目录下的app_cache文件夹
#  如果你想指定其他位置，请去掉上一行的注释，填入你想要的位置，一般不用改动

# ====媒体====
media:
  sing: # 机器人唱歌功能所需的音频文件目录，此目录下存放mp3,amr等格式的文件
#    path: # 此行被注释时默认为./sings，若无会在机器人运行时自动创建

  music:
    kugou: # 请将signatureGetter替换成酷狗signature签名生成文件的执行命令，如node /path/to/kugou.js
      signatureGetter: "node 酷狗js文件" # 必须填写
    netease: # 请将apiurl替换为网易云API的地址。搭建教程：https://github.com/Binaryify/NeteaseCloudMusicApi
      apiurl: "自己搭建的网易云API的URL地址" # 必须填写
```
本项目所需的配置项就这些了，对于其他的配置比如`SpringBoot`的配置请参考`SpringBoot`相关文章

## 三、添加功能
自定义监听器，与mirai一致，请查阅 [mirai-core文档](https://docs.mirai.mamoe.net/CoreAPI.html)

本项目中监听器均在`listeners/`下

## 四、MC风格指令

本项目对传统机器人“指令”调用方式进行了拓展，MC风格指令。
支持可变参数，多类型传参。因此用户不必纠结死板的指令调用格式。
换句话说：用户不需要关注怎么调，只需要关注要调用啥。

我们来看几个例子：

### (1) 无序传参
如：酷狗音乐搜歌

对发送如下内容 **均可正常识别和调用**

| 序号  | 内容                                  | 备注    |
|-----|-------------------------------------|-------|
| 1   | `/kugou 迈兮 守城记`                     | 正常顺序  |
| 2   | `迈兮 守城记 /kugou`                     | 乱序    |
| 3   | `功夫 /kugou 茶 封茗 囧菌`                 | 乱序    |
| 4   | 闻韶<br/>　音 社 /kugou <br/>　老 <br/>戏　院 | 多行+乱序 |

### (2) 多类型可变参数
如：/diu指令

在机器人所在群发送如下内容 **均可正常识别和调用**

| 序号  | 内容                         | 备注                                   |
|-----|----------------------------|--------------------------------------|
| 1   | `/diu @橘子奶`                | `diu`群友，支持`At`消息和QQ号                 |
| 2   | `@橘子奶 /diu @小红 1234567890` | 同时`diu`三位群友<br/>`1234567890`会被解释成QQ号 |

那么我们想加入自己的MC风格指令怎么办呢，也很简单。
本项目已经为大家编写了一套`Kotlin DSL`来完成这件事。

这个函数一共有5个重载，方便大家使用，请自行查阅源码中JavaDoc说明

```kotlin
// Kotlin
// 注意尖括号里的东西，此函数mcCommand，对应的是传入一个MessageEvent
@McCmd
val kgMusic = mcCommand<GroupMessageEvent> {
    name = "kugou music"  // 指令名字
    prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
    help = "用于酷狗音乐搜歌"
    needArgs = true  // 是否需要参数

    onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

    }
}
```
