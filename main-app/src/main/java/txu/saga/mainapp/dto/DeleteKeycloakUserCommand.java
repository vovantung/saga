package txu.saga.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteKeycloakUserCommand {
    private String sagaId;
    private String keycloakUserId;
}
