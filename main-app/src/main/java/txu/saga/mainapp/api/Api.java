package txu.saga.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import txu.saga.mainapp.base.AbstractApi;
import txu.saga.mainapp.dto.*;

import txu.saga.mainapp.entity.SagaEntity;
import txu.saga.mainapp.service.CommandProducer;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/saga")
public class Api extends AbstractApi {

    private final CommandProducer commandProducer;

    @PostMapping("/users")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest req) {
//        String sagaId = UUID.randomUUID().toString();
        SagaEntity saga = commandProducer.sendCreateUserKeycloakCommand(req);
        if (saga == null) {return ResponseEntity.status(400).body(null);}
        CreateUserResponse response = new CreateUserResponse();
        response.setSagaId(saga.getId());
        response.setStatus("PROCESSING");
        return ResponseEntity.accepted().body(response);
    }
}
