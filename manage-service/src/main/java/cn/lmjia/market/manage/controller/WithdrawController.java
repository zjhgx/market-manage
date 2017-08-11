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
import cn.lmjia.market.core.service.ReadService;
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

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ReadService readService;


    /**
     * 提现申请列表
     */
    @RequestMapping(method = RequestMethod.GET, value = "/withdraw/list")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public RowDefinition withdrawList(@AuthenticationPrincipal Login login, @RequestParam(value = "dealerName", required = false)String dealerName
            , @RequestParam(value = "dealerMobile", required = false) String mobile
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate startDate
            , @DateTimeFormat(pattern = "yyyy-M-d") @RequestParam(required = false) LocalDate endDate
            , WithdrawStatus status,Model model) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        //今日提现
        CriteriaQuery<BigDecimal> sumWithdrawToday = criteriaBuilder.createQuery(BigDecimal.class);
        Root<Withdraw> withdrawRoot = sumWithdrawToday.from(Withdraw.class);
        sumWithdrawToday = sumWithdrawToday.select(criteriaBuilder.sum(withdrawRoot.get("withdrawMoney")))
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(withdrawRoot.get("withdrawTime"),LocalDate.now())
                        )
                );
        //历史提现总额
        CriteriaQuery<BigDecimal> sumWithdrawLogin = criteriaBuilder.createQuery(BigDecimal.class);
        sumWithdrawLogin = sumWithdrawLogin.select(criteriaBuilder.sum(withdrawRoot.get("withdrawMoney")))
                .where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(withdrawRoot.get("payee"),login)
                        )
                );
        //待提现佣金
        model.addAttribute("withdrawBalance",readService.currentBalance(login));

        model.addAttribute("withdrawToday",sumWithdrawToday);
        model.addAttribute("withdrawLogin",sumWithdrawLogin);


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
    public void disable(@AuthenticationPrincipal Login login,@PathVariable("id") long id) {
        wechatWithdrawRepository.getOne(id).setWithdrawAuth(login);
        wechatWithdrawRepository.getOne(id).setWithdrawStatus(WithdrawStatus.success);
    }
}
