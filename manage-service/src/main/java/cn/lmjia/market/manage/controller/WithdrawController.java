package cn.lmjia.market.manage.controller;


import cn.lmjia.market.core.entity.Depot;
import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.DepotRows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ROOT')")
public class WithdrawController {

    @Autowired
    private WechatWithdrawRepository wechatWithdrawRepository;

    @Autowired
    private ConversionService conversionService;

    @GetMapping("/withdraw/requestList")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition data() {
        return new DepotRows(time -> conversionService.convert(time, String.class)) {

            @Override
            public Specification<Depot> specification() {
                return null;
            }
        };
    }

}
