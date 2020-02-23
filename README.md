## common_collection
### 1.作用
构建一个拥有slf4j,gson,fastjson等常用依赖的公共依赖jar包,
统一管理依赖的版本,也避免了每次创建项目都需要重复引入依赖

### 2.成员
- springboot-common
- util-common
- core-common
- restful-common

### 3.打包到github
使用github作为maven仓库,将jar包放上去以后,其他工程引用就简单了.

#### 3.1 deploy
1.在mvn工具的配置文件settings.xml中,添加一个server:
```xml
    <server>
        <id>github</id>
        <username>guihub登录的用户名</username>
        <password>guihub登录的用户密码</password>
    </server>
```
2.mvn clean deploy


#### 3.2 其他项目使用
最外层的pom.xml中引入仓库配置:
```xml
<repositories>
        <repository>
            <id>mvn-repo</id>
            <url>https://raw.github.com/zzzgd/mvn-repo/master</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
```
这个时候再引入common的jar包就可以了