#/bin/bash

service_name=$1
resource_group=$2
mysql_username=$3
mysql_password=$4

mvn clean package
cd dotnet/count/ && dotnet.exe build && cd -

az spring-cloud app create --name weather -s ${service_name} -g ${resource_group}
az spring-cloud app update --is-public true -n weather -s ${service_name} -g ${resource_group}
az spring-cloud app create --name count -s ${service_name} -g ${resource_group}
az spring-cloud app create --name access-stat -s ${service_name} -g ${resource_group}

az spring-cloud app binding mysql add --app access-stat --database-name access-stat --username ${mysql_username} \
    --key ${mysql_password} --resource-id /subscriptions/799c12ba-353c-44a1-883d-84808ebb2216/resourceGroups/panli/providers/Microsoft.DBforMySQL/servers/ipa-bug-bash \
    -g ${resource_group} -s ${service_name} -n mysql-binding

az spring-cloud app deploy -n weather --jar-path ./weather/target/weather-0.0.1-SNAPSHOT.jar -s ${service_name} -g ${resource_group}
az spring-cloud app deploy -n access-stat --jar-path ./access-stat/target/access-stat-0.0.1-SNAPSHOT.jar -s ${service_name} -g ${resource_group}
az spring-cloud app deploy -n count --runtime-version NetCore_31 --main-entry Microsoft.Azure.SpringCloud.Sample.Count.dll \
    --artifact-path dotnet/count/count.zip -s ${service_name} -g ${resource_group}

