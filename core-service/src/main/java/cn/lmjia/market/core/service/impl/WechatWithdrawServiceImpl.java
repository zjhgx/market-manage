package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Customer;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import cn.lmjia.market.core.service.WechatWithdrawService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WechatWithdrawServiceImpl implements WechatWithdrawService{

    private static final Log log = LogFactory.getLog(WechatWithdrawServiceImpl.class);

    @Autowired
    private WechatWithdrawRepository wechatWithdrawRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private WechatWithdrawService wechatWithdrawService;

    @Override
    public Withdraw withdrawNew(String payee, String account, String bank, String mobile, String withdrawMoney, String invoice,String logisticsNumber,String logisticsCompany ) {

        Invoice invoice1= new Invoice();
        if("0".equals(invoice)){
            invoice1.setCompanyName("利每家科技有限公司");
            invoice1.setTaxnumber("91330108MA28MBU173");
            invoice1.setLogisticsnumber(logisticsNumber);
            invoice1.setLogisticscompany(logisticsCompany);
        }

        Withdraw withdraw = new Withdraw();
        withdraw.setAccount(account);
        withdraw.setBank(bank);
        withdraw.setMobile(mobile);
        withdraw.setWithdrawMoney(BigDecimal.valueOf(Double.valueOf(withdrawMoney)));
        withdraw.setInvoice(invoice1);
        withdraw.setWithdrawTime(LocalDateTime.now());
        withdraw.setWithdrawStatus(WithdrawStatus.checkPending);
        return wechatWithdrawRepository.save(withdraw);
    }

    @Override
    public void checkWithdrawCode(String mobile, String code) throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobile, code,wechatWithdrawService.withdrawVerificationType());
    }

    @Override
    public Specification<Withdraw> search(String dealerName, String dealerMobile, LocalDate startDate, LocalDate endDate, WithdrawStatus status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.isTrue(cb.literal(true));
            if (startDate != null) {
                log.debug("search withdraw with startDate:" + startDate);
                //大于起始时间
                predicate = cb.and(cb.greaterThanOrEqualTo(root.get("withdrawTime").as(String.class),startDate.toString()));
            } else if (endDate != null) {
                log.debug("search order with endDate:" + endDate);
                //晚于结束时间
                predicate = cb.and(cb.lessThanOrEqualTo(root.get("withdrawTime").as(String.class),endDate.toString()));
            }
            if (!StringUtils.isEmpty(dealerMobile)) {
                log.debug("search order with mobile:" + dealerMobile);
                predicate = cb.and(predicate, cb.like(root.get("mobile"), "%" + dealerMobile + "%"));
            }
            if (dealerName != null) {
                predicate = cb.and(predicate, cb.equal(root.get("login").get("guideUser").get("loginName"), dealerName));
            }
            if (status != null && status != WithdrawStatus.EMPTY) {
                predicate = cb.and(predicate, cb.equal(root.get("withdrawStatus"), status));
            }

            return predicate;
        };
    }


}
