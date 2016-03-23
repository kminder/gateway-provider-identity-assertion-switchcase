# Knox Switch Case Identity Asserter
This example project show how to create a simple Knox identity assertion provider.
In this case the identtiy assertion provider can change the case of the user or group to upper or lower case based on configuration.
This can be useful if there are case differences between your identity store and other components in your Hadoop ecosystem.

### 1. Build the identity asserter.
Clone the repo and build the identity asserter from source with this command.

    mvn package

### 2. Install the identity asserter.
Install the identity asserter into Knox extensions directory.
The <GATEWAY_HOME> value below represents the directory where you have Knox installed.

    cp target/gateway-provider-identity-assertion-switchcase-1.0-SNAPSHOT.jar <GATEWAY_HOME>/ext

### 3. Configure the identity asserter.
The identity asserter is activated and configured by changing configuration in a topology files.
These can be found in <GATEWAY_HOME>/conf/topologies.
Normally these topologies have a default identity asserter provider configured.
You will see these with a \<role>identity-assertion\</role> and \<name>Default\</name>.
This new provider is configured as shown below.
Possible values for the parameter user and groups are "upper", "lower" and "none".
The default value is "lower".

    <provider>
        <role>identity-assertion</role>
        <name>SwitchCase</name>
        <param name="user" value="upper"/>
        <param name="group" value="upper"/>
        <enabled>true</enabled>
    </provider>

### 4. Restart Knox
Restart Knox for the installation of the new identity asserter to take effect.

### 5. Test
Test by accessing any REST API.
The example below uses the WebHDFS REST API.

    curl -iku 'tom:tom-password' 'https://localhost:8443/gateway/test/webhdfs/v1?op=GETHOMEDIRECTORY'

The output below is from the gateway-audit.log file (<GATEWAY_HOME>/logs/gateay-audit.log).
Here you can see that the identity was mapped from tom to TOM and the group from analyst to ANALYST.
```
16/03/23 15:29:29 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS||||access|uri|/gateway/test/webhdfs/v1?op=GETHOMEDIRECTORY|unavailable|
16/03/23 15:29:29 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|||authentication|uri|/gateway/test/webhdfs/v1?op=GETHOMEDIRECTORY|success|
16/03/23 15:29:29 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|||authentication|uri|/gateway/test/webhdfs/v1?op=GETHOMEDIRECTORY|success|Groups: [analyst]
16/03/23 15:29:29 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|TOM||identity-mapping|principal|tom|success|
16/03/23 15:29:29 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|TOM||identity-mapping|principal|TOM|success|Groups: [ANALYST]
16/03/23 15:29:30 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|TOM||dispatch|uri|http://localhost:50070/webhdfs/v1/?op=GETHOMEDIRECTORY&user.name=TOM|failure|
16/03/23 15:29:30 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|TOM||dispatch|uri|http://localhost:50070/webhdfs/v1/?op=GETHOMEDIRECTORY&user.name=TOM|unavailable|
16/03/23 15:29:30 ||90d4904f-fa42-4bc3-a192-7ae84eb36a92|audit|WEBHDFS|tom|TOM||access|uri|/gateway/test/webhdfs/v1?op=GETHOMEDIRECTORY|failure|
```