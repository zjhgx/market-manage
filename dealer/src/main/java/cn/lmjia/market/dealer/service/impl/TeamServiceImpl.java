package cn.lmjia.market.dealer.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.dealer.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * @author CJ
 */
@Service("teamService")
public class TeamServiceImpl implements TeamService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    private int createQuery(Login login, Integer level) {
        return createQuery(login, level, false);
    }

    private int createQuery(Login login, Integer level, boolean valid) {
        Query query = entityManager.createQuery("select " +
                        "count( distinct relation.to) " +
                        "from LoginRelation as relation " +
                        (valid ? "" : "where relation.to in (select l from Login as l where  l.guideUser=:current) ") +
                        (!valid ? "" : "where relation.to in (select cw.login from Customer as cw where cw.login.guideUser=:current and cw.successOrder=true ) ") +
//                (level == null ? "" : " and min(relation.level)=:level ") +
                        "group by relation.to " +
                        (level == null ? "" : " having min(relation.level)=:level ")
        )
                .setParameter("current", login);
        if (level != null)
            query = query.setParameter("level", level);
        try {
            //noinspection unchecked
            return query.getResultList().stream()
                    .mapToInt(value -> ((Number) value).intValue())
                    .sum();
//            return ((Number) query.getSingleResult()).intValue();
        } catch (NoResultException exception) {
            return 0;
        }
    }

    @Override
    public int customers(Login login) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Customer> customerRoot = countQuery.from(Customer.class);
        final Path<Object> loginPath = customerRoot.get("login");
        countQuery = countQuery.where(criteriaBuilder.equal(loginPath.get("guideUser"), login));
        countQuery = countQuery.select(criteriaBuilder.countDistinct(loginPath));
        try {
            return Math.toIntExact(entityManager.createQuery(countQuery).getSingleResult());
        } catch (NoResultException ignored) {
            return 0;
        }
    }

    @Override
    public int validCustomers(Login login) {
        return createQuery(login, Customer.LEVEL, true);
    }

    @Override
    public int agents(Login login, int level) {
        return createQuery(login, level);
    }

    @Override
    public int all(Login login) {
        return createQuery(login, null);
    }
}
