package txu.saga.mainapp.dao;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.saga.mainapp.base.AbstractDao;
import txu.saga.mainapp.entity.SagaEntity;


import java.util.List;

@Repository
public class SagaDao extends AbstractDao<SagaEntity> {
    @Transactional
    public SagaEntity save(SagaEntity sagaEntity) {
        if (sagaEntity.getId() == null || sagaEntity.getId() == 0) {
            persist(sagaEntity);
            return sagaEntity;
        } else {
            return merge(sagaEntity);
        }
    }

    @Override
    public SagaEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(SagaEntity accountEntity) {
        accountEntity = merge(accountEntity);
        getEntityManager().remove(accountEntity);
    }

    public SagaEntity getByUsername(String username) {
        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A WHERE username=:username");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("username", username);
        return getSingle(query);
    }

//    public AccountEntity getByEmail(String email) {
//        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A WHERE email=:email");
//        Query query = getEntityManager().createQuery(queryString.toString());
//        query.setParameter("email", email);
//        return getSingle(query);
//    }
//
//    public List<AccountEntity> getWithLimit(int limit) {
//        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A ORDER BY A.createdAt DESC");
//        Query query = getEntityManager().createQuery(queryString.toString());
//        query.setMaxResults(limit);
//        return getRessultList(query);
//
//    }

}
