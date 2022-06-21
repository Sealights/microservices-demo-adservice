# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:18-slim as builder

WORKDIR /app

RUN apt-get -y update && apt-get -y install git

RUN apt-get install wget zip git
RUN wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
RUN unzip -oq sealights-java-latest.zip && rm sealights-java-latest.zip

RUN GRPC_HEALTH_PROBE_VERSION=v0.4.8 && \
    wget -qO/bin/grpc_health_probe https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/${GRPC_HEALTH_PROBE_VERSION}/grpc_health_probe-linux-amd64 && \
    chmod +x /bin/grpc_health_probe

RUN touch build.gradle
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew wrapper 

ARG RM_DEV_SL_TOKEN=local \
SL_OTEL_ENDPOINT="https://dev-risk-management.dev.sealights.co:443" \
IS_PR="" \
TARGET_BRANCH="" \
LATEST_COMMIT="" \
PR_NUMBER="" \
TARGET_REPO_URL=""

ENV RM_DEV_SL_TOKEN=${RM_DEV_SL_TOKEN} \
SL_OTEL_ENDPOINT=${SL_OTEL_ENDPOINT} \
IS_PR=${IS_PR} \
TARGET_BRANCH=${TARGET_BRANCH} \
LATEST_COMMIT=${LATEST_COMMIT} \
PR_NUMBER=${PR_NUMBER} \
TARGET_REPO_URL=${TARGET_REPO_URL}

RUN echo -e "=========================================================\n\
targetBranch: ${TARGET_BRANCH}\n\
latestCommit: ${LATEST_COMMIT}\n\
pullRequestNumber ${PR_NUMBER}\n\
repositoryUrl ${TARGET_REPO_URL}\n\
========================================================="


COPY build.gradle .
RUN ./gradlew downloadRepos

COPY . .

RUN echo "${RM_DEV_SL_TOKEN}" > sltoken.txt
COPY java-agent-bootstrapper-3.0.0-SNAPSHOT.jar sl-test-listener.jar
COPY slgradle.json .

#RUN if [ $IS_PR = 0 ]; then \
#    echo "Check-in to repo"; \
#    java -jar sl-build-scanner.jar -gradle -configfile slgradle.json -workspacepath . ; \
#    BUILD_NAME=$(date +%F_%T) && ./node_modules/.bin/slnodejs config --token $RM_DEV_SL_TOKEN --appname "currencyservice" --branch "master" --build "${BUILD_NAME}" ; \
#else \ 
#    echo "Pull request"; \    
#    java -jar sl-build-scanner.jar -prConfig -token $RM_DEV_SL_TOKEN -appname "adservice"  -targetBranch "${TARGET_BRANCH}" \
#        -latestCommit "${LATEST_COMMIT}" -pullRequestNumber "${PR_NUMBER}" -repoUrl "${TARGET_REPO_URL}" -packagesincluded "*hipstershop.AdService*" -packagesexcluded "*hipstershop.AdServiceGrpc*" ; \  
#fi


RUN java -jar sl-build-scanner.jar -gradle -configfile slgradle.json -workspacepath .
RUN chmod +x gradlew
RUN ./gradlew installDist
RUN ./gradlew test

FROM openjdk:18-slim

ENV JAVA_TOOL_OPTIONS="-Dsl.tags=script,container \
-Dsl.labId=integ_master_813e_SLBoutique \
-javaagent:/app/java-agent-bootstrapper-3.0.0-SNAPSHOT.jar \
-Dsl.otel.enabled=true \
-Dsl.otel.loadAgent=true \
-Dsl.otel.endpoint=${SL_OTEL_ENDPOINT} \
-Dsl.otel.exporterType=grpc"

WORKDIR /app
COPY --from=builder /app .

EXPOSE 9555
ENTRYPOINT ["/app/build/install/hipstershop/bin/AdService"]
