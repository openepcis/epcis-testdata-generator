# How To Deploy Event Generator as Azure Function

## Prerequisites:

- Maven >= 3.9
- An Azure Account. Free accounts work.
- Azure CLI Installed
- Azure Functions Core Tools version 4.x
- A way to build a quarkus native executable (Graal installed or via docker build)

## Build quarkus-app for Azure Deployment

PREPARE: Setup Azure Function specific Quarkus HTTP Configuration 

```bash
# setup Azure compatible root path 
export QUARKUS_HTTP_ROOT_PATH=/api/epcis-event-generator
```

RECOMMENDED: use docker native build

```bash
# run maven build via docker build (docker or podman required!)
mvn -DskipTests=true -Pci-build package -Dnative \
         -Dquarkus.native.container-build=true \
         -f ../quarkus/quarkus-app/pom.xml
```

OR: use local GraalVM (>=21.0.2) and Linux OS required

```bash
# check java vm
java --version
#> openjdk 21.0.2 2024-01-16
#> OpenJDK Runtime Environment GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30)
#> OpenJDK 64-Bit Server VM GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30, mixed mode, sharing)


# run maven build via local GraalVM (Linux OS required!)
mvn -DskipTests=true -Pci-build package -Dnative \
         -f ../quarkus/quarkus-app/pom.xml
```

FINALLY: copy binary build to azure-function deployment application file 

```bash
         
# copy binary from build to 'application'
cp ../quarkus/quarkus-app/target/testdata-generator-quarkus-rest-app-runner application
```

## Deployment

### Create Function App in Azure

To deploy you’ll need to create an Azure Group and Function Application.

```bash
# login
$ az login

# create an Azure Resource Group
az group create -n rg-openepcis-functions \
-l eastus

# create an Azure Storage Account (required for Azure Functions App)
az storage account create -n sargopenepcisfunctions \
-g rg-openepcis-functions \
-l eastus

# create an Azure Functions App
az functionapp create -n yourunique-epcis-event-hash-app-name \
-g rg-openepcis-functions \
--consumption-plan-location eastus\
--os-type Linux \
--runtime custom \
--functions-version 4 \
--storage-account sargopenepcisfunctions 
```

*Make sure that the os-type is Linux*

**Note that your Function Application name must be unique and may collide with others.**


#### Deploy Function to Azure

Now you can deploy your application, make sure you are in the azure-function directory!

```bash
func azure functionapp publish yourunique-epcis-event-hash-app-name
```
