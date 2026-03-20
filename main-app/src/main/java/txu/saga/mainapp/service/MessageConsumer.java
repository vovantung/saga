package txu.saga.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import txu.common.saga.contract.command.SagaReplyEvent;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {
    private final CommandProducer commandProducer;

    @JmsListener(destination = "saga.reply.queue")
//    @Transactional
    public void onSagaReply(SagaReplyEvent event) {
        if (event.isSuccess()) {
            handleSuccess(event);
        } else {
            handleFailure(event);
        }
    }

    private void handleSuccess(SagaReplyEvent event) {
        if ("KEYCLOAK_CREATE".equals(event.getStep())) {
            log.info("Created KeycloakUser, username: {}", (String) event.getPayload().get("username"));
            commandProducer.sendCreateHRUserCommand(event.getSagaId(), (String) event.getPayload().get("username"), (String) event.getPayload().get("email"),
                    (String) event.getPayload().get("firstName"), (String) event.getPayload().get("lastName"), (Integer) event.getPayload().get("departmentId"), (String) event.getPayload().get("keycloakUserId"));
        } else if ("HR_CREATE".equals(event.getStep())) {
            log.info("Created HR User. Hoàn thành saga tạo User trên keycloak và HR.");
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.info("Da xoa keycloak user, sagaId: {}", (String) event.getPayload().get("sagaId"));
        }
    }

    private void handleFailure(SagaReplyEvent event) {
        if ("KEYCLOAK_CREATE".equals(event.getStep())) {
            log.warn("Saga tao keycloak user khong thanh cong, sagaId: {}", (String) event.getPayload().get("sagaId"));
        } else if ("HR_CREATE".equals(event.getStep())) {
            log.warn("Saga tao HR user khong thanh cong, chuan bi xoa keycloak user, sagaId: {}, keycloakUserId: {}", (String) event.getPayload().get("sagaId"), (String) event.getPayload().get("keycloakUserId"));
            commandProducer.sendDeleteUserKeycloakCommand((String) event.getPayload().get("sagaId"), (String) event.getPayload().get("keycloakUserId"));
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.warn("Xoa keycloak user khong thanh cong, sagaId: {}", (String) event.getPayload().get("sagaId"));
        }
    }
}

