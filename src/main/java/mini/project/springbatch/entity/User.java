package mini.project.springbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column
    private String name;

    @Column
    private String contact;

    @Column
    private boolean isMale;

    public static User of(String name,
                          String contact,
                          boolean isMale){
        return User.builder()
                .name(name)
                .contact(contact)
                .isMale(isMale)
                .build();
    }
}
