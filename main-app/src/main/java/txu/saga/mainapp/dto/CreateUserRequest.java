package txu.saga.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserRequest {
    String username;
    String email;
    String lastName;
    String firstName;
    Integer departmentId;
    List<String> roles;
}
