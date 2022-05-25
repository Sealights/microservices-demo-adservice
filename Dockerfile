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

RUN apt-get install wget 
RUN wget -O sl-build-scanner.jar https://oss.sonatype.org/service/local/repositories/releases/content/io/sealights/on-premise/agents/sl-build-scanner/3.1.1993/sl-build-scanner-3.1.1993.jar

COPY ["build.gradle", "gradlew", "./"]
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew downloadRepos

COPY . .
RUN java -jar sl-build-scanner.jar -gradle -configfile slgradle.json -workspacepath .
RUN chmod +x gradlew
RUN ./gradlew installDist

FROM openjdk:18-slim

RUN apt-get -y update && apt-get install -qqy \
    wget \
    && rm -rf /var/lib/apt/lists/*

RUN GRPC_HEALTH_PROBE_VERSION=v0.4.8 && \
    wget -qO/bin/grpc_health_probe https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/${GRPC_HEALTH_PROBE_VERSION}/grpc_health_probe-linux-amd64 && \
    chmod +x /bin/grpc_health_probe

WORKDIR /app
COPY --from=builder /app .
COPY agent/opentelemetry-javaagent.jar .

ENV JAVA_TOOL_OPTIONS="-javaagent:sl-test-listener.jar -Dsl.tags=script,container -Dsl.labId=integ_test_otel"

RUN apt-get update && apt-get install -y procps

EXPOSE 9555
ENTRYPOINT ["/app/build/install/hipstershop/bin/AdService"]
