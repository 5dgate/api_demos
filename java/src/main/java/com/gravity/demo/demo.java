package com.gravity.demo;

import com.alibaba.fastjson.JSONObject;
import com.gravity.util.HttpRequest;
import com.gravity.util.MD5Util;
import com.gravity.util.RSAUtil;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Copyright (C), 2017, 黑曜石
 *
 * @author jimmy
 * @desc main
 * @date 2017/10/2
 */
public class demo {
    private static Logger LOGGER = Logger.getLogger(RSAUtil.class);
    public static void main(String[] args) {
        patameter patameter = new patameter();
        //@TODO 填入客户请求账户
        patameter.setAccount("your accout");
        //@TODO 填入api接口地址
        patameter.setUrl("api host");
        //@TODO 填入我方对外公钥
        patameter.setPublicKey("public key");
        //@TODO 填入客户解密私钥
        patameter.setPrivateKey("private key");
        //@TODO 构造请求体
        patameter.setData("{ \"customerId\": \"客户请求唯一编号\", \"productId\": \"产品编号，邮件告知\", \"name\": \"请求参数：姓名\"}");
        try {
            sendMessage(patameter);
        } catch (Exception e) {
            LOGGER.error("请求调用错误，" + e);
        }
    }

    private static void sendMessage(patameter patameter) throws Exception {
        String data = new RSAUtil().encrypt(patameter.getPublicKey(), patameter.getData());
        String account = patameter.getAccount();
        String sign = MD5Util.encrypt("account" + account + "data" + data);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("data", data);
        jsonObject.put("sign", sign);
        String response = HttpRequest.post(patameter.getUrl(), jsonObject.toJSONString());

        JSONObject parse = (JSONObject) JSONObject.parse(response);
        if (null != response) {
            LOGGER.info("返回结果：" + response);
            StringBuilder sb = new StringBuilder();
            sb.append("data").append(parse.get("data")).append("encrypt").append(parse.get("encrypt"));
            String resultSign = MD5Util.encrypt(sb.toString());
            if(resultSign.equals(parse.get("sign"))) {
                LOGGER.info("验证返回签名正确");
                String decrypt = new RSAUtil().decrypt(patameter.getPrivateKey(), parse.get("data").toString());
                LOGGER.info("解密结果：" + decrypt);
            } else {
                LOGGER.error("验签返回签名错误，数据可能被篡改");
            }
        } else {
            LOGGER.error("返回结果为空");
        }
    }
}
