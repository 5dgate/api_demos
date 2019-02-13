5DGate API 对接示例代码.
========

本项目是黑曜石公司 API 接口对接的示例代码。


项目目录说明
========


```
api_demos           项目根目录
    java            Java 示例
    python          Python 示例
    php             PHP 示例
    REAMDME.md      说明文档
```

请根据贵司的项目需要，参考相应语言的示例代码。

请仔细阅读，各目录下的 README.md 说明文档。

各编程语言目录下的示例代码，均通过了我司专业的技术人员的测试。

如您发现示例代码存在 Bug，或有其他诉求，请发邮件至：github@5dgate.com，感谢您的反馈。


RSA 密钥生成步骤
========

推荐在 Linux 系统下，输入下列命令，生成公私钥：


```
# 生成私钥
openssl genrsa -out your_private_key.pem 2048

# 生成公钥
openssl rsa -in app_private_key.pem -pubout -out your_public_key.pem

# Java 开发者，需要将私钥转换成 PKCS8 格式
openssl pkcs8 -topk8 -inform PEM -in app_private_key.pem -outform PEM -nocrypt -out your_private_key_pkcs8.pem
```

请注意：

对于使用 Java 的开发者，将 pkcs8 文件中的私钥去除头尾、换行和空格，作为开发者私钥。

开发者将私钥保留，将公钥通过邮件告知我司，业务人员将客户的私钥，配置到系统内。


联调测试环境说明
========

目前，联调测试环境，为 mock 的假数据。
部分接口，输入的查询项合法，即返回相同的结果。


接口 API 数据安全通信机制图解
========

![数据安全通信机制图解 数据安全通信机制图解](images/https-rsa-workflow.jpg?raw=true)

