package cn.lmjia.market.core.entity.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.jpa.JpaFunctionUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 提现
 */
@Entity
@Setter
@Getter
public class Withdraw {

    public static final DateTimeFormatter SerialDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.CHINA);

    /**
     * 最长长度
     */
    private static final int MaxDailySerialIdBit = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流水号
     */
    private int serialId;
    /**
     * 收款人
     */
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REFRESH})
    private Login payee;

    /**
     * 收款账号
     */
    @Column(length = 20)
    private String account;

    /**
     * 开户行
     */
    @Column(length = 50)
    private String bank;

    /**
     * 收款人电话
     */
    @Column(length = 20)
    private String mobile;

    /**
     * 提现金额
     */
    @Column(scale = 2, precision = 12)
    private BigDecimal withdrawMoney;

    /**
     * 提现时间
     */
    @Column(columnDefinition = "timestamp")
    private LocalDateTime withdrawTime;

    /**
     * 提现的发票信息
     */
    @OneToOne(cascade={CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REFRESH})
    private Invoice invoice;

    /**
     * 提现的状态
     */
    private WithdrawStatus withdrawStatus;

    /**
     * 备注信息
     */
    @Column(length = 256)
    private String remark;


    /**
     * @param root            实体
     * @param criteriaBuilder cb
     * @return 业务订单号表达式
     * @see #getSerialId()
     */
    public static Expression<String> getSerialId(Path<Withdraw> root, CriteriaBuilder criteriaBuilder) {
        Expression<String> daily = JpaFunctionUtils.LeftPaddingWith(criteriaBuilder, root.get("dailySerialId"), MaxDailySerialIdBit, '0');
        // 然后是日期
        Path<LocalDateTime> withdrawTime = root.get("withdrawTime");
        // https://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html#function_date-format
        // date_format(current_date(),'%Y%m%d');
        Expression<String> year = criteriaBuilder.function("year", String.class, withdrawTime);
        Expression<String> month = JpaFunctionUtils.LeftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("month", String.class, withdrawTime), 2, '0'
        );
        Expression<String> day = JpaFunctionUtils.LeftPaddingWith(
                criteriaBuilder, criteriaBuilder.function("day", String.class, withdrawTime), 2, '0'
        );
        return criteriaBuilder.concat(
                criteriaBuilder.concat(year, month)
                , criteriaBuilder.concat(day, daily)
        );
    }

}
