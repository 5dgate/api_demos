#coding=utf-8

__author__ = 'lihengci'

import base64
import md5
import json
import requests

from Crypto import Random
from Crypto.Cipher import PKCS1_v1_5
from Crypto.PublicKey import RSA


# 定义 RSA key 的长度
rsa_key_size = 2048
# 加密 使用 key_size / 8 - 11
encrypt_block = rsa_key_size / 8 - 11
# 解密使用 key_size / 8
decrypt_block = rsa_key_size / 8

'''
* Change Python True to true, False to false
'''
def json_boolean(encrypt):
    if encrypt:
        return 'true'
    else:
        return 'false'

'''
* 按照字典顺序排列参数
'''
def sorted_key(params):
  return sorted(params.items(), key=lambda x: x[0])

'''
* 将 Dict 数据拼接为字符串
'''
def dict_to_string(params):
    return ''.join('{}{}'.format(key, val) for key, val in params.items())

'''
* MD5 签名
* data 待签名数据
* 参数按照字母排序后，进行 MD5 运算
* return 签名
'''
def md5_sign(params):
    params_str = 'account%sdata%s' % (params['account'], params['data'])
    return md5.new(params_str).hexdigest()

'''
* MD5 验签
* return 验签是否通过
'''
def verify_sign(encrypt, data, sign):
    params_str = 'data%sencrypt%s' % (data, json_boolean(encrypt))
    cal_sign = md5.new(params_str).hexdigest()
    if cal_sign.upper() == str(sign):
        return True
    return False

'''
* 使用公钥加密
'''
def rsa_encrypt(params, public_key):
    key = RSA.importKey(public_key)
    cipher = PKCS1_v1_5.new(key)

    raw_str = json.dumps(params)
    raw_str_len = len(raw_str)
    # 计算分段加密的block数 (向上取整)
    block_num = raw_str_len / encrypt_block
    # 余数非0，block数再加1
    if (raw_str_len % encrypt_block) != 0:
        block_num += 1

    ciphertext = ""
    offset = 0
    while offset < raw_str_len:
        chunk = raw_str[offset:offset+encrypt_block]
        ciphertext += cipher.encrypt(chunk)
        offset += encrypt_block
    cipher_text_base64 = base64.b64encode(ciphertext)

    return cipher_text_base64

'''
* 使用私钥解密
'''
def rsa_decrypt(data, private_key):
    # 伪随机数生成器
    random_generator = Random.new().read
    rsakey = RSA.importKey(private_key)
    cipher = PKCS1_v1_5.new(rsakey)

    raw_str = base64.b64decode(data)
    raw_str_len = len(raw_str)
    # 计算分段加密的block数 (向上取整)
    block_num = raw_str_len / decrypt_block
    # 余数非0，block数再加1
    if (raw_str_len % decrypt_block) != 0:
        block_num += 1

    cipher_data = ""
    offset = 0
    while offset < raw_str_len:
        chunk = raw_str[offset:offset+decrypt_block]
        cipher_data += cipher.decrypt(chunk, random_generator)
        offset += decrypt_block

    return cipher_data

'''
* account 客户号
* params  请求参数
* keys_content 加解密的密钥
'''
def do_request(account, params, keys_content, url):
    # RSA 加密请求参数
    cipher_text_base64 = rsa_encrypt(params, keys_content['server_public_key'])
    print('Encrypt data  ' + cipher_text_base64)

    # 计算 md5 sign
    request_sign = md5_sign({'account': account, 'data': cipher_text_base64})
    print('Data sign  ' + request_sign)

    content = {'account': account, 'data': cipher_text_base64, 'sign': request_sign}
    print('Request parameters ' + json.dumps(content, ensure_ascii=False))

    try:
        if 'https' in url:
            resp = requests.post(url, json=content, headers={}, timeout=120.0, verify=False)
        else:
            resp = requests.post(url, json=content, headers={}, timeout=120.0)

        respData = resp.json()
        print(respData)

        if respData is not None:
            encrypt_data = respData['data']
            if verify_sign(respData['encrypt'], encrypt_data, respData['sign']):
                dataStr = rsa_decrypt(encrypt_data, keys_content['user_private_key'])
                print('Decrypt data  ' + dataStr)

                dataObj = json.loads(dataStr)
                print(json.dumps(dataObj, ensure_ascii=False))
                return dataObj
            else:
                print('Sign verify failed')
        else:
            print('Response None')
        return None
    except Exception, err:
        print('Request Exception')
        print err
        return None


def main():
    # TODO 设置 API 请求地址
    api_url = '邮件告知'
    print('api_url  ' + api_url)

    # TODO 设置 account
    account = '邮件告知'
    print('account  ' + account)

    # TODO 设置 productId
    productId = '邮件告知'
    print('productId  ' + productId)

    # 设置 customerId
    customerId = '请求唯一编号'
    print('customerId  ' + customerId)

    # TODO 设置 请求参数
    params = {'name': '姓名', 'mobile': '手机号', 'customerId': '请求唯一编号', 'productId': '产品编号'}
    print('params  ' + json.dumps(params))

    # TODO 设置 加解密的密钥
    # 用户端的私钥
    # 服务端的公钥
    keys_content = { 'user_private_key': '-----BEGIN PRIVATE KEY-----\n   用户私钥内容   \n-----END PRIVATE KEY----- ',
        'server_public_key': '-----BEGIN PUBLIC KEY-----\n   平台公钥内容   \n-----END PUBLIC KEY-----'}

    do_request(account, params, keys_content, api_url)


if __name__ == "__main__":
    main()
