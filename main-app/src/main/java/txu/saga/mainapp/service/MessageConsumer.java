package txu.saga.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import txu.common.saga.contract.command.SagaReplyEvent;
import txu.saga.mainapp.entity.SagaEntity;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {
    private final CommandProducer commandProducer;
    private final SagaService sagaService;

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

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("PROCESSING");
            sagaInstance.setCurrentStep("HR_CREATE");
            sagaService.createOrUpdate(sagaInstance);

        } else if ("HR_CREATE".equals(event.getStep())) {
            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("COMPLETE");
            sagaInstance.setCurrentStep("HR_CREATE");
            sagaService.createOrUpdate(sagaInstance);
            log.info("Created HR User. Hoàn thành saga tạo User trên keycloak và HR.");
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.info("Da xoa keycloak user, sagaId: {}", (String) event.getPayload().get("sagaId"));
            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("FAILED");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga hoàn tất delete keycloak"
        }
    }

    private void handleFailure(SagaReplyEvent event) {
        if ("KEYCLOAK_CREATE".equals(event.getStep())) {
            log.warn("Saga tao keycloak user khong thanh cong, sagaId: {}", (String) event.getPayload().get("sagaId"));

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("FAILED");
            sagaInstance.setCurrentStep("KEYCLOAK_CREATE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga hoàn không can compensation"
        } else if ("HR_CREATE".equals(event.getStep())) {
            log.warn("Saga tao HR user khong thanh cong, chuan bi xoa keycloak user, sagaId: {}, keycloakUserId: {}", (String) event.getPayload().get("sagaId"), (String) event.getPayload().get("keycloakUserId"));
            commandProducer.sendDeleteUserKeycloakCommand((String) event.getPayload().get("sagaId"), (String) event.getPayload().get("keycloakUserId"));

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("COMPENSATION");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.warn("Xoa keycloak user khong thanh cong, sagaId: {}", (String) event.getPayload().get("sagaId"));

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setStatus("FAILED");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga không hoàn tất delete keycloak"
        }
    }
}

