package org.jboss.as.quickstarts.transactionscoped;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Author paul.robinson@redhat.com 07/12/2012
 */
@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Member(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
