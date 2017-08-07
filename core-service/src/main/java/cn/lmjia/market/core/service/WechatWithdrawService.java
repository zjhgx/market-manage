package cn.lmjia.market.core.service;


import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import com.huotu.verification.IllegalVerificationCodeException;
import com.huotu.verification.VerificationType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WechatWithdrawService {

    /**
     * 新创建提现
     *
     * @param payee              收款人
     * @param account            收款账号
     * @param bank               开户行
     * @param mobile             收款人电话
     * @param withdraw           提现金额
     * @param invoice            是否有发票
     * @param logisticsNumber    物流单号
     * @param logisticsCompany   物流公司
     * @return 新创建的提现
     */

    @Transactional
    Withdraw withdrawNew(String payee, String account, String bank, String mobile, String withdraw, String invoice,String logisticsNumber,String logisticsCompany);

    /**
     * @return 用于提现校验的验证码
     */
    default VerificationType withdrawVerificationType() {
        return new VerificationType() {
            @Override
            public int id() {
                return 1;
            }

            @Override
            public String message(String code) {
                return "提现校验短信验证码为：" + code + "；请勿泄露。";
            }
        };
    }

    /**
     * @throws IllegalVerificationCodeException - 验证码无效
     * @see com.huotu.verification.service.VerificationCodeService#verify(String, String, VerificationType)
     */
    @Transactional
    void checkWithdrawCode(String mobile, String code) throws IllegalVerificationCodeException;


    /**
     * @param dealerName      代理商名称
     * @param dealerMobile    代理商手机号
     * @param startDate       可选开始日期
     * @param endDate         可选结束日期
     * @param status          可选状态；如果为{@link WithdrawStatus#EMPTY}表示所有
     * @return 获取数据规格
     */
    Specification<Withdraw> search(String dealerName, String dealerMobile, LocalDate startDate, LocalDate endDate, WithdrawStatus status);

}
