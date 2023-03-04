package org.young.irpc.framework.core.common.rpc;

import lombok.Data;
import org.young.irpc.framework.core.common.constant.RpcConstants;

import java.io.Serializable;


/**
 * @ClassName RPCProtocol
 * @Description 通过自定义协议，定义报文传输的格式，就是报文传输的最小单位
 * @Author young
 * @Date 2023/1/20 上午9:01
 * @Version 1.0
 **/
@Data
public class RpcProtocol implements Serializable {

    /**
     * 对于协议来说，开头为标志（MAGIC_NUMBER）_short =》 2Bytes
     * 接下来是长度 int => 4 bytes
     */

    /**
     * Alt + Enter 以生成版本序列唯一ID。
     * 与UUID有区别
     */
    private static final long serialVersionUID = 2420724557226631489L;

    /**
     * 用于安全检测，看是不是这个协议，服务端/客户端应当统一
     */
    private short magicNumber = RpcConstants.MAGIC_NUMBER;

    /**
     * 不确定，为什么Protocol应该是通用协议，这里还要做成类？
     * 答:这里就是协议类，其实例就是单词保温
     * 下面是内容长度，后续可能根据长度作出相应处理
     */
    private int contentLength;

    /**
     * 请求的服务名称，请求服务的方法名称，请求参数内容，所有正式
     */
    private byte[] content;


    /**
     * 构造函数，传入报文字符数组
     * @param content RPC请求报文数组
     */
    public RpcProtocol(byte[] content) {
        this.content = content;
        this.contentLength = this.content.length;
    }
}
