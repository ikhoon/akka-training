# akka-training
:orange_book: akka programming notes

* Akka Http Client Connection Level API : [HttpClientSpec.scala](https://github.com/ikhoon/akka-training/blob/master/src/test/scala/http/HttpClientSpec.scala)
  ```scala
    // get content body and unmarshal to string
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val connectionFlow = Http().outgoingConnection("akka.io")
    val bodyFuture = Source.single(HttpRequest(uri = "/"))
    .via(connectionFlow)
    .map{ case r => Unmarshal(r.entity).to[String] }
    .runWith(Sink.head)

    val result: String = Await.result(bodyFuture.flatMap(x => x), Duration.Inf)
    println(result)
  ```
  - Reference : http://doc.akka.io/docs/akka/2.4.7/scala/http/client-side/connection-level.html
