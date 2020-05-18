package com.maximsachok.authoridentification.entitys;




import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_expert", schema = "semantic")
public class Author {
    @Id
    @Column(name = "expert_id_tk")
    private Long  expertidtk;

    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private Set<AuthorProject> authorProjects;

    public Set<AuthorProject> getAuthorProjects() {
        return authorProjects;
    }

    public void setAuthorProjects(Set<AuthorProject> authorProjects) {
        this.authorProjects = authorProjects;
    }

    public Long getExpertidtk() {
        return expertidtk;
    }

    public void setExpertidtk(Long expertidtk) {
        this.expertidtk = expertidtk;
    }
}
