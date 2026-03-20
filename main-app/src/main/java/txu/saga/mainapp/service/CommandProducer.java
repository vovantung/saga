package txu.saga.mainapp.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import txu.common.saga.contract.command.CreateHRUserCommand;
import txu.common.saga.contract.command.CreateKeycloakUserCommand;
import txu.common.saga.contract.command.DeleteUserKeycloakCommand;
import txu.saga.mainapp.dto.*;

@Component
@AllArgsConstructor
public class CommandProducer {

    private static final Logger log = LoggerFactory.getLogger(CommandProducer.class);
    private final JmsTemplate jmsTemplate;

    public void sendCreateUserKeycloakCommand(String sagaId, CreateUserRequest req) {
        CreateKeycloakUserCommand cmd = new CreateKeycloakUserCommand();
        cmd.setSagaId(sagaId);
        cmd.setUsername(req.getUsername());
        cmd.setEmail(req.getEmail());
        cmd.setFirstName(req.getFirstName());
        cmd.setLastName(req.getLastName());
        cmd.setDepartmentId(req.getDepartmentId());
        cmd.setRoles(req.getRoles());

        jmsTemplate.convertAndSend("keycloak.create.user.queue", cmd, message -> {
            message.setStringProperty("_type", CreateKeycloakUserCommand.class.getName());
            return message;
        });
    }

    public void sendCreateHRUserCommand(String sagaId, String username, String email, String firstName, String lastName, Integer departmentId, String keycloakUserId) {

        CreateHRUserCommand cmd = new CreateHRUserCommand();
        cmd.setSagaId(sagaId);
        cmd.setKeycloakUserId(keycloakUserId);
        cmd.setEmail(email);
        cmd.setFirstName(firstName);
        cmd.setLastName(lastName);
        cmd.setUsername(username);
        cmd.setDepartmentId(departmentId);

        jmsTemplate.convertAndSend("hr.create.user.queue", cmd, message -> {
            message.setStringProperty("_type", CreateHRUserCommand.class.getName());
            return message;
        });
    }

    public void sendDeleteUserKeycloakCommand(String sagaId, String keycloakUserId) {
        DeleteUserKeycloakCommand cmd = new DeleteUserKeycloakCommand();
        cmd.setSagaId(sagaId);
        cmd.setKeycloakUserId(keycloakUserId);
        jmsTemplate.convertAndSend("keycloak.delete.user.queue", cmd, message -> {
            message.setStringProperty("_type", DeleteUserKeycloakCommand.class.getName());
            return message;
        });
    }
}
