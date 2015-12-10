package neo4j.domain;


import org.neo4j.ogm.annotation.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(generator=JSOGGenerator.class)
@NodeEntity
public class Follower {

    private String name;
    private String status; //follow = 1, not follow = 0

    public Follower(String username, String stat) {
    	name = username;
    	status = stat;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}

