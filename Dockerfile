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

ARG RM_DEV_SL_TOKEN=local
ARG RM_DEV_SL_TOKEN=local
ARG IS_PR=""
ARG TARGET_BRANCH=""
ARG LATEST_COMMIT=""
ARG PR_NUMBER=""
ARG TARGET_REPO_URL=""

ENV RM_DEV_SL_TOKEN ${RM_DEV_SL_TOKEN}
ENV RM_DEV_SL_TOKEN ${RM_DEV_SL_TOKEN}
ENV IS_PR ${IS_PR}
ENV TARGET_BRANCH ${TARGET_BRANCH}
ENV LATEST_COMMIT ${LATEST_COMMIT}
ENV PR_NUMBER ${PR_NUMBER}
ENV TARGET_REPO_URL ${TARGET_REPO_URL}

RUN echo "========================================================="
RUN echo "targetBranch: ${TARGET_BRANCH}"
RUN echo "latestCommit: ${LATEST_COMMIT}"
RUN echo "pullRequestNumber ${PR_NUMBER}"
RUN echo "repositoryUrl ${TARGET_REPO_URL}"
RUN echo "========================================================="

WORKDIR /app

RUN apt-get -y update && apt-get -y install git

RUN apt-get install wget 
RUN wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
RUN apt-get -y install zip 
RUN unzip -oq sealights-java-latest.zip
RUN rm sealights-java-latest.zip

COPY ["build.gradle", "gradlew", "./"]
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew downloadRepos

COPY . .

RUN if [ $IS_PR = 0 ]; then \
    echo "Check-in to repo"; \
    echo '{ "token": "'$RM_DEV_SL_TOKEN'", "createBuildSessionId": true, "appName": "adservice", "branchName": "master", "buildName": "'$(date +%F_%T)'", "packagesIncluded": "*hipstershop.AdService*", "packagesExcluded": "*hipstershop.AdServiceGrpc*", "testTasksAndStages": {"test": "Unit Tests"}}' > slgradle.json ; \
    java -jar sl-build-scanner.jar -gradle -configfile slgradle.json -workspacepath . ; \    
else \ 
    echo "Pull request"; \    
    java -jar sl-build-scanner.jar -prConfig -token $RM_DEV_SL_TOKEN -appname "adservice"  -targetBranch "${TARGET_BRANCH}" \
        -latestCommit "${LATEST_COMMIT}" -pullRequestNumber "${PR_NUMBER}" -repoUrl "${TARGET_REPO_URL}" -packagesincluded "*hipstershop.AdService*" -packagesexcluded "*hipstershop.AdServiceGrpc*" ; \  
fi

RUN chmod +x gradlew
RUN ./gradlew installDist
RUN ./gradlew test

FROM openjdk:18-slim

ENV JAVA_TOOL_OPTIONS="-javaagent:sl-test-listener.jar -Dsl.tags=script,container -Dsl.labId=integ_master_813e_SLBoutique"

RUN apt-get -y update && apt-get install -qqy \
    wget \
    && rm -rf /var/lib/apt/lists/*

RUN GRPC_HEALTH_PROBE_VERSION=v0.4.8 && \
    wget -qO/bin/grpc_health_probe https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/${GRPC_HEALTH_PROBE_VERSION}/grpc_health_probe-linux-amd64 && \
    chmod +x /bin/grpc_health_probe

WORKDIR /app
COPY --from=builder /app .
COPY agent/opentelemetry-javaagent.jar .

EXPOSE 9555
ENTRYPOINT ["/app/build/install/hipstershop/bin/AdService"]
