package txu.saga.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "SAGA")
public class SagaEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

//    @Getter
//    @Column(name = "SAGA_TYPE")
//    private String type;

    @Getter
    @Column(name = "STATUS")
    private String status;

    @Getter
    @Column(name = "STEP")
    private String currentStep;



//    @ManyToOne
//    @JoinColumn(name = "ROLE_ID")
//    private RoleEntity role;
//
//    @ManyToOne
//    @JoinColumn(name = "DEPARTMENT_ID")
////    @JsonIgnore
//    private DepartmentEntity department;


    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;
//    public String getCreatedAt() {
////        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy] HH:mm:ss"));
//        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updateAt;
//    public String getUpdateAt() {
////        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy] HH:mm:ss"));
//        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

}
