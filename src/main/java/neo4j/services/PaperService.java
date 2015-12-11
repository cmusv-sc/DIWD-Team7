package neo4j.services;

import neo4j.repositories.PaperRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Service
@Transactional
public class PaperService {

    @Autowired PaperRepository paperRepository;

    @SuppressWarnings("rawtypes")
	private Map<String, Object> toD3Format(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("paper"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                		name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                	if (nodes.get(j).get("title").equals(name)) {
                		source = (int) nodes.get(j).get("id");
                		break;
                	} 
                }
                if (source == 0) {
                	author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }
                rels.add(map("source",source,"target",target));
            }
        }
        return map("nodes", nodes, "links", rels);
    }
    
    @SuppressWarnings("rawtypes")
	private Map<String, Object> toD3FormatKrelatedPaper(Iterator<Map<String, Object>> result) {
    	List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("Paper"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                        name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                    if (nodes.get(j).get("title").equals(name)) {
                        source = (int) nodes.get(j).get("id");
                        break;
                    } 
                }
                if (source == 0) {
                    author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }
                rels.add(map("source",source,"target",target));
            }
        }
        return map("nodes", nodes, "links", rels);
    }
    
	private Map<String, Object> toD3FormatSmallWorld(Iterator<Map<String, String>> result) {
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        while (result.hasNext()) {
        	Map<String, String> row = result.next();
        	nodes.add(map6("id", i, "title",row.get("cast"),"label", "author", "cluster", "1", "value", 2, "group", "author"));
        	if (i != 0) {
        		rels.add(map("source", i - 1,"target", i));
        	}
        	i++;
        }

        return map("nodes", nodes, "links", rels);
    }
    
    @SuppressWarnings("rawtypes")
    private Map<String, Object> toD3FormatAuthorNetwork(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("author"),"label", "author", "cluster", "1", "value", 2, "group", "author"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                        name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                    if (nodes.get(j).get("title").equals(name)) {
                        source = (int) nodes.get(j).get("id");
                        break;
                    } 
                }
                if (source == 0) {
                    author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }
                rels.add(map("source",source,"target",target));
            }
        }
        return map("nodes", nodes, "links", rels);
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> toD3FormatDepthNetwork(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            String user1 = row.get("user1").toString();
            String user2 = row.get("user2").toString();
            int source = -1;
            int target = -1;
            
            for (int j = 0; j < nodes.size(); j++) {
            	if (nodes.get(j).get("title").equals(user1)) {
                	source = (int) nodes.get(j).get("id");
                } 
            	if (nodes.get(j).get("title").equals(user2)) {
                	target = (int) nodes.get(j).get("id");
                } 
            }
            if (source == -1){
            	nodes.add(map6("id", i, "title", 
                        user1,"label", "author", "cluster", "2", "value", 1, "group", "author"));
            	source = i;
            	i++;
            }
            if (target == -1){
            	nodes.add(map6("id", i, "title", 
                        user2,"label", "author", "cluster", "2", "value", 1, "group", "author"));
            	target = i;
            	i++;
            }
            rels.add(map("source",source,"target",target));            
        }
        return map("nodes", nodes, "links", rels);
    }

	@SuppressWarnings("rawtypes")
    private Map<String, Object> toD3FormatPaperCitation(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("input"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                        name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                    if (nodes.get(j).get("title").equals(name)) {
                        source = (int) nodes.get(j).get("id");
                        break;
                    } 
                }
                if (source == 0) {
                    author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }
                rels.add(map("source",source,"target",target));
            }
        }
        return map("nodes", nodes, "links", rels);
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> toD3FormatTopCitedPaper(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            // Map<String, String> row = result.next();
            // nodes.add(map6("id", i, "title",row.get("input"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            // if (i != 0) {
            //     rels.add(map("source", i - 1,"target", i));
            // }
            // i++;
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("input"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                        name,"label", "paper", "cluster", "2", "value", 1, "group", "paper");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                    if (nodes.get(j).get("title").equals(name)) {
                        source = (int) nodes.get(j).get("id");
                        break;
                    } 
                }
                if (source == 0) {
                    author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }
                rels.add(map("source",source,"target",target));
            }
        }
        return map("nodes", nodes, "links", rels);
    }

	@SuppressWarnings("unchecked")
	private String[] getTopKeywords(Iterator<Map<String, Object>> result) {
		String[] res = new String[21];
        HashMap<String, Integer> map = new HashMap<String, Integer> ();
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            String title = row.get("paper").toString();
            String title2 = title.replaceAll("[,.]", "").toLowerCase();
            String[] words = title2.split(" ");
            for(int i = 0; i < words.length; i++){
            	String s = words[i];
            	if(map.containsKey(s)){
            		int count = map.get(s);
            		map.put(s, count+1);
            	}
            	else{
            		map.put(s, 1);
            	}
            }
            
        }
        Set<Entry<String, Integer>> entries = map.entrySet();
        Comparator<Entry<String, Integer>> intComparator =
        		new Comparator<Entry<String,Integer>>() 
        		{ @Override 
        	      public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) 
        		{ Integer v1 = e1.getValue();
        		Integer v2 = e2.getValue();
        		return v2.compareTo(v1); } };
        		List<Entry<String, Integer>> listOfEntries = new ArrayList<Entry<String, Integer>>(entries); 
        		Collections.sort(listOfEntries, intComparator);
        int r = 0;
        List<String> blacklist = new ArrayList<String>();
        blacklist.add("and");
        blacklist.add("or");
        blacklist.add("for");
        blacklist.add("the");
        blacklist.add("of");
        blacklist.add("a");
        blacklist.add("an");
        blacklist.add("in");
        blacklist.add("with");
        blacklist.add("on");
        blacklist.add("to");
        blacklist.add("other");
        blacklist.add("by");
        blacklist.add("");
        for (int c = 0; c < listOfEntries.size(); c++){
        	if(r > 19)
        		break;
        	Entry<String, Integer> e = listOfEntries.get(c);
        	if(!blacklist.contains(e.getKey())){
        		res[r]=e.getKey();
        		r++;
            	System.out.println(e.getKey() + " => " + e.getValue());
        	}
        }
        res[20] = "other";
        return res;
    }
	
	
	private Map<String, Object> toD3FormatCategorize(Iterator<Map<String, Object>> result, String[] category) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        for (int c = 0; c < category.length; c++){
        	nodes.add(map6("id", i, "title",category[c],"label", "category", "cluster", "1", "value", 2, "group", "category"));
            i++;
        }
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("paper"),"label", "paper", "cluster", "2", "value", 2, "group", "paper"));
            target = i++;
            int source = -1;
            for (int c = 0; c < category.length-1; c++) {
            	/*if (row.get("paper").toString().contains(category[c])) {
            		source = c;
            	} */
            	if (Pattern.compile(Pattern.quote(category[c]), Pattern.CASE_INSENSITIVE).matcher(row.get("paper").toString()).find()) {
            		source = c;
            		rels.add(map("source",source,"target",target));
        		}
            }
            if (source == -1) {
            	source = category.length-1;
            	rels.add(map("source",source,"target",target));
            }
            
        }
        return map("nodes", nodes, "links", rels);
    }
	
	private Map<String, Object> toD3FormatTimeline(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int source = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            String pname = row.get("paper").toString();
            String year = row.get("year").toString();
            nodes.add(map6("id", i, "title",pname,"label", "paper", "cluster", "1", "value", 1, "group", "paper"));
            source = i++;
            int target = -1;
            for (int j = 0; j < nodes.size(); j++) {
            	if (nodes.get(j).get("title").equals(year)) {
            		target = (int) nodes.get(j).get("id");
            		break;
            	} 
            }
            
            if (target == -1) {
            	nodes.add(map6("id", i, "title",year,"label", "year", "cluster", "2", "value", 2, "group", "year"));
            	target = i++;
            }
        	rels.add(map("source",source,"target",target));
        }
        return map("nodes", nodes, "links", rels);
    }
    
    @SuppressWarnings("rawtypes")
	private Map<String, Object> toAlcFormat(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 1;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("paper"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                		name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                	if (nodes.get(j).get("title").equals(name)) {
                		source = (int) nodes.get(j).get("id");
                		break;
                	} 
                }
                if (source == 0) {
                	author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }

                rels.add(map3("from", source, "to", target, "title", "PUBLISH"));
            }
        }
        return map("nodes", nodes, "edges", rels);
    }
    
    @SuppressWarnings("rawtypes")
	private Map<String, Object> toAlcFormatCoauthor(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 1;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(map6("id", i, "title",row.get("input"),"label", "input", "cluster", "1", "value", 2, "group", "input"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = map5("title", 
                		name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                	if (nodes.get(j).get("title").equals(name)) {
                		source = (int) nodes.get(j).get("id");
                		break;
                	} 
                }
                if (source == 0) {
                	author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }

                rels.add(map3("from", source, "to", target, "title", "CO"));
            }
        }
        return map("nodes", nodes, "edges", rels);
    }

    private Map<String, Object> map(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> result = new HashMap<String,Object>(2);
        result.put(key1,value1);
        result.put(key2,value2);
        return result;
    }
    
    private Map<String, Object> map3(String key1, Object value1, String key2, Object value2, 
    		String key3, Object value3) {
        Map<String, Object> result = new HashMap<String,Object>(3);
        result.put(key1,value1);
        result.put(key2,value2);
        result.put(key3, value3);
        return result;
    }
    
    private Map<String, Object> map5(String key1, Object value1, String key2, Object value2, 
    		String key3, Object value3, String key4, Object value4, String key5, Object value5) {
        Map<String, Object> result = new HashMap<String,Object>(5);
        result.put(key1,value1);
        result.put(key2,value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        return result;
    }
    
    private Map<String, Object> map6(String key1, Object value1, String key2, Object value2, 
    		String key3, Object value3, String key4, Object value4, String key5, Object value5,
    		String key6, Object value6) {
        Map<String, Object> result = new HashMap<String,Object>(6);
        result.put(key1,value1);
        result.put(key2,value2);
        result.put(key3, value3);
        result.put(key4, value4);
        result.put(key5, value5);
        result.put(key6, value6);
        return result;
    }

    public Map<String, Object> graph(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.graph(limit).iterator();
        return toD3Format(result);
    }
    
    public Map<String, Object> graphAlc(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.graph(limit).iterator();
        return toAlcFormat(result);
    }
    
    public Map<String, Object> graphD3(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.graph(limit).iterator();
        return toD3Format(result);
    }
    
    public Map<String, Object> getCoAuthor(String name) {
        Iterator<Map<String, Object>> result = paperRepository.findCoAuthor(name).iterator();
        return toAlcFormatCoauthor(result);
    }
    
    public Map<String, Object> categorize(int from, int to) {
        Iterator<Map<String, Object>> result = paperRepository.findPaperYear(from,to).iterator();
        String[] topkeyword = getTopKeywords(result);
        Iterator<Map<String, Object>> result2 = paperRepository.findPaperYear(from,to).iterator();
        return toD3FormatCategorize(result2, topkeyword);
    }
    
    public Map<String, Object> categorize2(int from, int to, String journal, String keywords) {
    	System.out.println(keywords);
        Iterator<Map<String, Object>> result = paperRepository.findPaperYJK(from,to,journal,keywords).iterator();
        String[] topkeyword = getTopKeywords(result);
        Iterator<Map<String, Object>> result2 = paperRepository.findPaperYJK(from,to,journal,keywords).iterator();
        return toD3FormatCategorize(result2, topkeyword);
    }
    
    public Map<String, Object> timeline(int from, int to, String authors) {
    	System.out.println(authors);
        Iterator<Map<String, Object>> result = paperRepository.findPaperTA(from,to,authors).iterator();
        return toD3FormatTimeline(result);
    }
    
    public Map<String, Object> getAuthorNetwork(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.findAuthorNetwork(limit).iterator();
        return toD3FormatAuthorNetwork(result);
    }
    
    public Map<String, Object> getDepthNetwork(int limit, String name) {
        Iterator<Map<String, Object>> result = paperRepository.findDepthNetwork(limit, name).iterator();
        return toD3FormatDepthNetwork(result);
    }

    public Map<String, Object> proveSmallWorld(String author1, String author2) {
    	Iterator<Map<String, String>> result = paperRepository.smallWorldTheory(author1, author2).iterator();

    	return toD3FormatSmallWorld(result);
    }
    
    public Map<String, Object> getPaperCitation(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.findPaperCitation(limit).iterator();
        return toD3FormatPaperCitation(result);
    }
  
    public Map<String, Object> getKRelated(String[] words, int k) {
    	Iterator<Map<String, Object>> result = paperRepository.topKRelated(words, k).iterator();
        
    	return toD3FormatKrelatedPaper(result);
    }


    public Map<String, Object> getTopCitedPaper(int limit, String journal) {
        Iterator<Map<String, Object>> result = paperRepository.findTopCitedPaper(limit, journal).iterator();
        return toD3FormatTopCitedPaper(result);
    }
}

