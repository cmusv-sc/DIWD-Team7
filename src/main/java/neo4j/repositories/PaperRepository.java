package neo4j.repositories;

import neo4j.domain.Paper;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface PaperRepository extends GraphRepository<Paper> {
    Paper findByTitle(@Param("title") String title);

    @Query("MATCH (p:Paper) WHERE p.title =~ ('(?i).*'+{title}+'.*') RETURN p")
    Collection<Paper> findByTitleContaining(@Param("title") String title);

    @Query("MATCH (p:Paper)<-[:PUBLISH]-(a:Author) RETURN p.title as paper, collect(a.name) as cast LIMIT {limit}")
    List<Map<String, Object>> graph(@Param("limit") int limit);
    
    @Query("MATCH (a:Author)-[:CO]->(b:Author) where a.name = {name} return a.name as input, collect(b.name) as cast LIMIT 50")
    List<Map<String, Object>> findCoAuthor(@Param("name") String name);
    
    @Query("MATCH (p:Paper) where p.year > {from} and p.year < {to} return p.title as paper;")
    List<Map<String, Object>> findPaperYear(@Param("from") int from, @Param("to") int to);
    
    @Query("MATCH (a:Author)-[:CO]->(b:Author) return a.name as author, collect(b.name) as cast LIMIT 50")
    List<Map<String, Object>> findAuthorNetwork(@Param("limit") int limit);
    
    @Query("match (a:Author {name: {author1}}), (b:Author {name: {author2}}), p = shortestPath((a)-[*..10]-(b)) with extract(n IN nodes(p)| n.name)"
    		+ " as Authors unwind(Authors) as cast return cast")
    List<Map<String, String>> smallWorldTheory(@Param("author1") String author1, @Param("author2") String author2);
}


