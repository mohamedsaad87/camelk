// camel-k: language=java dependency=camel-quarkus-openapi-java dependency=camel-atlasmap  dependency=mvn:xalan:xalan:2.7.1
// camel-k: trait=jolokia.enabled=true trait=prometheus.enabled=true
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class Hello extends RouteBuilder {
  @Override
  public void configure() throws Exception {

     from("direct:hello")
        .routeId("openapi")
        .log(">>> Headers : ${headers}")
        .to("atlasmap:{{api.resources}}/request.adm")
        .to("log:info")
        .removeHeaders("*")
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/xml"))
        .transform(simple("${body.replace('<echo>', '<echo xmlns=\"http://service.camel.gea.com/\">')}"))
        .to("http://{{backend-ep}}")
        .to("atlasmap:{{api.resources}}/response.adm")
        .wireTap("kafka:{{my.topic}}?")
        .to("log:info"); 

  }
}
