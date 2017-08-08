package cn.lmjia.market.manage.controller;


import cn.lmjia.market.core.entity.Depot;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.WithdrawStatus;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import cn.lmjia.market.core.rows.DepotRows;
import cn.lmjia.market.core.rows.MainOrderRows;
import cn.lmjia.market.core.rows.WithdrawRows;
import cn.lmjia.market.core.service.WechatWithdrawService;
import cn.lmjia.market.dealer.service.AgentService;
import me.jiangcai.lib.spring.data.AndSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@PreAuthorize("hasRole('ROOT')")
public class WithdrawController {

    @Autowired
    private WechatWithdrawRepository wechatWithdrawRepository;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private WechatWithdrawService wechatWithdrawService;
    @Autowired
    private AgentService agentService;


    /**
     * 提现申请列表
     */
    @RequestMapping(method = RequestMethod.GET, value = "/withdraw/list")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition withdrawList(@AuthenticationPrincipal Login login, @RequestParam(value = "dealerName", required = false)String dealerName
            , @RequestParam(value = "dealerMobile", required = false) String mobile
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate startDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate endDate
            , WithdrawStatus status) {
        return new WithdrawRows(login, t -> conversionService.convert(t,String.class)) {
            @Override
            public Specification<Withdraw> specification() {
                return new AndSpecification<>(
                        wechatWithdrawService.search(dealerName, mobile, startDate, endDate, status),
                        agentService.manageableWithdraw(login)
                );
            }
        };
    }

    @GetMapping("/withdrawDetail")
    public String detail(Long id, Model model) {
        model.addAttribute("currentData", wechatWithdrawRepository.getOne(id));
        return "_withdrawDetail.html";
    }

    @PutMapping("/withdrawList/{id}/pending")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(@PathVariable("id") long id) {
        wechatWithdrawRepository.getOne(id).setWithdrawStatus(WithdrawStatus.success);
    }
}
