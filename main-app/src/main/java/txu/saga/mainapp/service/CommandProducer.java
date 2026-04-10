package txu.saga.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import txu.common.saga.contract.command.CreateHRUserCommand;
import txu.common.saga.contract.command.CreateKeycloakUserCommand;
import txu.common.saga.contract.command.DeleteUserKeycloakCommand;
import txu.saga.mainapp.dto.*;
import txu.saga.mainapp.entity.SagaEntity;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
//@AllArgsConstructor
@RequiredArgsConstructor
public class CommandProducer {

    private static final Logger log = LoggerFactory.getLogger(CommandProducer.class);
    private final JmsTemplate jmsTemplate;
    private final SagaService sagaService;

    public SagaEntity sendCreateUserKeycloakCommand(CreateUserRequest req) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String history = LocalDateTime.now().format(formatter) + ": {step: Keycloak_Create, status: Running}" + System.lineSeparator();

        SagaEntity sagaInstance = new SagaEntity();
        sagaInstance.setStatus("RUNNING");
        sagaInstance.setCurrentStep("KEYCLOAK_CREATE");
//        sagaInstance.setPayload(req.toString());
        sagaInstance.setHistory(history);
        SagaEntity saga = sagaService.createOrUpdate(sagaInstance);


        if(saga == null) {
            return null;
        }

        CreateKeycloakUserCommand cmd = new CreateKeycloakUserCommand();
        cmd.setSagaId(saga.getId());
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

        return saga;
    }

    public void sendCreateHRUserCommand(Integer sagaId, String username, String email, String firstName, String lastName, Long departmentId, String keycloakUserId) {

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

    public void sendDeleteUserKeycloakCommand(Integer sagaId, String keycloakUserId) {
        DeleteUserKeycloakCommand cmd = new DeleteUserKeycloakCommand();
        cmd.setSagaId(sagaId);
        cmd.setKeycloakUserId(keycloakUserId);
        jmsTemplate.convertAndSend("keycloak.delete.user.queue", cmd, message -> {
            message.setStringProperty("_type", DeleteUserKeycloakCommand.class.getName());
            return message;
        });
    }
}
