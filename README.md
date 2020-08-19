Experimenting deploying a gRPC service as part of a CloudFlow app.

Needs cloudflow from https://github.com/lightbend/cloudflow/pull/642

Parts:
* `src/main/protobuf/helloworld.proto` the protocol
* `src/main/scala/GreeterServiceImpl` the service implementation
* `src/main/scala/SensorDataIngress` the ingress that hooks the service implementation into cloudflow

Notable:
* Only tested locally so far

Testing:
* `sbt runLocal`
* `grpcurl -plaintext -d '{"name":"Joseph"}' localhost:3000 helloworld.GreeterService.SayHello`
