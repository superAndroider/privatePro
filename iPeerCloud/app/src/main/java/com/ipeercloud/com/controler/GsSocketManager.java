package com.ipeercloud.com.controler;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: jni调用管理类
 */

public class GsSocketManager {

    private static GsSocketManager instance;

    private GsSocketManager() {

    }

    public static GsSocketManager getInstance() {
        if (instance == null) {
            instance = new GsSocketManager();
        }
        return instance;
    }

    //
    //函数名：helloGoonas
    //功能：返回当前jni库的版本，确定java调用goonas jni库已经成功
    //输入参数：无
    //返回数据：返回当前jni库的版本信息
    //
    public native String helloGoonas();

    //
    //函数名：gsUserRegister
    //功能：注册一个新的用户账户
    //输入参数：server是goonas的域名，根据手机语言中国用sz.goonas.com，其他国家用utah.goonas.com，
    // 			user表示要注册的账户名（有效的邮箱地址），password表示账户的密码（6个字符以上）
    //返回数据：返回 true表示成功，false表示失败
    //
    public native boolean gsUserRegister(String server, String user, String passowrd);

    //
    //函数名：gsChangePassword
    //功能：改变账户密码，
    //输入参数：user表示当前账户名，oldpassword表示旧的密码，newpassword表示新的密码
    //返回数据：返回 true表示成功，false表示失败
    //
    public native boolean gsChangePassword(String user, String oldpassword, String newpassword);

    //
    //函数名：gsResetPassword
    //功能：重置账户密码，goonas会发送重置密码的链接到账户邮箱中，点击后即可重置，用邮箱中的临时密码登录后修改密码。
    //输入参数：server表示goonas的域名，user表示当前要重置密码的账户
    //返回数据：返回 true表示成功，false表示失败
    //
    public native boolean gsResetPassword(String server, String user);

    //
    //函数名：gsLogin
    //功能：登录goonas服务系统，使用后续文件服务必须要先登录goonas
    //输入参数：server表示goonas的域名，user表示当前的账户，password表示当前的账户密码
    //返回数据：返回 true表示成功，false表示失败
    //
    public native boolean gsLogin(String server, String user, String password);

    //
    //函数名：gsLinked
    //功能：返回当前账户是否绑定了一台私有云存储服务器终端
    //输入参数：无
    //返回数据：返回 true表示已经绑定，false表示失败
    //
    public native boolean gsLinked();

    //
    //函数名：gsOnline
    //功能：返回当前账户绑定的私有云存储服务器终端是否在线
    //输入参数：无
    //返回数据：返回 true表示在线，false表示失败
    //
    public native boolean gsOnline();

    //
    //函数名：gsLinkCloudServer
    //功能：绑定一台私有云存储服务器终端到当前的账号
    //输入参数：私有云存储服务器的UUID号，UUID号可以在产品的外包装上找到
    //返回数据：返回 true表示成功，false表示失败
    //
    public native boolean gsLinkCloudServer(String CloudServerUuid);

    //
    //函数名：gsGetPathFile
    //功能：获取指定目录下的所有文件名和目录名等参数
    //输入参数：准备获取数据的目录完全路径名，根目录是“\"
    //返回数据：以json格式返回数据
    //	/// json格式：
    /*
	[
        {
                "FileName": "$360Section",      // 名称
                "FileSize": 0,                  // 文件大小
                "FileType": 0,                  // 类型：对应PATHFILE_TYPE
				"LastModifyTime": 1464752412    // 最后修改时间
        },
        {
                "FileName": "$Recycle.Bin",
                "FileSize": 0,
                "FileType": 0,
				"LastModifyTime": 1464752412
        },
        {
                "FileName": "转账单.xlsx",
                "FileSize": 11842,
                "FileType": 1,
				"LastModifyTime": 1464752412
        }
	]
	*/
    public native String gsGetPathFile(String path);

    //
    //函数名：gsReturnConnectedMode
    //功能：返回当前跟“云服务器”的通信连接模式
    //输入参数：无
    //返回数据：返回 1:为直连模式 2:中转模式 3:局域网LAN模式
    //

    public native int gsReturnConnectedMode();

    //
    //函数名：gsPutFile
    //功能：下载指定的本地文件到“云存储服务器上”指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：返回 1:为直连模式 2:中转模式 3:局域网LAN模式
    //
    public native boolean gsPutFile(String local, String remote);

    //
    //函数名：gsGetFile
    //功能：下载指定的文件到本地指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：为0时成功，为1表明正在进行，<0表明失败
    //
    public native int gsGetFile(String remote, String local);

    //
    //函数名：gsFreePathFile
    //功能：释放gsGetPathFile占用的资源
    //输入参数：无
    //返回数据：返回 无s
    //
    public native void gsFreePathFile();

    //
    //函数名：gsAddWifi
    //功能：为设备增加一组WIFI连接
    //输入参数：wifiName是wifi的名称，密码
    //返回数据：返回逻辑成功与失败
    //
    public native boolean gsAddWifi(String wifiName, String password);

    //
    //函数名：gsReadFileBuffer
    //功能：读取指定的数据到Buffer缓冲区
    //输入参数：remote要读取的远端云存储服务上的文件完全路径（文件名及它的路径），offset指定要从哪个位置开始读取，count指定要读取的字节数，buf指定读取
    //			到哪里，即缓冲区，bufflen返回实际读取到的数量。
    //			bufflen读取数据缓存大小，这个值在函数前需要填写buff的大小，完成会修改成为真实读取的大小。
    //返回数据：返回值，为0时成功，为1表明已经到达结尾，没有数据可读，小于0代表失败,
    //
    public native boolean gsReadFileBuffer(String remote, long offset, int count, byte[] buffer, int[] bufflen);

    public static void main(String[] args) {
        return;
    }

}
