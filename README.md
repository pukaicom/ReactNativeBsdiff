# ReactNativeBsdiff
da 结合ReactNative bundle的基本特点 使用bsdiff 合成实现 reactNative bundle的 拆分和合成 
da 可以使用 bsdiff的源代码（C语言）使用ndk 自己编译so 
da demo里面有server的源代码可在mac os eclipse下直接运行 将需要做查分的文件 放进去即可生成差分包 
da module.json 是对每个业务bundle的 定义 程序运行时会预先去解析bundle的内容然后在本地合成相应的业务bundle
da DDDD
