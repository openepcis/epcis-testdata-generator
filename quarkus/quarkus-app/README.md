# testdata-generator-rest-api

## create native docker image

### default amd64

```console
mvn package -Pnative -Dquarkus.container-image.build=true -Dquarkus.native.container-build=true -Dquarkus.container-image.name=testdata-generator-native    
```

### aarch64

```console
mvn package -Pnative -Dquarkus.container-image.build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:22.1-java17-arm64 -Dquarkus.jib.base-native-image=quay.io/quarkus/quarkus-micro-image:2.0-arm64 -Dquarkus.native.container-build=true -Dquarkus.container-image.name=testdata-generator-native-arm64    
```