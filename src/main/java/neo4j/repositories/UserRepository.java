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
}


