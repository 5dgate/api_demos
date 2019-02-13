<?php

if (! function_exists('url_safe_base64_encode')) {
    function url_safe_base64_encode ($data) {
        return base64_encode($data);
    }
}

if (! function_exists('url_safe_base64_decode')) {
    function url_safe_base64_decode ($data) {
        return base64_decode($data);
    }
}

class RsaUtil
{
    const CHAR_SET = "UTF-8";
    const BASE_64_FORMAT = "UrlSafeNoPadding";
    // const RSA_ALGORITHM_KEY_TYPE = OPENSSL_KEYTYPE_RSA;
    const RSA_ALGORITHM_KEY_TYPE = OPENSSL_PKCS1_PADDING;
    const RSA_ALGORITHM_SIGN = OPENSSL_ALGO_SHA256;

    protected $public_key;
    protected $private_key;
    protected $key_len;

    public function __construct($pub_key, $pri_key)
    {
        $this->public_key = $pub_key;
        $this->private_key = $pri_key;

        $pub_id = openssl_get_publickey($this->public_key);
        // $this->key_len = openssl_pkey_get_details($pub_id)['bits'];
        $this->key_len = 2048;
    }

    /*
     * 公钥加密
     */
    public function publicEncrypt($data)
    {
        $encrypted = '';
        $part_len = $this->key_len / 8 - 11;
        $parts = str_split($data, $part_len);

        foreach ($parts as $part) {
            $encrypted_temp = '';
            openssl_public_encrypt($part, $encrypted_temp, $this->public_key);
            $encrypted .= $encrypted_temp;
        }

        return url_safe_base64_encode($encrypted);
    }

    /*
     * 私钥解密
     */
    public function privateDecrypt($encrypted)
    {
        $decrypted = "";
        $part_len = $this->key_len / 8;
        $base64_decoded = url_safe_base64_decode($encrypted);
        $parts = str_split($base64_decoded, $part_len);

        foreach ($parts as $part) {
            $decrypted_temp = '';
            openssl_private_decrypt($part, $decrypted_temp,$this->private_key);
            $decrypted .= $decrypted_temp;
            echo $decrypted_temp;
        }
        return $decrypted;
    }
}

class HttpUtil
{
    protected $curl;

    public function __construct($url)
    {
        $headers[] = 'Content-Type: application/json ';
        $this->curl = curl_init();
        curl_setopt($this->curl, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($this->curl, CURLINFO_HEADER_OUT, TRUE);
        curl_setopt($this->curl, CURLOPT_POST, true);//传输方式
        curl_setopt($this->curl, CURLOPT_RETURNTRANSFER, true);//返回的内容作为变量储存，而不是直接输出
        curl_setopt($this->curl, CURLOPT_SSL_VERIFYPEER, false);//不验证证书
        curl_setopt($this->curl, CURLOPT_SSL_VERIFYHOST, false);
        // curl_setopt($this->curl, CURLOPT_HEADER,0);//过滤http头
        curl_setopt($this->curl, CURLOPT_URL, $url);
    }

    public function getResponse($arr)
    {
        curl_setopt($this->curl, CURLOPT_POSTFIELDS, $arr);
        $result = curl_exec($this->curl);
        curl_close($this->curl);
        return $result;
    }
}

// TODO: 解密私钥
$rsaPriKey = <<<EOF
-----BEGIN RSA PRIVATE KEY-----
-----END RSA PRIVATE KEY-----
EOF;
// TODO: 五维公钥
$rsaPubKey = <<<EOF
-----BEGIN PUBLIC KEY-----
-----END PUBLIC KEY-----
EOF;

$ras = new RsaUtil($rsaPubKey, $rsaPriKey);

// TODO: 请求地址
$http = new HttpUtil('http://');

// TODO: 用户账号名
$account = "";
// TODO: 请求参数
$request_data =[
        'productId' => 'Y0107',// 产品编号
        'customerId' => time(),// 请求流水号
        'name' => '张三', // 姓名
        'card' =>'6214830178929305', //银行卡卡号
        'cid' =>'410527199109150614', //身份证号
        'mobile' =>'15201123501',
];

$js=json_encode($request_data,JSON_UNESCAPED_UNICODE);

$encrypted = $ras->publicEncrypt($js);
$data = 'account'.$account.'data'."$encrypted";
$datas = md5($data);

$request=[
        'account'=>$account,
        'data' =>  $encrypted,
        'sign' => $datas,
];

$arr = json_encode($request);

$result = $http->getResponse($arr);

if (empty($result)){
    var_dump('返回结果为空');
    die;
}
$result_data = json_decode($result, true);
$array_data = $result_data['data'];//加密内容
$decrypted = $ras->privateDecrypt($array_data);// 解密返回结果
var_dump('原始返回数据：');
var_dump($result_data);
var_dump('解密后数据：');
var_dump($decrypted);
die;

