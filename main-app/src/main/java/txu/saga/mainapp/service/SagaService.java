package txu.saga.mainapp.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;
import txu.saga.mainapp.dao.SagaDao;
import txu.saga.mainapp.entity.SagaEntity;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaDao sagaDao;


//    @Value("${ceph.rgw.bucket}")
//    private String bucketName;
//
//    @Value("${ceph.rgw.endpoint}")
//    private String url;

    @Transactional
    public SagaEntity createOrUpdate(SagaEntity sagaEntity) {

//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Add new
        if (sagaEntity.getId() == null || sagaEntity.getId() == 0) {

            if (sagaEntity.getStatus() == null || sagaEntity.getStatus().isEmpty()) {
                throw new BadParameterException("Step is required");
            }

            sagaEntity.setCreatedAt(DateTime.now().toDate());
            sagaEntity.setUpdateAt(DateTime.now().toDate());
            SagaEntity account = null;

            try {
                account = sagaDao.save(sagaEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException(ex.getMessage());
            }
            return account;
        }

        // Update
        SagaEntity sagaInstance = sagaDao.findById(sagaEntity.getId());

        if (sagaInstance != null) {

            if (sagaEntity.getCurrentStep() != null && !sagaEntity.getCurrentStep().isEmpty()) {
                sagaInstance.setCurrentStep(sagaEntity.getCurrentStep());
            }

            if (sagaEntity.getStatus() != null && !sagaEntity.getStatus().isEmpty()) {
                sagaInstance.setStatus(sagaEntity.getStatus());
            }

            sagaInstance.setHistory(sagaEntity.getHistory());

            sagaInstance.setUpdateAt(DateTime.now().toDate());

            try {
                return sagaDao.save(sagaInstance);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save saga instance");
            }
        } else {
            throw new NotFoundException("Saga instance not found");
        }
    }

    //    @Transactional
    public SagaEntity getById(Integer id) {
        SagaEntity saga = sagaDao.findById(id);
        if (saga == null) {
            throw new NotFoundException("User is not found");
        }
        return saga;
    }

    public List<SagaEntity> getLimit(int limit) {
        return sagaDao.getLimit(limit);
    }
//
//    public List<AccountEntity> getWithLimit(int limit) {
//        return accountDao.getWithLimit(limit);
//    }
//
//    public boolean removeByUsername(String username) {
//        AccountEntity account = accountDao.getByUsername(username);
//        if (account == null) {
//            throw new NotFoundException("User is not found");
//        }
//        accountDao.remove(account);
//        return true;
//    }


}
