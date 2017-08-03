package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import cn.lmjia.market.core.service.WechatWithdrawService;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.service.VerificationCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
}
