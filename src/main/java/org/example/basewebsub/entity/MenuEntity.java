package org.example.basewebsub.entity;

import lombok.*;
import org.example.basewebsub.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "menu")
public class MenuEntity extends BaseEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "NAME")
    private String name;

    @Basic
    @Column(name = "PATH")
    private String path;

    @Basic
    @Column(name = "PARENT_ID")
    private Integer parentId;

}
