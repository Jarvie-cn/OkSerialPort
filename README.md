# OkSerialPort
### OkSerialPort 一个灵活简单易用的串口通讯框架，基于谷歌API SerialPort 结合RxJava和基于注解 使用APT技术，支持485 232，让使用者无需在繁琐的组装数据，大大提升开发效率。
 
### OkSerialPort 1.0版本 由于时间问题，所以框架还是存在很多可以改进的地方，有任何问题或建议欢迎交流：QQ:877867730
# 打算使用OkSerialPort前需要知道的：
### 目前1.0版本 协议开头为帧头 最后为校验位（目前校验码支持 异或和CRC16），不支持取消
 
### 帧头   ...  自由配置  ... 校验码
 
### 如果你的协议规则和这个有出入，不建议使用
 
# 使用
### 准备工作 

### 拷贝serialport-release.aar到app工程的lib文件夹（文件在项目app-lib目录有，可以直接下载）

### 添加依赖 
 
```javascript 
 
  defaultConfig {
        
        //因为框架使用了注解，所以需要加上这个，不加会报错
        javaCompileOptions { annotationProcessorOptions { includeCompileClasspath = true } }
    }

repositories {
	flatDir { dirs 'libs' }
    	maven { url 'https://jitpack.io' }
}

dependencies {
   /**因为框架异步使用的RxJava,所以需要先依赖RxJava,如果你的项目中已经依赖，就不需要在依赖RxJava了*/
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
    implementation 'io.reactivex.rxjava2:rxjava:2.1.14'
    implementation 'com.jakewharton.rxbinding2:rxbinding:2.1.1'

	/**串口*/
    implementation(name: 'serialport-release', ext: 'aar')
	/**主角 串口通讯框架*/
    implementation 'com.github.Jarvie-cn:OkSerialPort:1.1.5'
}
``` 
### 到目前为止就已经准备就绪了，接下来可以开始使用框架了

### 根据自己的协议配置协议
### 示例协议图片：
http://tiebapic.baidu.com/forum/pic/item/c96733d12f2eb938fc782b8bc2628535e5dd6f36.jpg

### 上面图片是示例协议规则，那么根据这份协议规则如何配置，直接看代码
### 注解字段说明：
###### @Protocol(index = 0, length = 1, value = (byte) 0x3B)

######	index = 协议字段的顺序位置从0开始（这个是必须传的）
######	length = 协议字段的字节长度（如果字节长度不固定的，就不传，比如数据）
######	value = byte类型 协议字段的数据值，有些相对固定的值可以直接填入，而不确定的就不需要，比如命令码，数据，流水号，校验码等，不填入的话默认会使用-1当占位符，在发送的时候！千万记住！！按顺序！！将实际数据进行填充替换掉占位符数据
###### 占位符数据填充替换的原理类似于数据库使用原理一样：
```javascript 
String aql =  “select name =? form table” ;
String[] args = new String[]{"张三"};
db.execSQL(sql,args) ;
```
#######  这里提一点，该框架目前版本：数据，命令码，流水号，校验码这几字段value不传，发送数据时会自动填充数据，其他自定义字段如果value没有赋值都需要在发送时填充数据

