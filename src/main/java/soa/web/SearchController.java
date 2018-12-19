package soa.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class SearchController {

  private final ProducerTemplate producerTemplate;

  @Autowired
  public SearchController(ProducerTemplate producerTemplate) {
    this.producerTemplate = producerTemplate;
  }

  @RequestMapping("/")
  public String index() {
    return "index";
  }


  @RequestMapping(value = "/search")
  @ResponseBody
  public Object search(@RequestParam("q") String q) {
    Map<String,Object> headers = new HashMap<>();

    int maxLimit = -1;
    if (q.contains("max:")) {
      String[] words = q.split(" ");
      for (String word : words) {
        if (word.contains("max:")) {
          String[] split = word.split(":");
          maxLimit = Integer.parseInt(split[1]);
          break;
        }
      }
    }

    String query = q.replaceAll(" max:[0-9]*", "");
    headers.put("CamelTwitterKeywords",query);
    if (maxLimit > -1) {
      headers.put("CamelTwitterCount",maxLimit);
    }

    return producerTemplate.requestBodyAndHeaders("direct:search", "",  headers);
  }
}