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


    @Column(name = "STATUS")
    private String status;


    @Column(name = "CURRENT_STEP")
    private String currentStep;


    @Column(name = "HISTORY")
    private String history;

//    @Getter
//    @Column(name = "PAYLOAD")
//    private byte[] payload;


//    @ManyToOne
//    @JoinColumn(name = "DEPARTMENT_ID")
////    @JsonIgnore
//    private DepartmentEntity department;


    @Column(name = "CREATED_AT")
    private Date createdAt;
//    public String getCreatedAt() {
////        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy] HH:mm:ss"));
//        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }


    @Column(name = "UPDATED_AT")
    private Date updateAt;

    @Column(name = "COMPLETED_AT")
    private Date completedAt;

}