#### 继续往下看，如何根据自身的协议规则来配置协议
```javascript 

/** 按照协议顺序配置  这里注意一下帧头，不管你的协议规定帧头多少个字节都需要一个字节一个字节配置，后面在指定帧头位数*/
    /**
     * 帧头
     */
    @Protocol(index = 0, length = 1, value = (byte) 0x3B)
    public  byte frameHeader ;

    @Protocol(index = 1, length = 1, value = (byte) 0xB3)
    public  byte frameHeader2 ;

    /**
     * 原地址
     */
    @Protocol(index = 2, length = 1, value = (byte) 0x00)
    public  int rawAddress;


    /**
     * 目标地址，示例协议里面目标地址每次发送命令都会不一样，所以 value就不传值，默认会先用-1当占位数据，在发送的时候
     *需要真实的目标地址数据进行填充
     *在强调一次，该框架目前版本：数据，命令码，流水号，校验码这几字段value不传，发送数据时会自动填充数据，其他自定义字段如果value没有赋值都需要在发送时填充数据
     */
    @Protocol(index = 3, length = 1)
    public int deviceAddress  ;

    /**
     * 数据长度 这里注意，如果你也有这规则字段那么传值和我一样传，我这份协议协的数据长度是包含命令吗+版本协议+数据，
     * 等于是 1+1+n 前面两个字节长度是固定的所以传2，如果你是单数据的话那么传0
     *
     */
    @Protocol(index = 4, length = 1,value = 2)
    public int dateNumber;


    /**
     * 命令码
     */
    @Protocol(index = 5, length = 1)
    public  int command ;


    /**
     * 协议版本
     */
    @Protocol(index = 6,length = 1, value = (byte) 0x10)
    public  int dealVersions;

    /**
     * 数据
     */
    @Protocol(index = 7)
    public  int data ;

    /**
     * 异或字节
     */
    @Protocol(index = 8, length = 1)
    public  int OXR ;


/** ---- end  按照文档协议配置结束 这部分根据自身协议自由配置   -----*/

/**  -----  分割线    --------------*/


/**  -----  下面这块注意！！，是必须要配置的    --------------*/

   /**
     * 读取数据长度这个字段字节开始位置角标从0开始,示例文档是在第5位，如果你的协议没有数据长度这个字段那么传-1
     */
    @Protocol(dataLenFirst = 4)
    public int dataLenStart;

    /**
     * 数据长度字段在你配置协议对应的角标位置（就是上面index的值） ，如果你的协议没有数据长度这个字段那么传-1
     */
    @Protocol(dataLenIndex = 4)
    public int dataLenIndex;

    /**
     * 读取数据这个字段开始字节位置,示例文档是第8位，如果你的协议没有数据这个字段那么传-1
     */
    @Protocol(dataFirst = 7)
    public int dataFirst ;

    /**
     * 读取命令码字节开始开始,例文档是第6位，如果你的协议没有这个字段那么传-1
     */
    @Protocol(commandFirst = 5)
    public int commandFirst ;

    /**
     * 命令码字段在你配置协议对应的角标位置（就是上面index的值），如果你的协议没有这个字段那么传-1
     */
    @Protocol(commandIndex = 5)
    public  int commandIndex ;

    /**
     * 流水号起始位置 如果你的协议没有流水号这个字段那么传-1
     */
    @Protocol(runningNumberFirst = -1)
    public int runningNumberFirst ;


    /**
     * 帧头字节数
     */
    @Protocol(frameHeaderCount = 2)
    public static int frameHeaderCount;
    /**
     * 校验码规则  0表示异或校验  1表示CRC16校验
     */
    @Protocol(checkCodeRule = 0)
    public static int checkCodeRule;


    /**
     * 通信协议最短字节不包含数据域
     *比如示例的：（帧头(2字节)	+源地址(1字节)+目标地址(1字节)+数据长度(1字节)+命令码(1字节)+数据(n字节)+异或校验(1字节)）
     * 帧头	    源地址      目标地址	数据长度  命令码    协议版本   	数据      异或校验
     * 2个字节	1个字节	    1个字节  	1个字节	 1个字节	1个字节    n个字节    1个字节
     */
    @Protocol(minDalaLen = 8)
    public static int minDalaLen;

/**  ----- end 分割线    --------------*/

``` 

### 如果你的是232有心跳主动上传的，那么添加配置心跳命令的方式有两种自行选择一种就行了：
#### 1，注解的形式添加：
```javascript
/**  -----  如果你的是232有心跳上传，可以添加N个  --------------*/
    /**
     * 心跳命令1
     */
    @Protocol(heartbeatCommand = (byte) 0x33)
    public int heartbeatCommand1;
    /**
     * 心跳命令2
     */
    @Protocol(heartbeatCommand = (byte) 0xB3)
    public int heartbeatCommand2;
```
#### 2，在打开串口方法中传入心跳命令集合：


### 我们的协议规则已经配置完毕了，接下来进入下一步

### 绑定协议规则
```javascript
OkSerialPort_Protocol.bind();
```

#### OkSerialPort_Protocol这个API是编译后才生成的，绑定协议规则要放在使用串口之前

## 打开串口
```javascript
/**使用建造者模式，灵活配置参数*/
OkSerialport.getInstance().open(new SerialPortParams.Builder()
                .addDeviceAddress("串口地址")//默认/dev/ttyS0
                .addBaudRate(波特率)//默认115200
                .addHeartCommands(心跳命令集合)
                .isReconnect(是否自动重连)//默认false
                .callback(new SerialportConnectCallback() {
                    @Override
                    public void onError(ApiException apiException) {

                    }

                    @Override
                    public void onOpenSerialPortSuccess() {
                        Log.e("ljw", "onOpenSerialPortSuccess" );

                    }

                    @Override
                    public void onHeatDataCallback(DataPack dataPack) {
                        String command = ByteUtil.bytes2HexStr(dataPack.getCommand());
                        Log.e("ljw", "心跳上来的命令：" + command + "，对应的数据 = " + ByteUtil.bytes2HexStr(dataPack.getData()));
                    }
                })
                .build());
```

## 发送命令
#### 所有字符串数据格式全部必须为16进制，字节数必须准确
```javascript
/**
     * @param fillDatas          填充占位数据（在配置协议规则时value没有赋值的）位置顺序从小到大不能错！！没有则传null 
     * @param data               数据
     * @param sendCommand        命令
     * @param sendResultCallback 发送回调
     */
     String[] fillDatas = new String[]{"00"};//00就是配置协议时目标地址的实际数据值
 OkSerialport.getInstance().send(fillDatas,"0003",命令码,new SendResultCallback() {
            @Override
            public void onStart(CmdPack cmdPack) {
                
            }

            @Override
            public void onSuccess(DataPack dataPack) {
	    //这里是子线程，如果有刷新UI的动作，记得切换回UI线程
               Log.e("数据" +dataPack.getData() )
          }

            @Override
            public void onFailed(BaseSerialPortException dLCException) {
                
            }
        });
```
### 一些其他方法
```javascript
//设置发送超时时间
setmSendOutTime();
//设置读取超时时间
setmWaitOutTime();
//关闭串口
close();
//打开调试日志
OkSerialPortLog.isDebug = true;
```
