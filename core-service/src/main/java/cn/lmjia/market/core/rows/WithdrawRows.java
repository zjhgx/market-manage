package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Depot;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.service.ReadService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract  class WithdrawRows implements RowDefinition<Withdraw> {

    private final Function<LocalDateTime, String> withdrawTimeFormatter;
    @Autowired
    private ReadService readService;

    /**
     * 要渲染这些记录的身份
     */
    private Login login;

    public WithdrawRows(Login login,Function<LocalDateTime, String> withdrawTimeFormatter) {
        this.login = login;
        this.withdrawTimeFormatter = withdrawTimeFormatter;
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Withdraw> root) {
        return Arrays.asList(
             criteriaBuilder.desc(root.get("withdrawTime"))
        );
    }

    @Override
    public Class<Withdraw> entityClass() {
        return Withdraw.class;
    }

    @Override
    public List<FieldDefinition<Withdraw>> fields() {
        return Arrays.asList(
                Fields.asBasic("id")
                , Fields.asBiFunction("serial",Withdraw::getSerialId)
                , Fields.asFunction("dealer",root -> root.get("payee").get("guideUser").get("loginName"))
                , Fields.asBiFunction("withdrawAmount",Withdraw::getWithdrawAmount)
                , Fields.asBiFunction("withdrawBalance",Withdraw::getWithdrawAmount)//待修改，剩余可提现的余额？？
                , Fields.asFunction("account",root -> root.get("account"))
                , Fields.asFunction("payee",root -> root.get("payee"))
                , Fields.asFunction("invoice",root -> root.get("invoice"))
                , FieldBuilder.asName(Withdraw.class,"withdrawMoney")
                        .addFormat((data,type)
                                -> withdrawTimeFormatter.apply(((LocalDateTime) data)))
                        .build()
                , FieldBuilder.asName(Withdraw.class, "status")
                        .addSelect(root -> root.get("withdrawStatus"))
                        .addFormat((data, type) -> data == null ? null : data.toString())
                        .build()
                , Fields.asFunction("operator",root -> root.get("withdrawAuth"))
                , Fields.asFunction("remark",root -> root.get("withdrawRemark"))
                , FieldBuilder.asName(Withdraw.class, "quickPending")
                        .addSelect(root -> root.get("orderStatus"))
                        .addFormat((data, type) -> {
                            WithdrawStatus withdrawStatus = (WithdrawStatus) data;
                            return login.isManageable() && withdrawStatus == WithdrawStatus.success;
                        })
                        .build()
        );
    }

}