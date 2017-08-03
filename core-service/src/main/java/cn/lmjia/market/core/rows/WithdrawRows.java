package cn.lmjia.market.core.rows;

import cn.lmjia.market.core.entity.Depot;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract  class WithdrawRows implements RowDefinition<Withdraw> {

    private final Function<LocalDateTime, String> withdrawTimeFormatter;

    public WithdrawRows(Function<LocalDateTime, String> withdrawTimeFormatter) {
        this.withdrawTimeFormatter = withdrawTimeFormatter;
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<Withdraw> root) {
        return Arrays.asList(
             criteriaBuilder.desc(root.get("createTime"))
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
                , Fields.asBasic("serialId")
                , Fields.asBasic("payee")
                , Fields.asBasic("withdrawMoney")
                , Fields.asBasic("withdrawMoney")//剩余可提现金额
                , Fields.asBasic("withdrawSection")//提现区间
                , Fields.asBasic("account")
                , Fields.asBasic("payee")
                , Fields.asBasic("invoice")//发票信息
                , FieldBuilder.asName(Withdraw.class,"withdrawMoney")
                        .addFormat((data,type)
                                -> withdrawTimeFormatter.apply(((LocalDateTime) data)))
                        .build()
                , Fields.asBasic("withdrawStatus")
                , Fields.asBasic("withdrawAuth")
                , Fields.asBasic("withdrawRemark")
                , Fields.asBasic("withdrawdetail")
                , Fields.asBasic("enable")
        );
    }

}
