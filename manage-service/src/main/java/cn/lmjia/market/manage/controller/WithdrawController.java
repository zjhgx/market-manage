package cn.lmjia.market.manage.controller;


import cn.lmjia.market.core.repository.WechatWithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@PreAuthorize("hasRole('ROOT')")
public class WithdrawController {

    @Autowired
    private WechatWithdrawRepository wechatWithdrawRepository;

}
