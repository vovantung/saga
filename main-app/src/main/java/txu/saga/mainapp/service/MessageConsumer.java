package txu.saga.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import txu.common.saga.contract.command.SagaReplyEvent;
import txu.saga.mainapp.entity.SagaEntity;

import java.nio.charset.StandardCharsets;

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

            SagaEntity saga = sagaService.getById(event.getSagaId());

            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content +"HR_CREATE \t RUNNING" + System.lineSeparator();

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setStatus("RUNNING");
            sagaInstance.setCurrentStep("HR_CREATE");
            sagaService.createOrUpdate(sagaInstance);

        } else if ("HR_CREATE".equals(event.getStep())) {
            SagaEntity saga = sagaService.getById(event.getSagaId());

            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content+ "HR_CREATE \t COMPLETED" + System.lineSeparator();

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setStatus("COMPLETED");
            sagaInstance.setCurrentStep("HR_CREATE");
            sagaService.createOrUpdate(sagaInstance);
            log.info("Created HR User. Hoàn thành saga tạo User trên keycloak và HR.");
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.info("Da xoa keycloak user, sagaId: {}",  event.getSagaId());

            SagaEntity saga = sagaService.getById(event.getSagaId());
            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content+ "KEYCLOAK_DELETE \t COMPENSATED" + System.lineSeparator();


            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setStatus("COMPENSATED");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga hoàn tất delete keycloak"
        }
    }

    private void handleFailure(SagaReplyEvent event) {
        if ("KEYCLOAK_CREATE".equals(event.getStep())) {
            log.warn("Saga tao keycloak user khong thanh cong, sagaId: {}",  event.getSagaId());

            SagaEntity saga = sagaService.getById(event.getSagaId());
            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content + "KEYCLOAK_CREATE \t FAILED" + System.lineSeparator();

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setStatus("FAILED");
            sagaInstance.setCurrentStep("KEYCLOAK_CREATE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga hoàn không can compensation"
        } else if ("HR_CREATE".equals(event.getStep())) {
            log.warn("Saga tao HR user khong thanh cong, chuan bi xoa keycloak user, sagaId: {}, keycloakUserId: {}",  event.getSagaId(), event.getPayload().get("keycloakUserId"));
            commandProducer.sendDeleteUserKeycloakCommand(event.getSagaId() , (String) event.getPayload().get("keycloakUserId"));

            SagaEntity saga = sagaService.getById(event.getSagaId());
            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content+  "KEYCLOAK_DELETE \t COMPENSATING" + System.lineSeparator();

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setStatus("COMPENSATING");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
        } else if ("KEYCLOAK_DELETE".equals(event.getStep())) {
            log.warn("Xoa keycloak user khong thanh cong, sagaId: {}",  event.getSagaId());

            SagaEntity saga = sagaService.getById(event.getSagaId());
            byte[] data = saga.getHistory();
            String content = new String(data, StandardCharsets.UTF_8);
            String history = content+ "KEYCLOAK_DELETE \t FAILED" + System.lineSeparator();

            SagaEntity sagaInstance = new SagaEntity();
            sagaInstance.setId(event.getSagaId());
            sagaInstance.setHistory(history.getBytes(StandardCharsets.UTF_8));
            sagaInstance.setStatus("FAILED");
            sagaInstance.setCurrentStep("KEYCLOAK_DELETE");
            sagaService.createOrUpdate(sagaInstance);
            // Thêm ghi chú "saga không hoàn tất delete keycloak, hoac thu lai cho den khi hoan thanh buoc buf tru"
        }
    }
}

