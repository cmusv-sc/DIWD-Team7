package neo4j.security;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    //private User user;

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CurrentUser(String name, String password) {
    	super(name,password, AuthorityUtils.createAuthorityList("role-user"));
        //super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        //this.user = user;
    }

    /*public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }*/

}
