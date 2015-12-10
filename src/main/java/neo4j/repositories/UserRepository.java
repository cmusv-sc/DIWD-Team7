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
public interface UserRepository extends GraphRepository<Paper> {

    @Query("match (u:User) where u.username = {username} return u.username as name, u.password as pass")
    List<Map<String, Object>> findUser(@Param("username") String username);
    
    @Query("match (n:Path{user:{username}}) detach delete n")
    void removelastpath(@Param("username") String username);
    
    @Query("match (u:User{username:{username}}) create (t:Path{user:{username},path:{path}}) create (u)-[r:visit0]->(t)")
    void setlastpath(@Param("username") String username, @Param("path") String path);
    
    @Query("match (u:User{username:{username}}), (u)-[:visit0]->(v) return v.path as path")
    List<Map<String, Object>> getlastpath(@Param("username") String username);
    
    @Query("create (u:User{username:{username} ,password:{password}})")
    void createuser(@Param("username") String username, @Param("password") String password);
    
    @Query("match(n:User) where n.username<> {username} with n optional match (u:User), (u)-[f:FOLLOWS]->(n) where u.username={username} return n.username as name, CASE when f is null then '0' else '1' END  as condition")
    List<Map<String, Object>> getfollowlist(@Param("username") String username);
    
    @Query("match (u:User)-[:FOLLOWS]->(t:User) where t.username={username} return u.username as name")
    List<Map<String, Object>> getfollowers(@Param("username") String username);
    
    @Query("match(u:User{username:{from}}) with u match (t:User{username:{to}}) CREATE UNIQUE (u)-[:FOLLOWS]->(t)")
    void createfollow(@Param("from") String from, @Param("to") String to);
    
    @Query("MATCH (u:User)-[f:FOLLOWS]->(t:User) where u.username = {from} and t.username = {to} delete f")
    void deletefollow(@Param("from") String from, @Param("to") String to);
}


