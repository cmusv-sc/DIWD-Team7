package startingPoint.KG_DBLP;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import neo4j.domain.*;
import neo4j.repositories.*;
import neo4j.services.DatasetService;
import neo4j.services.PaperService;

@Configuration
@Import(App.class)
@RestController("/")
public class KnowledgeGraph extends WebMvcConfigurerAdapter {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(KnowledgeGraph.class, args);
    }
    
    @Autowired
    PaperService paperService;
    @Autowired
    DatasetService datasetService;

    @Autowired PaperRepository paperRepository;
    @Autowired DatasetRepository datasetRepository;
    
    @Autowired 
    HandlerInterceptor QueryInterceptor;
    
    /*@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    	System.out.println("-----configurer----");
        configurer.enable();
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        System.out.println("-----viewResolver----");
        resolver.setPrefix("WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }*/

    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(QueryInterceptor).excludePathPatterns("/login","/logout","/error","/register","/createuser");
    }

    @RequestMapping("/graph")
    public Map<String, Object> graph(@RequestParam(value = "limit",required = false) Integer limit) {
    	return paperService.graph(limit == null ? 100 : limit);
    }
    
    @RequestMapping("/graphTest")
    public String graphTest(@RequestParam(value = "limit",required = false) String input) {
    	System.out.println(input);
    	Map<String, Object> map = null;
    	if (input == null || input.length() == 0) {
    		//map = paperService.graphAlc(200);
    		map = paperService.graphD3(200);
    	} else {
    		Integer limit = Integer.parseInt(input);
    		//map = paperService.graphAlc(limit);
    		map = paperService.graphD3(limit);
    	}
    	
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/getcategorize")
    public String categorize(@RequestParam(value = "from",required = false) String from, @RequestParam(value = "to",required = false) String to) {
    	System.out.println(from);
    	System.out.println(to);
    	Map<String, Object> map = null;
    	if (from == null || from.length() == 0) {
    		from = "1900";
    	} 
    	if (from == null || from.length() == 0)  {
    		to = "2100";
    	}
    	map = paperService.categorize(Integer.parseInt(from),Integer.parseInt(to));
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/topKrelated")
    public String topKrelatedPapers(@RequestParam(value = "keywords",required = false) String keywords, @RequestParam(value = "k", required = false) String k) {
    	System.out.println(keywords);
    	keywords = keywords.replace("%2C", " ");
    	keywords = keywords.replace('+', ' ');
    	System.out.println("after replace : " + keywords);
    	String[] words = keywords.split("  ");
    	for (int i = 0; i < words.length; i++) {
    		words[i] = words[i].trim();
    	}
    	int num = Integer.parseInt(k);
    	
    	//System.out.println(num);
    	String json = "";
    	Map<String, Object> map = paperService.getKRelated(words, num);
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/getcategorize2")
    public String categorize2(@RequestParam(value = "from",required = false) String from, @RequestParam(value = "to",required = false) String to, @RequestParam(value = "journal",required = false) String journal, @RequestParam(value = "keywords",required = false) String keywords) {
    	System.out.println(from);
    	System.out.println(to);
    	System.out.println(journal);
    	System.out.println(keywords);
    	Map<String, Object> map = null;
    	if (from == null || from.length() == 0) {
    		from = "1900";
    	} 
    	if (from == null || from.length() == 0)  {
    		to = "2100";
    	}
    	journal = journal.replace('+', ' ');
    	keywords = keywords.replace('+', ' ');
    	String newkey=keywords.replace("%2C", "|");
    	map = paperService.categorize2(Integer.parseInt(from),Integer.parseInt(to),journal,newkey);
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/coAuthor")
    public String getcoAuthor(@RequestParam(value = "name", required = false) String name) {
    	System.out.println("paper sevice name : " + name);
    	name = name.replace('+', ' ');
    	System.out.println("Name: " + name);
    	Map<String, Object> map = paperService.getCoAuthor(name == null ? "unknown" : name);
    	System.out.println("/CoAuthor : " + name);
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/getsmallWorldTheory")
    public String proveSmallWorld(@RequestParam(value = "author1", required = false) String author1,
    				@RequestParam(value = "author2", required = false) String author2) {
    	author1 = author1.replace('+', ' ');
    	author2 = author2.replace('+', ' ');
    	Map<String, Object> map = paperService.proveSmallWorld(author1, author2);
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/graphUserDataset")
    public String graphUserDataset(@RequestParam(value = "limit",required = false) Integer limit) {
    	Map<String, Object> map = datasetService.graphAlc(limit == null ? 1 : limit);
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/getPapers")
    public Collection<Paper> getPapers(String title) {
    	return paperRepository.findByTitleContaining(title);
    	//return paperRepository.findByTitleLike(title);
    }
    
    @RequestMapping("/getPaper")
    public Paper getPaper(String title) {
    	//return movieRepository.findByTitleContaining(title);
    	return paperRepository.findByTitle(title);
    }

    @RequestMapping("/authorNetwork")
    public String getauthornetwork(@RequestParam(value = "limit",required = false) String input) {
        System.out.println(input);
        Map<String, Object> map = null;
        if (input == null || input.length() == 0) {
            //map = paperService.graphAlc(200);
            map = paperService.getAuthorNetwork(200);
        } else {
            Integer limit = Integer.parseInt(input);
            //map = paperService.graphAlc(limit);
            map = paperService.getAuthorNetwork(limit);
        }
        
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert map to JSON string
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        return json;
    }

    @RequestMapping("/depthNetwork")
    public String getdepthnetwork(@RequestParam(value = "limit",required = false) String input, @RequestParam(value = "name",required = false) String name) {
        System.out.println(input);
        Map<String, Object> map = null;
        name = name.replace('+', ' ');
        if (input == null || input.length() == 0) {
            //map = paperService.graphAlc(200);
            map = paperService.getDepthNetwork(200, name);
        } else {
            Integer limit = Integer.parseInt(input);
            //map = paperService.graphAlc(limit);
            map = paperService.getDepthNetwork(limit, name);
        }
        
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert map to JSON string
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        return json;
    }
    
    @RequestMapping("/paperCitation")
    public String getpapercitation(@RequestParam(value = "limit",required = false) String input) {
        System.out.println(input);
        Map<String, Object> map = null;
        if (input == null || input.length() == 0) {
            //map = paperService.graphAlc(200);
            map = paperService.getPaperCitation(200);
        } else {
            Integer limit = Integer.parseInt(input);
            //map = paperService.graphAlc(limit);
            map = paperService.getPaperCitation(limit);
        }
        
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert map to JSON string
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        return json;
    }

    @RequestMapping("/topCitedPaper")
    public String gettopcitedpaper(@RequestParam(value = "limit",required = false) String input, @RequestParam(value = "journal", required = false) String journal) {
        System.out.println(input);
        Map<String, Object> map = null;
        journal = journal.replace('+', ' ');

        if (input == null || input.length() == 0) {
            //map = paperService.graphAlc(200);
            map = paperService.getTopCitedPaper(50, journal);
        } else {
            Integer limit = Integer.parseInt(input);
            
            //map = paperService.graphAlc(limit);
            map = paperService.getTopCitedPaper(limit, journal);
        }
        
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            //convert map to JSON string
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        return json;
    }
    
    @RequestMapping("/getAreaExperts")
    public String getAreaExperts(@RequestParam(value = "keywords",required = false) String keywords) {
    	System.out.println(keywords);
    	keywords = keywords.replace("%2C", " ");
    	keywords = keywords.replace('+', ' ');
    	System.out.println("after replace : " + keywords);
    	String[] words = keywords.split("  ");
    	for (int i = 0; i < words.length; i++) {
    		words[i] = words[i].trim();
    		System.out.println(words[i]);
    	}
    	

    	String json = "";
    	Map<String, Object> map = paperService.getExperts(words);
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
}