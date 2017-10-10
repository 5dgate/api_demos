package com.gravity.demo;

/**
 * Copyright (C), 2017, 黑曜石
 *
 * @author jimmy
 * @desc patameter
 * @date 2017/10/2
 */
public class patameter {
    //账户名
    String account;

    //调用url
    String url;

    //请求参数json字符串
    String data;

    //我司加密公钥
    String publicKey;

    //客户解密私钥
    String privateKey;

    public patameter() {
    }

    public patameter(String account, String url, String data, String publicKey, String privateKey,
        String productId, String customerId) {
        this.account = account;
        this.url = url;
        this.data = data;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override public String toString() {
        return "patameter{" + "account='" + account + '\'' + ", url='" + url + '\'' + ", data='"
            + data + '\'' + ", publicKey='" + publicKey + '\'' + ", privateKey='" + privateKey
            + '\'' + '}';
    }
}
